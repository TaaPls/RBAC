package rbac;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssignmentManagerTest {
    private final List<Role> roles = List.of(
            new Role("Manager"),
            new Role("Supervisor"),
            new Role("Developer"),
            new Role("QA engineer"),
            new Role("QA director"),
            new Role("Marketing director")
    );
    private final List<User> users = List.of(
            new User("johndoe", "John Doe", "john@example.com"),
            new User("janesmith", "Jane Smith", "jane@example.com"),
            new User("bobwilson", "Bob Wilson", "bob@example.com"),
            new User("Aga", "Adam Johnson", "aga@gmail.com"),
            new User("will142", "William Fors", "w@yahoo.com")
    );
    private AssignmentManager assignmentManager;
    private final List<RoleAssignment> assignments = List.of(
            new TemporaryAssignment(users.getFirst(), roles.getFirst(), new AssignmentMetadata("str1", "2020-01-01", "str1")),
            new PermanentAssignment(users.get(1), roles.get(2), new AssignmentMetadata("str2", "2021-01-01", "str2")),
            new TemporaryAssignment(users.get(1), roles.get(3), new AssignmentMetadata("str3", "2022-01-01", "str3")),
            new PermanentAssignment(users.get(3), roles.get(4), new AssignmentMetadata("str4", "2023-01-01", "str4")),
            new TemporaryAssignment(users.get(4), roles.get(5), new AssignmentMetadata("str5", "2024-01-01", "str4"))
    );
    @BeforeEach
    public void setup() {
        assignmentManager = new AssignmentManager();
        assignments.forEach(roleAssignment -> assignmentManager.add(roleAssignment));
    }
    @Test
    public void addTest() {
        var list = assignmentManager.findAll();
        assignments.forEach(roleAssignment -> assertTrue(list.contains(roleAssignment)));
        assertEquals(assignmentManager.count(), assignments.size());
    }

    @Test
    public void removeTest() {
        assertTrue(assignmentManager.remove(assignments.getFirst()));
        assertEquals(assignments.size()-1, assignmentManager.count());
    }
    @Test
    public void clearTest() {
        assignmentManager.clear();
        assertEquals(0, assignmentManager.count());
    }
    @Test
    public void addNullTest() {
        assertThrows(IllegalArgumentException.class, () -> assignmentManager.add(null));
    }
    @Test
    public void addExistingTest() {
        int n = assignmentManager.count();
        assignmentManager.add(new PermanentAssignment(users.get(1), roles.get(2), new AssignmentMetadata("str2", "2021-01-01", "str2")));
        assertEquals(n, assignmentManager.count());
    }
    @Test
    public void findByIdTest() {
        assertTrue(assignmentManager.findById(assignments.getFirst().assignmentId()).isPresent());
        assertEquals(assignments.getFirst(), assignmentManager.findById(assignments.getFirst().assignmentId()).get());
    }
    @Test
    public void filterAndSortTest() {
        assertArrayEquals(List.of(assignments.get(1), assignments.get(2)).toArray(),
                assignmentManager.findAll(AssignmentFilters.byUsername("janesmith"),
                        AssignmentSorters.byAssignmentDate()).toArray());
    }
    @Test
    public void revokeTest() {
        RoleAssignment newAssignment = new PermanentAssignment(users.get(4), roles.getFirst(), new AssignmentMetadata("str6", "2025-01-01", "str6"));
        assignmentManager.add(newAssignment);
        assignmentManager.revokeAssignment(newAssignment.assignmentId());
        assertFalse(newAssignment.isActive());
    }
    @Test
    public void extendTemporaryTest() {
        RoleAssignment newAssignment = new TemporaryAssignment(users.get(4), roles.getFirst(), new AssignmentMetadata("str6", "2025-01-01", "str6"));
        assignmentManager.add(newAssignment);
        assignmentManager.extendTemporaryAssignment(newAssignment.assignmentId(),
                "2020-01-01");
        assertFalse(newAssignment.isActive());
    }
    @Test
    public void getPermissionsTest() {
        Role firstRole = new Role("firstRole");
        Role secondRole = new Role("secondRole");
        Permission firstPermission = new Permission("newPermission", "*resource*", "Very important :thumbsup:");
        Permission secondPermission = new Permission("secondPermission", "secondResource", "Not important");
        firstRole.addPermission(firstPermission);
        secondRole.addPermission(secondPermission);
        assignmentManager.add(new PermanentAssignment(users.get(3), firstRole, new AssignmentMetadata("str6", "2025-01-01", "str6")));
        assignmentManager.add(new TemporaryAssignment(users.get(3), secondRole, new AssignmentMetadata("str7", "2026-01-01", "str7")));

        var permissions = assignmentManager.getUserPermissions(users.get(3));
        assertTrue(permissions.contains(firstPermission));
        assertTrue(permissions.contains(secondPermission));
        assertEquals(2, permissions.size());
    }
}
