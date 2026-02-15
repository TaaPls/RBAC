import java.time.LocalDate;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {
    public static AssignmentMetadata now(String assignedBy, String reason) {
        return new AssignmentMetadata(assignedBy, LocalDate.now().toString(), reason);
    }
    String format() {
        return "by "+assignedBy+" at "+assignedAt+"\n"+"Reason: "+ (reason == null ? "No reason" : reason);
    }
}
