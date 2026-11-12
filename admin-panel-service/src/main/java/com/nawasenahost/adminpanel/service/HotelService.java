package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.HotelForm;
import com.nawasenahost.adminpanel.dto.HotelResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.util.List;

public interface HotelService {

    List<HotelResponse> findAll(SessionData sessionData);
    HotelResponse findById(SessionData sessionData, int hotelId);
    HotelResponse save(SessionData sessionData, HotelForm hotelForm);
    HotelResponse update(SessionData sessionData, int hotelId, HotelForm hotelForm);
    void delete(SessionData sessionData, int hotelId);

}
