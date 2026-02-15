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
    public boolean isActive() {
        return !revoked;
    }

    @Override
    public String assignmentType() {
        return "PERMANENT";
    }
}
