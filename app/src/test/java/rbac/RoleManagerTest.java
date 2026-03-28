package rbac;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleManagerTest {
    private RoleManager roleManager;
    private final List<Role> roles = List.of(
            new Role("Manager"),
            new Role("Supervisor"),
            new Role("Developer"),
            new Role("QA engineer"),
            new Role("QA director"),
            new Role("Marketing director")
    );
    private UserManager userManager = new UserManager();
    private final List<User> users = List.of(
            new User("johndoe", "John Doe", "john@example.com"),
            new User("janesmith", "Jane Smith", "jane@example.com"),
            new User("bobwilson", "Bob Wilson", "bob@example.com"),
            new User("Aga", "Adam Johnson", "aga@gmail.com"),
            new User("will142", "William Fors", "w@yahoo.com")
    );

    @BeforeEach
    public void setup() {
        roleManager = new RoleManager();
        roles.forEach(role -> roleManager.add(role));
    }

    @Test
    public void addTest() {
        roles.forEach(role -> assertTrue(roleManager.exists(role.name)));
        assertEquals(roleManager.count(), roles.size());
    }

    @Test
    public void removeTest() {
        assertTrue(roleManager.remove(roles.getFirst()));
        assertEquals(roles.size()-1, roleManager.count());
    }
    @Test
    public void clearTest() {
        roleManager.clear();
        assertEquals(0, roleManager.count());
    }
    @Test
    public void addNullTest() {
        assertThrows(IllegalArgumentException.class, () -> roleManager.add(null));
    }
    @Test
    public void addExistingTest() {
        int count = roleManager.count();
        roleManager.add(roles.getFirst());
        assertEquals(count, roleManager.count());
    }
    @Test
    public void existsTest() {
        assertTrue(roleManager.exists(roles.getLast().name));
    }
    @Test
    public void idFindTest() {
        assertTrue(roleManager.findById(roles.get(3).id).isPresent());
        assertEquals(roles.get(3), roleManager.findById(roles.get(3).id).get());
    }
    @Test
    public void nameFindTest() {
        assertTrue(roleManager.findByName(roles.get(3).name).isPresent());
        assertEquals(roles.get(3), roleManager.findByName(roles.get(3).name).get());
    }
    @Test
    public void filterAndSortTest() {
        assertArrayEquals(List.of(roles.get(5), roles.get(4)).toArray(),
                roleManager.findAll(RoleFilters.byNameContains("director"),
                        RoleSorters.byName()).toArray());
    }
    @Test
    public void permissionAddTest() {
        Role newRole = new Role("newRole");
        Permission firstPermission = new Permission("newPermission", "*resource*", "Very important :thumbsup:");
        newRole.addPermission(firstPermission);
        roleManager.add(newRole);
        Permission secondPermission = new Permission("secondPermission", "secondResource", "Not important");
        roleManager.addPermissionToRole("newRole", secondPermission);

        assertArrayEquals(List.of(firstPermission, secondPermission).toArray(), newRole.getPermissions().toArray());
    }
    @Test
    public void permissionRemoveTest() {
        Role newRole = new Role("newRole");
        Permission firstPermission = new Permission("newPermission", "*resource*", "Very important :thumbsup:");
        Permission secondPermission = new Permission("secondPermission", "secondResource", "Not important");
        newRole.addPermission(firstPermission);
        newRole.addPermission(secondPermission);
        roleManager.add(newRole);
        roleManager.removePermissionFromRole("newRole", firstPermission);

        assertArrayEquals(List.of(secondPermission).toArray(), newRole.getPermissions().toArray());
    }
    @Test
    public void findRolesWithPermissionTest() {
        Role firstRole = new Role("firstRole");
        Role secondRole = new Role("secondRole");
        Permission firstPermission = new Permission("newPermission", "*resource*", "Very important :thumbsup:");
        Permission secondPermission = new Permission("secondPermission", "secondResource", "Not important");
        firstRole.addPermission(firstPermission);
        firstRole.addPermission(secondPermission);
        secondRole.addPermission(secondPermission);
        roleManager.add(firstRole);
        roleManager.add(secondRole);

        var list = List.of(firstRole, secondRole);
        var list1 = roleManager.findRolesWithPermission("secondPermission", "secondResource");
        list.forEach(o -> assertTrue(list1.contains(o)));
        assertEquals(2, list1.size());
    }
}
