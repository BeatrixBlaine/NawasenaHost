package com.nawasenahost.adminpanel.dto;

import java.time.LocalDate;

public record DailyCount(LocalDate date, long count) {
}
