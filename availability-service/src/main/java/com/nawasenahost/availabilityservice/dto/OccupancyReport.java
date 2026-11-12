package com.nawasenahost.availabilityservice.dto;

import java.time.LocalDate;
import java.util.List;

public class OccupancyReport {

    private LocalDate from;
    private LocalDate to;
    private double averageRate;
    private double todayRate;
    private List<OccupancyDay> days;

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public double getAverageRate() {
        return averageRate;
    }

    public void setAverageRate(double averageRate) {
        this.averageRate = averageRate;
    }

    public double getTodayRate() {
        return todayRate;
    }

    public void setTodayRate(double todayRate) {
        this.todayRate = todayRate;
    }

    public List<OccupancyDay> getDays() {
        return days;
    }

    public void setDays(List<OccupancyDay> days) {
        this.days = days;
    }
}
