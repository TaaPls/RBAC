package com.taxi.notification.service;

import com.taxi.notification.entity.NotificationTask;
import com.taxi.notification.entity.NotificationTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Scope("prototype")
public class TaskService {

    @Autowired
    NotificationTaskRepository taskRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.SERIALIZABLE)
    public Optional<NotificationTask> fetchTask(int id) {
        try {
            Optional<NotificationTask> taskOpt = taskRepository.findTopByStatus(NotificationTask.NotificationStatus.PENDING);

            if (taskOpt.isPresent()) {
                NotificationTask task = taskOpt.get();

                task.setAttempts(task.getAttempts() + 1);
                task.setStatus(NotificationTask.NotificationStatus.PROCESSING);
                taskRepository.save(task);
                return Optional.of(task);
            }
        } catch (Exception e) {
            System.err.println("Error fetching task for worker: " + id + " " + e.getMessage());
        }
        return Optional.empty();
    }

    @Transactional
    public void confirmTask(NotificationTask task) {
        task.setStatus(NotificationTask.NotificationStatus.SENT);
        taskRepository.save(task);
    }

    @Transactional
    public void failTask(NotificationTask task) {
        if (task.getAttempts() >= 3) {
            task.setStatus(NotificationTask.NotificationStatus.FAILED);
        }
        else {
            task.setStatus(NotificationTask.NotificationStatus.PENDING);
        }
        taskRepository.save(task);
    }
}
