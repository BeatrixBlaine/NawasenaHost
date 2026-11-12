package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record BookingReport(LocalDate from,
                            LocalDate to,
                            long totalBookings,
                            Map<String, Long> byStatus,
                            Map<String, Long> bySource,
                            List<DailyCount> dailyCheckIns,
                            long arrivalsToday,
                            long departuresToday) {
}
