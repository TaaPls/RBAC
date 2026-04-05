package rbac;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScheduledExecutorServiceTest {
    private final RBACSystem system = new RBACSystem();

    @BeforeEach
    void setup() {
        system.initialize();
    }

    @AfterEach
    void tearDown() {
        system.getExecutorService().shutdown();
        system.getScheduledExecutorService().shutdown();
    }

    @Test
    void RevokeTest() {
        User user = new User("johndoe", "John Doe", "john@example.com");
        Role role = new Role("Test");
        system.getUserManager().add(user);
        system.getRoleManager().add(role);
        TemporaryAssignment assignment = new TemporaryAssignment(user, role,
                new AssignmentMetadata("Test", LocalDate.now().toString(),
                        "Test"));
        assignment.extend(LocalDate.now().minusMonths(2).toString());
        system.getAssignmentManager().add(assignment);
        assertFalse(system.getAssignmentManager().findByUser(user).isEmpty());
        try {
            Thread.sleep(12000L);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        var future = AuditLog.log(
                "ADD USER",
                "SYSTEM",
                "users",
                "Add new user"
        );
        future.join();
        //AuditLog.printLog();
        assertFalse(AuditLog.getByAction("REVOKE ASSIGNMENT").isEmpty());
        assertFalse(assignment.isActive());
        assertTrue(assignment.isRevoked());
    }
}
