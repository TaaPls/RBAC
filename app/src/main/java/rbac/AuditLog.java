package rbac;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AuditLog {
    private static final List<AuditEntry> entries = new ArrayList<>();

    public static void log(String action, String performer, String target, String details) {
        entries.add(new AuditEntry(LocalDateTime.now().toString(),
                action,
                performer,
                target,
                details));
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
}
