package com.nawasenahost.roomservice.repository;

import com.nawasenahost.roomservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findAllByHotelId(int hotelId);

}
