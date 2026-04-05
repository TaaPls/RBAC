package rbac;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RBACSystemTest {
    private RBACSystem rbacSystem;

    @BeforeEach
    public void setup() {
        rbacSystem = new RBACSystem();
    }

    @AfterEach
    void tearDown() {
        rbacSystem.getExecutorService().shutdown();
        rbacSystem.getScheduledExecutorService().shutdown();
    }

    @Test
    void initializationTest() {
        rbacSystem.initialize();
        assertEquals("Admin", rbacSystem.getCurrentUser());
        assertEquals(3, rbacSystem.getRoleManager().count());
        assertEquals(1, rbacSystem.getUserManager().count());
        assertEquals(1, rbacSystem.getAssignmentManager().count());
        assertFalse(rbacSystem.getRoleManager().findRolesWithPermission("WRITE", "files").isEmpty());
        //System.out.println(rbacSystem.generateStatistics());
    }
}