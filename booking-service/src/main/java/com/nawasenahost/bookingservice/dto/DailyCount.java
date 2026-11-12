package com.nawasenahost.bookingservice.dto;

import java.time.LocalDate;

public class DailyCount {

    private LocalDate date;
    private long count;

    public DailyCount(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDate getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }
}
