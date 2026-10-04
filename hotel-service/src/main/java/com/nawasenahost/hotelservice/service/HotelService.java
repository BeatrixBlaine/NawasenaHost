package com.nawasenahost.hotelservice.service;

import com.nawasenahost.hotelservice.dto.HotelRequest;
import com.nawasenahost.hotelservice.entity.Hotel;

import java.util.List;

public interface HotelService {

    List<Hotel> findAll();
    Hotel findById(int id);
    Hotel save(HotelRequest hotelRequest);
    void deleteById(int id);
    Hotel update(int id, HotelRequest hotelRequest);

}
