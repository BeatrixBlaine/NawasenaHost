package com.nawasenahost.adminpanel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyAmount(LocalDate date, BigDecimal amount) {
}
