package com.taxi.trip.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequestDto {
    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Recipient type is required")
    private String recipientType;

    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    @NotBlank(message = "Message is required")
    private String message;

    public NotificationRequestDto(Long tripId, String recipientType, Long recipientId, String message) {
        this.tripId = tripId;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.message = message;
    }

    public Long getTripId() {
        return tripId;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public String getMessage() {
        return message;
    }
}
