package com.nawasenahost.roomservice.repository;

import com.nawasenahost.roomservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
