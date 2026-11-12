package com.nawasenahost.bookingservice.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BookingReport {

    private LocalDate from;
    private LocalDate to;
    private long totalBookings;
    private Map<String, Long> byStatus;
    private Map<String, Long> bySource;
    private List<DailyCount> dailyCheckIns;
    private long arrivalsToday;
    private long departuresToday;

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

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Map<String, Long> getByStatus() {
        return byStatus;
    }

    public void setByStatus(Map<String, Long> byStatus) {
        this.byStatus = byStatus;
    }

    public Map<String, Long> getBySource() {
        return bySource;
    }

    public void setBySource(Map<String, Long> bySource) {
        this.bySource = bySource;
    }

    public List<DailyCount> getDailyCheckIns() {
        return dailyCheckIns;
    }

    public void setDailyCheckIns(List<DailyCount> dailyCheckIns) {
        this.dailyCheckIns = dailyCheckIns;
    }

    public long getArrivalsToday() {
        return arrivalsToday;
    }

    public void setArrivalsToday(long arrivalsToday) {
        this.arrivalsToday = arrivalsToday;
    }

    public long getDeparturesToday() {
        return departuresToday;
    }

    public void setDeparturesToday(long departuresToday) {
        this.departuresToday = departuresToday;
    }
}
