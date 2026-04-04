package rbac;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
public class LoadTest {

    private RBACSystem system;
    private final AtomicInteger userCounter = new AtomicInteger(0);
    private final AtomicInteger roleCounter = new AtomicInteger(0);
    private final AtomicInteger totalOperations = new AtomicInteger(0);
    private final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
    private final Set<String> createdUsernames = ConcurrentHashMap.newKeySet();
    private final Set<String> createdRoleNames = ConcurrentHashMap.newKeySet();

    @BeforeEach
    void setUp() {
        system = new RBACSystem();
        system.initialize();
        userCounter.set(0);
        roleCounter.set(0);
        totalOperations.set(0);
        exceptions.clear();
        createdUsernames.clear();
        createdRoleNames.clear();
    }

    @AfterEach
    void tearDown() {
        system.getExecutorService().shutdown();
        //AuditLog.shutdown();
        if (!exceptions.isEmpty()) {
            System.err.println("Exceptions during test: " + exceptions.size());
            exceptions.forEach(Throwable::printStackTrace);
        }
    }

    @Test
    void loadTest() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<List<String>>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            final int scenario = i % 5;

            futures.add(executor.submit(() -> runScenario(threadId, scenario, operationsPerThread)));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);

        assertTrue(finished, "Test did not finish in time");

        List<String> allResults = new ArrayList<>();
        for (Future<List<String>> future : futures) {
            try {
                allResults.addAll(future.get());
            } catch (ExecutionException e) {
                exceptions.add(e.getCause());
            }
        }

        System.out.println("\n========== LOAD TEST RESULTS ==========");
        System.out.println("Total operations executed: " + totalOperations.get());
        System.out.println("Exceptions: " + exceptions.size());
        System.out.println("Final Users count: " + system.getUserManager().count());
        System.out.println("Final Roles count: " + system.getRoleManager().count());
        System.out.println("Final Assignments count: " + system.getAssignmentManager().count());

        verifyDataIntegrity();

        verifyNoDuplicates();

        assertTrue(exceptions.isEmpty(), "Exceptions occurred during load test: " + exceptions.size());
        assertTrue(system.getUserManager().count() > 0, "No users created");
        assertTrue(system.getRoleManager().count() > 0, "No roles created");
    }

    private List<String> runScenario(int threadId, int scenario, int operations) {
        List<String> results = new ArrayList<>();

        for (int op = 0; op < operations; op++) {
            try {
                totalOperations.incrementAndGet();
                String operationId = String.format("T%d-O%d", threadId, op);

                switch (scenario) {
                    case 0 -> scenarioCreateUsers(threadId, op);
                    case 1 -> scenarioCreateRolesAndPermissions(threadId, op);
                    case 2 -> scenarioAssignRolesAndCheck(threadId, op);
                    case 3 -> scenarioFilterAndSearch(threadId, op);
                    case 4 -> scenarioUpdateAndRevoke(threadId, op);
                }

                results.add(operationId + ": SUCCESS");

//                if (random.nextInt(100) < 5) {
//                    Thread.sleep(1);
//                }

            } catch (Exception e) {
                exceptions.add(e);
                results.add(String.format("T%d-O%d: ERROR - %s", threadId, op, e.getMessage()));
            }
        }

        return results;
    }

    private void scenarioCreateUsers(int threadId, int op) {
        String username = "testuser_" + threadId + "_" + op + "_" + userCounter.incrementAndGet();
        String fullName = "User " + threadId + " " + op;
        String email = username + "@test.com";

        User user = new User(username, fullName, email);
        system.getUserManager().add(user);

        Optional<User> found = system.getUserManager().findByUsername(username);
        assertTrue(found.isPresent(), "User not found after creation: " + username);
        assertEquals(user, found.get());

        createdUsernames.add(username);

        User duplicate = new User(username, "Duplicate", "dup@test.com");
        system.getUserManager().add(duplicate);
        Optional<User> stillOriginal = system.getUserManager().findByUsername(username);
        assertEquals(fullName, stillOriginal.get().fullName(), "Duplicate user overwrote original");
    }

    private void scenarioCreateRolesAndPermissions(int threadId, int op) {
        String roleName = "role_" + threadId + "_" + op + "_" + roleCounter.incrementAndGet();
        Role role = new Role(roleName);
        system.getRoleManager().add(role);

        Permission perm1 = new Permission("READ", "resource_" + threadId, "Read permission");
        Permission perm2 = new Permission("WRITE", "resource_" + threadId, "Write permission");

        system.getRoleManager().addPermissionToRole(roleName, perm1);
        system.getRoleManager().addPermissionToRole(roleName, perm2);

        Optional<Role> found = system.getRoleManager().findByName(roleName);
        assertTrue(found.isPresent(), "Role not found after creation: " + roleName);
        assertTrue(found.get().hasPermission("READ", "resource_" + threadId));
        assertTrue(found.get().hasPermission("WRITE", "resource_" + threadId));

        createdRoleNames.add(roleName);

        Role duplicate = new Role(roleName);
        system.getRoleManager().add(duplicate);
        Optional<Role> stillOriginal = system.getRoleManager().findByName(roleName);
        assertEquals(2, stillOriginal.get().getPermissions().size(),
                "Duplicate role modified permissions");
    }

    private void scenarioAssignRolesAndCheck(int threadId, int op) {
        String username = "assign_user_" + threadId + "_" + op;
        User user = new User(username, "Assign User", username + "@test.com");
        system.getUserManager().add(user);

        String roleName = "assign_role_" + threadId + "_" + op;
        Role role = new Role(roleName);
        Permission perm = new Permission("EXECUTE", "action", "Execute action");
        role.addPermission(perm);
        system.getRoleManager().add(role);

        RoleAssignment assignment = new TemporaryAssignment(user, role,
                new AssignmentMetadata("LoadTest", LocalDate.now().toString(), "Load test assignment"));
        system.getAssignmentManager().add(assignment);

        assertTrue(system.getAssignmentManager().userHasRole(user, role),
                "User should have role: " + username + " -> " + roleName);

        assertTrue(system.getAssignmentManager().userHasPermission(user, "EXECUTE", "action"),
                "User should have permission: EXECUTE on action");
    }

    private void scenarioFilterAndSearch(int threadId, int op) {
        String username = "filter_user_" + threadId + "_" + op;
        User user = new User(username, "Filter User", username + "@test.com");
        system.getUserManager().add(user);

        UserFilter nameFilter = u -> u.username().contains("filter_user_" + threadId);
        List<User> filteredUsers = system.getUserManager().findByFilter(nameFilter);
        assertNotNull(filteredUsers);

        List<User> parallelFiltered = system.getUserManager().findByFilterParallel(nameFilter);
        assertEquals(filteredUsers.size(), parallelFiltered.size(),
                "Sequential and parallel filters returned different sizes");

        Optional<User> emailSearch = system.getUserManager().findByEmail(username + "@test.com");
        assertTrue(emailSearch.isPresent(), "Email search failed");

        RoleFilter roleFilter = RoleFilters.byName("role");
        List<Role> roles = system.getRoleManager().findByFilter(roleFilter);
        assertNotNull(roles);

        List<Role> sortedRoles = system.getRoleManager().findAll(roleFilter,
                RoleSorters.byName());
        assertNotNull(sortedRoles);

        List<RoleAssignment> userAssignments = system.getAssignmentManager().findByUser(user);
        assertNotNull(userAssignments);
    }

    private void scenarioUpdateAndRevoke(int threadId, int op) {
        String username = "update_user_" + threadId + "_" + op;
        String originalFullName = "Original Name";
        String originalEmail = username + "@original.com";
        User user = new User(username, originalFullName, originalEmail);
        system.getUserManager().add(user);

        String newFullName = "Updated Name " + op;
        String newEmail = username + "@updated.com";
        system.getUserManager().update(username, newFullName, newEmail);

        Optional<User> updated = system.getUserManager().findByUsername(username);
        assertTrue(updated.isPresent());
        assertEquals(newFullName, updated.get().fullName());
        assertEquals(newEmail, updated.get().email());
    }

    private void verifyDataIntegrity() {
        Map<String, Long> usernameCounts = new HashMap<>();
        for (User user : system.getUserManager().findAll()) {
            usernameCounts.merge(user.username(), 1L, Long::sum);
        }
        usernameCounts.forEach((username, count) -> {
            if (count > 1) {
                exceptions.add(new AssertionError("Duplicate username found: " + username + " (count: " + count + ")"));
            }
        });

        Map<String, Long> roleNameCounts = new HashMap<>();
        for (Role role : system.getRoleManager().findAll()) {
            roleNameCounts.merge(role.name, 1L, Long::sum);
        }
        roleNameCounts.forEach((roleName, count) -> {
            if (count > 1) {
                exceptions.add(new AssertionError("Duplicate role name found: " + roleName + " (count: " + count + ")"));
            }
        });
    }

    private void verifyNoDuplicates() {
        Set<String> assignmentIds = new HashSet<>();
        for (RoleAssignment assignment : system.getAssignmentManager().findAll()) {
            assertTrue(assignmentIds.add(assignment.assignmentId()),
                    "Duplicate assignmentId found: " + assignment.assignmentId());
        }

        Set<String> roleIds = new HashSet<>();
        for (Role role : system.getRoleManager().findAll()) {
            assertTrue(roleIds.add(role.id), "Duplicate role id found: " + role.id);
        }
    }
}