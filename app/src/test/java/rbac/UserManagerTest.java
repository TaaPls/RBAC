package rbac;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class UserManagerTest {
    private UserManager userManager = new UserManager();
    private final List<User> users = List.of(
            new User("johndoe", "John Doe", "john@example.com"),
            new User("janesmith", "Jane Smith", "jane@example.com"),
            new User("bobwilson", "Bob Wilson", "bob@example.com"),
            new User("Aga", "Adam Johnson", "aga@gmail.com"),
            new User("will142", "William Fors", "w@yahoo.com")
    );

    @Test
    public void addTest() {
        users.forEach(user -> assertTrue(userManager.exists(user.username())));
        assertEquals(userManager.count(), users.size());
    }

    @BeforeEach
    public void setup() {
        userManager = new UserManager();
        users.forEach(user -> userManager.add(user));
    }

    @Test
    public void removeTest() {
        assertTrue(userManager.remove(users.getFirst()));
    }
    @Test
    public void clearTest() {
        userManager.clear();
        assertEquals(0, userManager.count());
    }
    @Test
    public void addNullTest() {
        assertThrows(IllegalArgumentException.class, () -> userManager.add(null));
    }
    @Test
    public void addExistingTest() {
        assertThrows(IllegalArgumentException.class, () -> userManager.add(users.getFirst()));
    }
    @Test
    public void existsTest() {
        assertTrue(userManager.exists("Aga"));
    }
    @Test
    public void findUsernameTest() {
        assertTrue(userManager.findByUsername("Aga").isPresent());
        assertEquals(users.get(3), userManager.findByUsername("Aga").get());
    }
    @Test
    public void updateTest() {
        userManager.update("Aga", "John Doe", "doejohn@gmail.com");
        assertEquals(new User("Aga", "John Doe", "doejohn@gmail.com"),
                userManager.findByUsername("Aga").get());
    }
    @Test
    public void filterAndSortTest() {
        assertArrayEquals(List.of(users.get(2), users.get(1), users.get(0)).toArray(),
                userManager.findAll(UserFilters.byEmailDomain("example.com"),
                UserSorters.byUsername()).toArray());
    }
}
