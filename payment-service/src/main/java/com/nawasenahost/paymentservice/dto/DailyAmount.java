package com.nawasenahost.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyAmount {

    private LocalDate date;
    private BigDecimal amount;

    public DailyAmount(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
