package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;

import java.util.List;

public interface RoomService {

    List<RoomResponse> findAll(SessionData sessionData);

}
