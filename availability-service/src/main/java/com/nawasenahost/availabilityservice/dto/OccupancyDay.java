package com.nawasenahost.availabilityservice.dto;

import java.time.LocalDate;

public class OccupancyDay {

    private LocalDate date;
    private int total;
    private int reserved;
    private int available;
    private double rate;

    public OccupancyDay(LocalDate date, int total, int reserved, int available, double rate) {
        this.date = date;
        this.total = total;
        this.reserved = reserved;
        this.available = available;
        this.rate = rate;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTotal() {
        return total;
    }

    public int getReserved() {
        return reserved;
    }

    public int getAvailable() {
        return available;
    }

    public double getRate() {
        return rate;
    }
}
