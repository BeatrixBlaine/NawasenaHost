package com.nawasenahost.roomservice.service;

import com.nawasenahost.roomservice.dto.RoomRequest;
import com.nawasenahost.roomservice.entity.Room;

import java.util.List;

public interface RoomService {

    List<Room> findAll();
    Room findById(int id);
    Room save(RoomRequest roomRequest);
    void deleteById(int id);
    Room update(int id, RoomRequest roomRequest);

}
