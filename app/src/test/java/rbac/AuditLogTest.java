package rbac;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void logTest() {
        int n = AuditLog.getAll().size();
        AuditLog.log(
                "ADD",
                "SYSTEM",
                "users",
                "Add new user"
        );
        assertEquals(n+1, AuditLog.getAll().size());
    }

    @Test
    void getByPerformerTest() {
        AuditLog.log(
                "ADD",
                "SYSTEM",
                "users",
                "Add new user"
        );
        for (AuditEntry entry : AuditLog.getAll()) {
            assertEquals("SYSTEM", entry.performer());
        }
    }

    @Test
    void getByActionTest() {
        AuditLog.log(
                "ADD",
                "SYSTEM",
                "users",
                "Add new user"
        );
        for (AuditEntry entry : AuditLog.getAll()) {
            assertEquals("ADD", entry.action());
        }
    }
}