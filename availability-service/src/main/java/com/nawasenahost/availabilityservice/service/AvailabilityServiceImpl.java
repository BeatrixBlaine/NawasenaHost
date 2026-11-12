package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.audit.AuditPublisher;
import com.nawasenahost.availabilityservice.client.RoomClient;
import com.nawasenahost.availabilityservice.entity.RoomInventory;
import com.nawasenahost.availabilityservice.repository.RoomInventoryRepository;
import com.nawasenahost.availabilityservice.dto.AvailabilityRequest;
import com.nawasenahost.availabilityservice.dto.RoomResponse;
import com.nawasenahost.availabilityservice.entity.Availability;
import com.nawasenahost.availabilityservice.entity.AvailabilityStatus;
import com.nawasenahost.availabilityservice.exception.AvailabilityNotFoundException;
import com.nawasenahost.availabilityservice.exception.RoomNotAvailableException;
import com.nawasenahost.availabilityservice.exception.RoomNotFoundException;
import com.nawasenahost.availabilityservice.repository.AvailabilityRepository;
import com.nawasenahost.availabilityservice.security.TenantGuard;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityServiceImpl implements AvailabilityService{

    private final AvailabilityRepository availabilityRepository;
    private final RestClient restClient;
    private final RoomClient roomClient;
    private final TenantGuard tenantGuard;
    private final RoomInventoryRepository roomInventoryRepository;
    private final AuditPublisher auditPublisher;

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
                                   RestClient restClient,
                                   RoomClient roomClient,
                                   TenantGuard tenantGuard,
                                   RoomInventoryRepository roomInventoryRepository,
                                   AuditPublisher auditPublisher) {
        this.availabilityRepository = availabilityRepository;
        this.restClient = restClient;
        this.roomClient = roomClient;
        this.tenantGuard = tenantGuard;
        this.roomInventoryRepository = roomInventoryRepository;
        this.auditPublisher = auditPublisher;
    }

    public RoomResponse validateRoom(int roomId) {
        try {
            return roomClient.getRoom(roomId);
        } catch (FeignException.NotFound e) {
            throw new RoomNotFoundException(
                    "Room with ID " + roomId + " does not exist"
            );
        }
    }

    // reserved can never go below zero or above total
    private void changeReserved(int hotelId, LocalDate date, int delta) {
        for (RoomInventory inventory : roomInventoryRepository.lockRange(hotelId, date, date.plusDays(1))) {
            int reserved = Math.min(Math.max(inventory.getReserved() + delta, 0), inventory.getTotal());
            inventory.setReserved(reserved);
            inventory.setAvailable(Math.max(inventory.getTotal() - reserved, 0));
            roomInventoryRepository.save(inventory);
        }
    }

    @Override
    public boolean isAvailable(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {

        List<Availability> blockingRecords =
                availabilityRepository.findByRoomIdAndStatusInAndDateGreaterThanEqualAndDateLessThan(
                        roomId,
                        List.of(
                                AvailabilityStatus.BOOKED,
                                AvailabilityStatus.MAINTENANCE,
                                AvailabilityStatus.BLOCKED
                        ),
                        checkInDate,
                        checkOutDate
                );

        return blockingRecords.isEmpty();
    }

    @Override
    public List<RoomResponse> findAvailableRooms(int hotelId, LocalDate checkInDate, LocalDate checkOutDate) {

        tenantGuard.assertHotel(hotelId);

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        List<RoomResponse> availableRooms = new ArrayList<>();

        for (RoomResponse room : roomClient.getRooms(hotelId)) {
            if (isAvailable(room.getRoomId(), checkInDate, checkOutDate)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    // the inventory rows of the hotel are locked first, so two guests cannot book the same room at the same time
    // the unique key (room_id, date) is the last safety net and answers 409
    @Override
    @Transactional
    public List<Availability> createBooking(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        RoomResponse room = validateRoom(roomId);

        // lock the inventory rows (nothing is locked when the hotel has no inventory yet)
        roomInventoryRepository.lockRange(room.getHotelId(), checkInDate, checkOutDate);

        // never book the same night twice
        if (!isAvailable(roomId, checkInDate, checkOutDate)) {
            throw new RoomNotAvailableException(
                    "Room with ID " + roomId + " is not available for the selected dates"
            );
        }

        // old CANCELLED rows would collide with the unique key
        availabilityRepository.deleteAll(
                availabilityRepository.findByRoomIdAndDateGreaterThanEqualAndDateLessThan(
                        roomId, checkInDate, checkOutDate));
        availabilityRepository.flush();

        List<Availability> bookedRoom = new ArrayList<>();

        // loop through checkInDate to checkOutDate
        for (LocalDate date = checkInDate;
             date.isBefore(checkOutDate);
             date = date.plusDays(1)) {

            // adding new Data
            Availability tempAvailaibility = new Availability();
            tempAvailaibility.setRoomId(roomId);
            tempAvailaibility.setDate(date);
            tempAvailaibility.setStatus(AvailabilityStatus.BOOKED);

            bookedRoom.add(tempAvailaibility);

            changeReserved(room.getHotelId(), date, 1);
        }

        // saving all data
        availabilityRepository.saveAll(bookedRoom);

        return bookedRoom;
    }

    // called when a booking is cancelled, calling it twice does nothing the second time
    @Override
    @Transactional
    public int releaseBooking(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {

        RoomResponse room = validateRoom(roomId);

        roomInventoryRepository.lockRange(room.getHotelId(), checkInDate, checkOutDate);

        int released = 0;

        for (Availability availability : availabilityRepository
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThan(roomId, checkInDate, checkOutDate)) {

            if (availability.getStatus() == AvailabilityStatus.BOOKED) {
                availabilityRepository.delete(availability);
                changeReserved(room.getHotelId(), availability.getDate(), -1);
                released++;
            }
        }

        return released;
    }

    @Override
    @Transactional
    public int blockRoom(int roomId, LocalDate startDate, LocalDate endDate, AvailabilityStatus status) {

        if (status != AvailabilityStatus.MAINTENANCE && status != AvailabilityStatus.BLOCKED) {
            throw new IllegalArgumentException("Status must be MAINTENANCE or BLOCKED");
        }

        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        RoomResponse room = validateRoom(roomId);
        tenantGuard.assertHotel(room.getHotelId());

        roomInventoryRepository.lockRange(room.getHotelId(), startDate, endDate);

        List<Availability> existing = availabilityRepository
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThan(roomId, startDate, endDate);

        for (Availability availability : existing) {
            if (availability.getStatus() == AvailabilityStatus.BOOKED) {
                throw new RoomNotAvailableException(
                        "Room with ID " + roomId + " already has a booking on " + availability.getDate());
            }
        }

        // replace what is there (an older block or a CANCELLED row)
        availabilityRepository.deleteAll(existing);
        availabilityRepository.flush();

        List<Availability> blocks = new ArrayList<>();

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            blocks.add(new Availability(roomId, date, status));
        }

        availabilityRepository.saveAll(blocks);

        auditPublisher.publish(
                room.getHotelId(),
                "ROOM_" + status,
                "ROOM",
                roomId,
                "Room " + room.getRoomNumber() + " set to " + status + " from " + startDate + " until " + endDate);

        return blocks.size();
    }

    @Override
    @Transactional
    public int unblockRoom(int roomId, LocalDate startDate, LocalDate endDate) {

        RoomResponse room = validateRoom(roomId);
        tenantGuard.assertHotel(room.getHotelId());

        int removed = 0;

        for (Availability availability : availabilityRepository
                .findByRoomIdAndDateGreaterThanEqualAndDateLessThan(roomId, startDate, endDate)) {

            if (availability.getStatus() == AvailabilityStatus.MAINTENANCE
                    || availability.getStatus() == AvailabilityStatus.BLOCKED) {
                availabilityRepository.delete(availability);
                removed++;
            }
        }

        auditPublisher.publish(
                room.getHotelId(),
                "ROOM_UNBLOCKED",
                "ROOM",
                roomId,
                "Room " + room.getRoomNumber() + " opened again from " + startDate + " until " + endDate);

        return removed;
    }

    // everything that makes a room unavailable in the period, used for the availability calendar
    @Override
    public List<Availability> findBlocks(int hotelId, LocalDate startDate, LocalDate endDate) {

        tenantGuard.assertHotel(hotelId);

        List<Integer> roomIds = roomClient.getRooms(hotelId).stream()
                .map(RoomResponse::getRoomId)
                .toList();

        if (roomIds.isEmpty()) {
            return List.of();
        }

        return availabilityRepository
                .findByRoomIdInAndStatusInAndDateGreaterThanEqualAndDateLessThanOrderByDateAsc(
                        roomIds,
                        List.of(AvailabilityStatus.BOOKED, AvailabilityStatus.MAINTENANCE, AvailabilityStatus.BLOCKED),
                        startDate,
                        endDate);
    }

    @Override
    public List<Availability> findAll() {
        return availabilityRepository.findAll();
    }

    @Override
    public Availability findById(int id) {

        Optional<Availability> tempAvailability = availabilityRepository.findById(id);

        Availability theAvailability;

        if(tempAvailability.isPresent()){
            theAvailability = tempAvailability.get();
        } else {
            throw new AvailabilityNotFoundException("Availability not found with id: " + id);
        }

        return theAvailability;
    }

    @Override
    public Availability save(AvailabilityRequest availabilityRequest) {

        validateRoom(availabilityRequest.getRoomId());

        Availability tempAvailability = new Availability();
        tempAvailability.setRoomId(availabilityRequest.getRoomId());
        tempAvailability.setDate(availabilityRequest.getDate());
        tempAvailability.setStatus(availabilityRequest.getStatus());

        return availabilityRepository.save(tempAvailability);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        availabilityRepository.deleteById(id);
    }

    @Override
    public Availability update(int id, AvailabilityRequest availabilityRequest) {

        Availability tempAvailability = findById(id);
        validateRoom(availabilityRequest.getRoomId());

        tempAvailability.setRoomId(availabilityRequest.getRoomId());
        tempAvailability.setDate(availabilityRequest.getDate());
        tempAvailability.setStatus(availabilityRequest.getStatus());

        return availabilityRepository.save(tempAvailability);
    }
}
