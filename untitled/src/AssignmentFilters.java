import java.time.LocalDate;
import java.util.Objects;

public class AssignmentFilters {
    public static AssignmentFilter byUser(User user) {
        return assignment -> Objects.equals(user, assignment.user());
    };
    public static AssignmentFilter byUsername(String username) {
        return assignment -> Objects.equals(assignment.user().username(), username);
    };
    public static AssignmentFilter byRole(Role role) {
        return assignment -> Objects.equals(assignment.role(), role);
    };
    public static AssignmentFilter byRoleName(String roleName) {
        return assignment -> Objects.equals(assignment.role().name, roleName);
    };
    public static AssignmentFilter activeOnly() {
        return assignment -> assignment.isActive();
    };
    public static AssignmentFilter inactiveOnly() {
        return assignment -> !assignment.isActive();
    };
    public static AssignmentFilter byType(String type) {
        return assignment -> Objects.equals(assignment.assignmentType(), type);
    };
    public static AssignmentFilter assignedBy(String username) {
        return assignment -> Objects.equals(assignment.metadata().assignedBy(), username);
    };
    public static AssignmentFilter assignedAfter(String date) {
        return assignment -> {
            return LocalDate.parse(assignment.metadata().assignedAt())
                    .isAfter(LocalDate.parse(date));
        };
    };
    public static AssignmentFilter expiringBefore(String date) {
        return assignment -> {
            if (assignment instanceof TemporaryAssignment o) {
                return LocalDate.parse(o.expiresAt)
                        .isBefore(LocalDate.parse(date));
            }
            return false;
        };
    }
}
