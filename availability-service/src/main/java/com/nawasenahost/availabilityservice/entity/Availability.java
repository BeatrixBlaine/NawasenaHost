package com.nawasenahost.availabilityservice.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "availability",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_room_date",
                        columnNames = {"room_id", "date"}
                )
        })
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private int availabilityId;

    @Column(name = "room_id", nullable = false)
    private int roomId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AvailabilityStatus status;

    public Availability(){}

    public Availability(int roomId, LocalDate date, AvailabilityStatus status) {
        this.roomId = roomId;
        this.date = date;
        this.status = status;
    }

    public int getAvailabilityId() {
        return availabilityId;
    }

    public void setAvailabilityId(int availabilityId) {
        this.availabilityId = availabilityId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "availabilityId=" + availabilityId +
                ", roomId=" + roomId +
                ", date=" + date +
                ", status=" + status +
                '}';
    }
}
