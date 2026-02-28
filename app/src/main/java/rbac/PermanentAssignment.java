package rbac;

public class PermanentAssignment extends AbstractRoleAssignment{
    boolean revoked = false;

    public PermanentAssignment(User user, Role role, AssignmentMetadata metadata) {
        super(user, role, metadata);
    }

    public  boolean isRevoked() {
        return revoked;
    }
    public void Revoke() {
        revoked = true;
    }

    @Override
    public boolean isActive() {
        return !revoked;
    }

    @Override
    public String assignmentType() {
        return "PERMANENT";
    }
}
