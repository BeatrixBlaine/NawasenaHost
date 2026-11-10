package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    List<RoomResponse> findAvailableRooms(SessionData sessionData,
                                          LocalDate checkInDate,
                                          LocalDate checkOutDate);

}
