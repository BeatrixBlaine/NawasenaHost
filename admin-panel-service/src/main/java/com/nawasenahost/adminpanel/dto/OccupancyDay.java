package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;

public record OccupancyDay(LocalDate date, int total, int reserved, int available, double rate) {
}
