import java.util.Date;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {
    public static AssignmentMetadata now(String assignedBy, String reason) {
        return new AssignmentMetadata(assignedBy, new Date().toString(), reason);
    }
    String format() {
        return "assigned by "+assignedBy+" at "+assignedAt+"\n"+"Reason: "+ (reason == null ? "No reason" : reason);
    }
}
