package com.taxi.notification.dto;

import com.taxi.notification.entity.NotificationTask;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequestDto {
    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Recipient type is required")
    private NotificationTask.RecipientType recipientType;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotBlank(message = "Message is required")
    private String message;

    public NotificationRequestDto(Long tripId, NotificationTask.RecipientType recipientType, Long recipientId, String message) {
        this.tripId = tripId;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.message = message;
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
}
