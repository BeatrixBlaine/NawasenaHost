package com.nawasenahost.roomservice.service;

import com.nawasenahost.roomservice.client.AvailabilityClient;
import com.nawasenahost.roomservice.client.HotelClient;
import com.nawasenahost.roomservice.dto.RoomRequest;
import com.nawasenahost.roomservice.entity.Room;
import com.nawasenahost.roomservice.exception.HotelNotFoundException;
import com.nawasenahost.roomservice.exception.RoomNotFoundException;
import com.nawasenahost.roomservice.repository.RoomRepository;
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

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository,
                           RestClient restClient,
                           HotelClient hotelClient,
                           AvailabilityClient availabilityClient) {
        this.roomRepository = roomRepository;
        this.restClient = restClient;
        this.hotelClient = hotelClient;
        this.availabilityClient = availabilityClient;
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

    @Override
    public List<Room> findAll(int hotelId) {
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

        return theRoom;
    }

    @Override
    public Room save(RoomRequest roomRequest) {

        // validate hotelId from hotel-service
        validateHotel(roomRequest.getHotelId());

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
    public void deleteById(int id) {
        findById(id);
        roomRepository.deleteById(id);
    }

    @Override
    public Room update(int id, RoomRequest roomRequest) {

        Room tempRoom = findById(id);

        validateHotel(roomRequest.getHotelId());

        tempRoom.setHotelId(roomRequest.getHotelId());
        tempRoom.setRoomNumber(roomRequest.getRoomNumber());
        tempRoom.setRoomType(roomRequest.getRoomType());
        tempRoom.setPrice(roomRequest.getPrice());
        tempRoom.setCapacity(roomRequest.getCapacity());
        tempRoom.setDescription(roomRequest.getDescription());

        return roomRepository.save(tempRoom);
    }

    @Override
    public long getTotalRoomsByHotelId(int hotelId) {
        return roomRepository.countByHotelId(hotelId);
    }

    @Override
    @Transactional
    public List<Room> initializeRoomInventory(int hotelId, List<RoomRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Room list must not be empty");
        }

        List<Room> savedRooms = new ArrayList<>();

        for (RoomRequest request : requests) {
            request.setHotelId(hotelId);
            savedRooms.add(save(request));
        }

        int totalRooms = Math.toIntExact(roomRepository.countByHotelId(hotelId));

        availabilityClient.initializeInventory(hotelId, totalRooms);

        return savedRooms;
    }
}
