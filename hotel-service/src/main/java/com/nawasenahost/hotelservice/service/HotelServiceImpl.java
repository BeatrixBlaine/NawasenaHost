package com.nawasenahost.hotelservice.service;

import com.nawasenahost.hotelservice.dto.HotelRequest;
import com.nawasenahost.hotelservice.entity.Hotel;
import com.nawasenahost.hotelservice.exception.HotelNotFoundException;
import com.nawasenahost.hotelservice.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceImpl implements HotelService{

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
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

        return hotelRepository.save(tempHotel);
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

        return hotelRepository.save(tempHotel);
    }

    @Override
    public void deleteById(int id) {
        findById(id);
        hotelRepository.deleteById(id);
    }
}
