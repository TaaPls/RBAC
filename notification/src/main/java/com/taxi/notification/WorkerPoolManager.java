package com.taxi.notification;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class WorkerPoolManager {

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ObjectProvider<Worker> workerProvider;
    private ExecutorService executorService;

    private static final int POOL_SIZE = 4;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 30;

    public WorkerPoolManager(ObjectProvider<Worker> workerProvider) {
        this.workerProvider = workerProvider;
    }

    @PostConstruct
    public void startWorkers() {
        executorService = Executors.newFixedThreadPool(POOL_SIZE);

        for (int i = 0; i < POOL_SIZE; i++) {
            Worker worker = workerProvider.getObject(i+1, running);
            executorService.submit(worker);
            System.out.println("Worker submitted to pool: " + i+1);
        }
    }

    @PreDestroy
    public void stopWorkers() {
        running.set(false);
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}