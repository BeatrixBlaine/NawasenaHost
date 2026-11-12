package com.nawasenahost.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// sent by the other services through their AuditClient
public class AuditLogRequest {

    @NotBlank(message = "Actor is required")
    private String actor;

    private String actorRole;

    private int hotelId;

    @NotBlank(message = "Action is required")
    private String action;

    @NotBlank(message = "Entity type is required")
    private String entityType;

    private String entityId;

    @Size(max = 1000, message = "Details must not exceed 1000 characters")
    private String details;

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
