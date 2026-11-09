package com.nawasenahost.availabilityservice.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "room_inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_hotel_date",
                        columnNames = {"hotel_id", "date"}
                )
        })
public class RoomInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomInventoryId;

    @Column(nullable = false)
    private int hotelId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int total;

    @Column(nullable = false)
    private int available;

    @Column(nullable = false)
    private int reserved;

    public RoomInventory(){}

    public RoomInventory(int hotelId, LocalDate date, int total, int available, int reserved) {
        this.hotelId = hotelId;
        this.date = date;
        this.total = total;
        this.available = available;
        this.reserved = reserved;
    }

    public int getRoomInventoryId() {
        return roomInventoryId;
    }

    public void setRoomInventoryId(int roomInventoryId) {
        this.roomInventoryId = roomInventoryId;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }
}
