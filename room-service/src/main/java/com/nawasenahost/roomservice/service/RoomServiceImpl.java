package com.nawasenahost.roomservice.service;

import com.nawasenahost.roomservice.audit.AuditPublisher;
import com.nawasenahost.roomservice.client.AvailabilityClient;
import com.nawasenahost.roomservice.client.HotelClient;
import com.nawasenahost.roomservice.dto.RoomRequest;
import com.nawasenahost.roomservice.entity.Room;
import com.nawasenahost.roomservice.exception.HotelMismatchException;
import com.nawasenahost.roomservice.exception.HotelNotFoundException;
import com.nawasenahost.roomservice.exception.InventorySyncException;
import com.nawasenahost.roomservice.exception.RoomNotFoundException;
import com.nawasenahost.roomservice.repository.RoomRepository;
import com.nawasenahost.roomservice.security.TenantGuard;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;
    private final RestClient restClient;
    private final HotelClient hotelClient;
    private final AvailabilityClient availabilityClient;
    private final TenantGuard tenantGuard;
    private final AuditPublisher auditPublisher;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository,
                           RestClient restClient,
                           HotelClient hotelClient,
                           AvailabilityClient availabilityClient,
                           TenantGuard tenantGuard,
                           AuditPublisher auditPublisher) {
        this.roomRepository = roomRepository;
        this.restClient = restClient;
        this.hotelClient = hotelClient;
        this.availabilityClient = availabilityClient;
        this.tenantGuard = tenantGuard;
        this.auditPublisher = auditPublisher;
    }

    private void validateHotel(int hotelId) {
        try {
            hotelClient.getHotel(hotelId);
        } catch (FeignException.NotFound e) {
            throw new HotelNotFoundException(
                    "Hotel with ID " + hotelId + " does not exist"
            );
        }
    }

    // makes the inventory total in availability-service follow the real room count of the hotel
    private void syncInventory(int hotelId) {

        int totalRooms = (int) roomRepository.countByHotelId(hotelId);

        try {
            availabilityClient.initializeInventory(hotelId, totalRooms);
        } catch (FeignException e) {
            throw new InventorySyncException(
                    "Room inventory for hotel with ID " + hotelId + " could not be synchronized"
            );
        }
    }

    // saves a room without touching the inventory
    private Room saveRoom(RoomRequest roomRequest) {

        Room tempRoom = new Room();
        tempRoom.setHotelId(roomRequest.getHotelId());
        tempRoom.setRoomNumber(roomRequest.getRoomNumber());
        tempRoom.setRoomType(roomRequest.getRoomType());
        tempRoom.setPrice(roomRequest.getPrice());
        tempRoom.setCapacity(roomRequest.getCapacity());
        tempRoom.setDescription(roomRequest.getDescription());

        return roomRepository.save(tempRoom);
    }

    @Override
    public List<Room> findAll(int hotelId) {
        tenantGuard.assertHotel(hotelId);
        return roomRepository.findAllByHotelId(hotelId);
    }

    @Override
    public Room findById(int id) {

        Optional<Room> tempRoom = roomRepository.findById(id);

        Room theRoom;

        if(tempRoom.isPresent()) {
            theRoom = tempRoom.get();
        } else {
            throw new RoomNotFoundException("Room not found with id: " + id);
        }

        tenantGuard.assertHotel(theRoom.getHotelId());

        return theRoom;
    }

    @Override
    @Transactional
    public Room save(RoomRequest roomRequest) {

        tenantGuard.assertHotel(roomRequest.getHotelId());

        // validate hotelId from hotel-service
        validateHotel(roomRequest.getHotelId());

        Room savedRoom = saveRoom(roomRequest);

        // if the sync fails, the saved room is rolled back too
        syncInventory(savedRoom.getHotelId());

        auditPublisher.publish(savedRoom.getHotelId(), "ROOM_CREATED", "ROOM", savedRoom.getRoomId(),
                "Room " + savedRoom.getRoomNumber() + " (" + savedRoom.getRoomType() + ") created");

        return savedRoom;
    }

    @Override
    @Transactional
    public void deleteById(int id) {

        Room theRoom = findById(id);
        roomRepository.deleteById(id);

        syncInventory(theRoom.getHotelId());

        auditPublisher.publish(theRoom.getHotelId(), "ROOM_DELETED", "ROOM", id,
                "Room " + theRoom.getRoomNumber() + " deleted");
    }

    @Override
    @Transactional
    public Room update(int id, RoomRequest roomRequest) {

        // findById already checks the current hotel of the room
        Room tempRoom = findById(id);

        tenantGuard.assertHotel(roomRequest.getHotelId());

        validateHotel(roomRequest.getHotelId());

        int oldHotelId = tempRoom.getHotelId();

        tempRoom.setHotelId(roomRequest.getHotelId());
        tempRoom.setRoomNumber(roomRequest.getRoomNumber());
        tempRoom.setRoomType(roomRequest.getRoomType());
        tempRoom.setPrice(roomRequest.getPrice());
        tempRoom.setCapacity(roomRequest.getCapacity());
        tempRoom.setDescription(roomRequest.getDescription());

        Room updatedRoom = roomRepository.save(tempRoom);

        // room moved to another hotel, so both room counts changed
        if (oldHotelId != updatedRoom.getHotelId()) {
            syncInventory(oldHotelId);
            syncInventory(updatedRoom.getHotelId());
        }

        auditPublisher.publish(updatedRoom.getHotelId(), "ROOM_UPDATED", "ROOM", updatedRoom.getRoomId(),
                "Room " + updatedRoom.getRoomNumber() + " updated, price " + updatedRoom.getPrice());

        return updatedRoom;
    }

    @Override
    public long getTotalRoomsByHotelId(int hotelId) {
        tenantGuard.assertHotel(hotelId);
        return roomRepository.countByHotelId(hotelId);
    }

    @Override
    @Transactional
    public List<Room> initializeRoomInventory(int hotelId, List<RoomRequest> requests) {

        tenantGuard.assertHotel(hotelId);

        // validate hotelId from hotel-service
        validateHotel(hotelId);

        // every room in the batch must belong to the hotel that is being initialized
        for (RoomRequest request : requests) {
            if (request.getHotelId() != hotelId) {
                throw new HotelMismatchException(
                        "Room " + request.getRoomNumber() + " has hotel ID " + request.getHotelId()
                                + ", expected hotel ID " + hotelId
                );
            }
        }

        List<Room> savedRooms = new ArrayList<>();

        for (RoomRequest request : requests) {
            savedRooms.add(saveRoom(request));
        }

        // one sync for the whole batch instead of one per room
        syncInventory(hotelId);

        return savedRooms;
    }
}
