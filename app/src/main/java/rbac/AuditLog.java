package rbac;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AuditLog {
    private static final BlockingQueue<AuditEntry> logQueue = new LinkedBlockingQueue<>();
    private static final List<AuditEntry> entries = new CopyOnWriteArrayList<>();
    private static final ExecutorService logger = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean running = new AtomicBoolean(true);

//    static {
//        logger.submit(() -> {
//            while (running.get()) {
//                try {
//                    AuditEntry entry = logQueue.poll(1, TimeUnit.SECONDS);
//                    if (entry != null) {
//                        entries.add(entry);
//                    }
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//            List<AuditEntry> remaining = new ArrayList<>();
//            logQueue.drainTo(remaining);
//            entries.addAll(remaining);
//        });
//    }

    public static CompletableFuture<Void> log(String action, String performer, String target, String details) {
        return CompletableFuture.runAsync(() -> {
            entries.add(new AuditEntry(LocalDateTime.now().toString(),
                    action,
                    performer,
                    target,
                    details));
        }, logger);
    }
    public static List<AuditEntry> getAll() {
        return new ArrayList<>(entries);
    }
    public static List<AuditEntry> getByPerformer(String performer) {
        return entries.stream().filter(auditEntry -> Objects.equals(auditEntry.performer(), performer)).
                toList();
    }
    public static List<AuditEntry> getByAction(String action) {
        return entries.stream().filter(auditEntry -> Objects.equals(auditEntry.action(), action)).
                toList();
    }
    public static void printLog() {
        StringBuilder str = new StringBuilder();
        entries.forEach(auditEntry -> {
                str.append(auditEntry.timestamp()).append(": ").append(auditEntry.action()).append(" on ").
                        append(auditEntry.target()).append(" by ").append(auditEntry.performer()).
                        append(":\n\t").append(auditEntry.details()).append("\n\n");
        });
        System.out.println(str);
    }
    public static void saveToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename.concat(".txt"))) {
            for (AuditEntry entry : entries) {
                String str = entry.timestamp() + ": " + entry.action() + " on " +
                        entry.target() + " by " + entry.performer() +
                        ":\n\t" + entry.details() + "\n";
                writer.write(str);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static ExecutorService getLogger() {
        return logger;
    }
    public static void shutdown() {
        running.set(false);

        logger.shutdown();
        try {
            if (!logger.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    public static int logSize() {
        return logQueue.size() + entries.size();
    }
}
