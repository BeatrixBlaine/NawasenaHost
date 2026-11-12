package com.nawasenahost.adminpanel.service;

import com.nawasenahost.adminpanel.dto.AvailabilityBlock;
import com.nawasenahost.adminpanel.dto.OccupancyReport;
import com.nawasenahost.adminpanel.dto.RoomResponse;
import com.nawasenahost.adminpanel.dto.SessionData;
import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {

    List<RoomResponse> findAvailableRooms(SessionData sessionData,
                                          LocalDate checkInDate,
                                          LocalDate checkOutDate);
    List<AvailabilityBlock> findBlocks(SessionData sessionData, LocalDate from, LocalDate to);
    OccupancyReport getOccupancy(SessionData sessionData, LocalDate from, LocalDate to);
    void block(SessionData sessionData, int roomId, LocalDate startDate, LocalDate endDate, String status);
    void unblock(SessionData sessionData, int roomId, LocalDate startDate, LocalDate endDate);

}
