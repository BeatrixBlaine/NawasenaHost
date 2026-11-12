package com.nawasenahost.hotelservice.service;

import com.nawasenahost.hotelservice.dto.HotelRequest;
import com.nawasenahost.hotelservice.audit.AuditPublisher;
import com.nawasenahost.hotelservice.entity.Hotel;
import com.nawasenahost.hotelservice.exception.HotelNotFoundException;
import com.nawasenahost.hotelservice.repository.HotelRepository;
import com.nawasenahost.hotelservice.security.TenantGuard;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;
    private final TenantGuard tenantGuard;
    private final AuditPublisher auditPublisher;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            TenantGuard tenantGuard,
                            AuditPublisher auditPublisher) {
        this.hotelRepository = hotelRepository;
        this.tenantGuard = tenantGuard;
        this.auditPublisher = auditPublisher;
    }

    @Override
    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel findById(int id) {

        Optional<Hotel> tempHotel = hotelRepository.findById(id);

        Hotel theHotel;

        if(tempHotel.isPresent()) {
            theHotel = tempHotel.get();
        } else {
            throw new HotelNotFoundException("Hotel not found with id: " + id);
        }

        tenantGuard.assertHotel(theHotel.getId());

        return theHotel;
    }

    @Override
    public Hotel save(HotelRequest hotelRequest) {

        Hotel tempHotel = new Hotel();

        tempHotel.setName(hotelRequest.getName());
        tempHotel.setDescription(hotelRequest.getDescription());
        tempHotel.setAddress(hotelRequest.getAddress());
        tempHotel.setCity(hotelRequest.getCity());
        tempHotel.setCountry(hotelRequest.getCountry());
        tempHotel.setPostalCode(hotelRequest.getPostalCode());
        tempHotel.setPhone(hotelRequest.getPhone());
        tempHotel.setEmail(hotelRequest.getEmail());

        Hotel savedHotel = hotelRepository.save(tempHotel);

        auditPublisher.publish(savedHotel.getId(), "HOTEL_CREATED", "HOTEL", savedHotel.getId(),
                "Hotel " + savedHotel.getName() + " created");

        return savedHotel;
    }

    @Override
    public Hotel update(int id, HotelRequest hotelRequest) {

        Hotel tempHotel = findById(id);

        tempHotel.setName(hotelRequest.getName());
        tempHotel.setDescription(hotelRequest.getDescription());
        tempHotel.setAddress(hotelRequest.getAddress());
        tempHotel.setCity(hotelRequest.getCity());
        tempHotel.setCountry(hotelRequest.getCountry());
        tempHotel.setPostalCode(hotelRequest.getPostalCode());
        tempHotel.setPhone(hotelRequest.getPhone());
        tempHotel.setEmail(hotelRequest.getEmail());

        Hotel savedHotel = hotelRepository.save(tempHotel);

        auditPublisher.publish(savedHotel.getId(), "HOTEL_UPDATED", "HOTEL", savedHotel.getId(),
                "Hotel " + savedHotel.getName() + " updated");

        return savedHotel;
    }

    @Override
    public void deleteById(int id) {
        Hotel theHotel = findById(id);
        hotelRepository.deleteById(id);

        auditPublisher.publish(id, "HOTEL_DELETED", "HOTEL", id,
                "Hotel " + theHotel.getName() + " deleted");
    }
}
