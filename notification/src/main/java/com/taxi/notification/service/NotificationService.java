package com.taxi.notification.service;

import com.taxi.notification.dto.NotificationRequestDto;
import com.taxi.notification.dto.NotificationResponseDto;
import com.taxi.notification.entity.NotificationTask;
import com.taxi.notification.entity.NotificationTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.cfg.MapperBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationTaskRepository taskRepository;

    public NotificationService(NotificationTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public NotificationResponseDto createNotification(NotificationRequestDto request) {

        NotificationTask task = new NotificationTask(
                request.getTripId(),
                request.getRecipientType(),
                request.getRecipientId(),
                request.getMessage()
        );

        task = taskRepository.save(task);

        return mapToResponse(task);
    }


    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getTripNotifications(Long tripId) {
        return taskRepository.getAllByTripId(tripId).stream().map(this::mapToResponse).toList();
    }

    NotificationResponseDto mapToResponse(NotificationTask task) {
        return new NotificationResponseDto(
                task.getId(),
                task.getTripId(),
                task.getRecipientType(),
                task.getRecipientId(),
                task.getMessage(),
                task.getStatus(),
                task.getAttempts(),
                task.getCreatedAt()
        );
    }
}