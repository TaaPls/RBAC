package com.taxi.notification;

import com.taxi.notification.entity.NotificationTask;
import com.taxi.notification.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Scope("prototype")
public class Worker implements Runnable {

    private final int workerId;

    private final AtomicBoolean running;

    @Autowired
    private TaskService taskService;

    private final Random random = new Random();

    public Worker(int workerId, AtomicBoolean running) {
        this.workerId = workerId;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Optional<NotificationTask> taskOpt = taskService.fetchTask(workerId);

                if (taskOpt.isPresent()) {
                    processTask(taskOpt.get());
                } else {
                    System.out.println("No tasks for worker: " + workerId);
                    Thread.sleep(1000 + random.nextInt(2000));
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Worker interrupted: " + workerId);
                break;
            } catch (Exception e) {
                System.err.println("Worker unexpected error: " + workerId + "error: " + e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        System.out.println("Worker stopped: " + workerId);
    }



    private void processTask(NotificationTask task) {
        System.out.println("Worker " + workerId + " is processing task " + task.getId() + " for trip " + task.getTripId());

        try {
            sendNotification(task);
            taskService.confirmTask(task);
            System.out.println("Worker " + workerId + " successfully processed task " + task.getId());

        } catch (Exception e) {
            System.err.println("Worker " + workerId + " failed to process task " + task.getId());
            taskService.failTask(task);
        }
    }

    private void sendNotification(NotificationTask task) throws Exception {
        long delay = 500 + random.nextInt(2000);
        Thread.sleep(delay);

        if (random.nextInt(100) < 10) {
            throw new RuntimeException("Simulated network error");
        }

        String str = "\n\n\n\n\n" + "-".repeat(50) +
                "\n" + task.getMessage() + "\n" +
                "-".repeat(50) + "\n\n\n\n\n";
        System.out.println(str);
    }
}