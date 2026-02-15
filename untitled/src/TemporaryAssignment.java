import java.time.LocalDate;

public class TemporaryAssignment extends AbstractRoleAssignment {
    String expiresAt;
    boolean autoRenew;

    public TemporaryAssignment(User user, Role role, AssignmentMetadata metadata) {
        super(user, role, metadata);
    }

    void extend(String newExpirationDate) {
        expiresAt = newExpirationDate;
    }
    boolean isExpired() {
        return LocalDate.now().isBefore(LocalDate.parse(expiresAt));
    }
    String getTimeRemaining() {
        return LocalDate.now().datesUntil(LocalDate.parse(expiresAt)).toString();
    }

    @Override
    public boolean isActive() {
        return !isExpired();
    }

    @Override
    public String assignmentType() {
        return "TEMPORARY";
    }

    @Override
    public String summary() {
        return "["+assignmentType()+"] "+role.name+" assigned to "+user.username()+" "+metadata+"\n"
                +"Status: "+(isActive() ? "ACTIVE" : "INACTIVE")+"\nExpires at: "+expiresAt;
    }
}
