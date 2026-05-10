package com.taxi.notification.dto;

import com.taxi.notification.entity.NotificationTask;
import java.time.LocalDateTime;

public class NotificationResponseDto {
    private Long id;
    private Long tripId;
    private NotificationTask.RecipientType recipientType;
    private Long recipientId;
    private String message;
    private NotificationTask.NotificationStatus status;
    private int attempts;
    private LocalDateTime createdAt;

    public NotificationResponseDto(Long id, Long tripId, NotificationTask.RecipientType recipientType, Long recipientId, String message, NotificationTask.NotificationStatus status, int attempts, LocalDateTime createdAt) {
        this.id = id;
        this.tripId = tripId;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.message = message;
        this.status = status;
        this.attempts = attempts;
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public Long getTripId() {
        return tripId;
    }

    public NotificationTask.RecipientType getRecipientType() {
        return recipientType;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public String getMessage() {
        return message;
    }

    public NotificationTask.NotificationStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}