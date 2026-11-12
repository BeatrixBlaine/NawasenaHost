package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;

public record AvailabilityBlock(int availabilityId, int roomId, LocalDate date, String status) {
}
