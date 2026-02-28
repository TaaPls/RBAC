package rbac;

import java.time.LocalDate;

public class TemporaryAssignment extends AbstractRoleAssignment {
    String expiresAt = String.valueOf(LocalDate.now().plusMonths(1));
    boolean autoRenew;

    public TemporaryAssignment(User user, Role role, AssignmentMetadata metadata) {
        super(user, role, metadata);
    }

    void extend(String newExpirationDate) {
        expiresAt = newExpirationDate;
    }
    boolean isExpired() {
        return LocalDate.now().isAfter(LocalDate.parse(expiresAt));
        //System.out.println(LocalDate.now());
        //return true;
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
