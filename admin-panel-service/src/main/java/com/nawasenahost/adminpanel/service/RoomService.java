package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.RoomForm;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.util.List;

public interface RoomService {

    List<RoomResponse> findAll(SessionData sessionData);
    RoomResponse findById(SessionData sessionData, int roomId);
    RoomResponse save(SessionData sessionData, RoomForm roomForm);
    RoomResponse update(SessionData sessionData, int roomId, RoomForm roomForm);
    void delete(SessionData sessionData, int roomId);

}
