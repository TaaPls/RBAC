import java.util.Objects;
import java.util.UUID;

public abstract class AbstractRoleAssignment implements RoleAssignment {
    String assignmentId;
    User user;
    Role role;
    AssignmentMetadata metadata;

    public AbstractRoleAssignment(User user, Role role, AssignmentMetadata metadata) {
        this.assignmentId = UUID.randomUUID().toString();
        this.user = user;
        this.role = role;
        this.metadata = metadata;
    }
    public String summary() {
        return "["+assignmentType()+"] "+role.name+" assigned to "+user.username()+" "+metadata+"\n"
                +"Status: "+(isActive() ? "ACTIVE" : "INACTIVE");
    }
    public abstract boolean isActive();
    public abstract String assignmentType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRoleAssignment abstractRoleAssignment = (AbstractRoleAssignment) o;
        return Objects.equals(assignmentId, abstractRoleAssignment.assignmentId);
    }

    @Override
    public String assignmentId() {
        return assignmentId;
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public Role role() {
        return role;
    }

    @Override
    public AssignmentMetadata metadata() {
        return metadata;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentId);
    }

    @Override
    public String toString() {
        return summary();
    }
}
