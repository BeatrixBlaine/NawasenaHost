package com.nawasenahost.availabilityservice.service;

import com.nawasenahost.availabilityservice.client.RoomClient;
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

    @Autowired
    public AvailabilityServiceImpl(AvailabilityRepository availabilityRepository,
                                   RestClient restClient,
                                   RoomClient roomClient,
                                   TenantGuard tenantGuard) {
        this.availabilityRepository = availabilityRepository;
        this.restClient = restClient;
        this.roomClient = roomClient;
        this.tenantGuard = tenantGuard;
    }

    public void validateRoom(int roomId) {
        try {
            roomClient.getRoom(roomId);
        } catch (FeignException.NotFound e) {
            throw new RoomNotFoundException(
                    "Room with ID " + roomId + " does not exist"
            );
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

    @Override
    @Transactional
    public List<Availability> createBooking(int roomId, LocalDate checkInDate, LocalDate checkOutDate) {

        // never book the same night twice
        if (!isAvailable(roomId, checkInDate, checkOutDate)) {
            throw new RoomNotAvailableException(
                    "Room with ID " + roomId + " is not available for the selected dates"
            );
        }

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
        }

        // saving all data
        availabilityRepository.saveAll(bookedRoom);

        return bookedRoom;
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
