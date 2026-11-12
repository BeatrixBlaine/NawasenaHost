package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;
import java.util.List;

public record OccupancyReport(LocalDate from,
                              LocalDate to,
                              double averageRate,
                              double todayRate,
                              List<OccupancyDay> days) {
}
