import java.time.LocalDate;
import java.util.Comparator;

public class AssignmentSorters {
    public static Comparator<RoleAssignment> byUsername() {
        return (o1, o2) -> o1.user().username().
                compareToIgnoreCase(o2.user().username());
    }
    public static Comparator<RoleAssignment> byRoleName() {
        return (o1, o2) -> o1.role().name.
                compareToIgnoreCase(o2.role().name);
    }
    public static Comparator<RoleAssignment> byAssignmentDate() {
        return Comparator.comparing(o -> LocalDate.parse(o.metadata().assignedAt()));
    }
}
