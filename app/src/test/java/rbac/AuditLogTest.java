package rbac;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @AfterAll
    static void after() {
        AuditLog.shutdown();
    }

    @Test
    void logTest() {
        int n = AuditLog.logSize();
        var future = AuditLog.log(
                "ADD",
                "SYSTEM",
                "users",
                "Add new user"
        );
        future.join();
        assertEquals(n+1, AuditLog.getAll().size());
    }

    @Test
    void getByPerformerTest() {
        var future = AuditLog.log(
                "ADD",
                "SYSTEM",
                "users",
                "Add new user"
        );
        future.join();
        for (AuditEntry entry : AuditLog.getAll()) {
            assertEquals("SYSTEM", entry.performer());
        }
    }

    @Test
    void getByActionTest() {
        var future = AuditLog.log(
                "ADD",
                "SYSTEM",
                "users",
                "Add new user"
        );
        future.join();
        for (AuditEntry entry : AuditLog.getAll()) {
            assertEquals("ADD", entry.action());
        }
    }
}