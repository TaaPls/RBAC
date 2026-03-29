package rbac;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RBACSystem {
    private final UserManager userManager = new UserManager();
    private final RoleManager roleManager = new RoleManager();
    private final AssignmentManager assignmentManager = new AssignmentManager();
    private String currentUser;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void setCurrentUser(String username) {
        currentUser = username;
    }
    public String getCurrentUser() {
        return currentUser;
    }
    public void initialize() {
        List<Permission> permissions = List.of(
                new Permission("READ", "users", "Read users in system"),
                new Permission("WRITE", "users", "Add, change users in system"),
                new Permission("DELETE", "users", "Delete users from system"),
                new Permission("READ", "files", "Read files in system"),
                new Permission("WRITE", "files", "Add, change files in system"),
                new Permission("DELETE", "files", "Delete files from system")
        );
        List<Role> roles = List.of(
                new Role("Admin"),
                new Role("Manager"),
                new Role("Viewer")
        );
        roles.forEach(roleManager::add);
        permissions.forEach(permission -> roleManager.addPermissionToRole("Admin", permission));
        permissions.subList(0, 3).forEach(permission -> roleManager.addPermissionToRole("Manager", permission));
        roleManager.addPermissionToRole("Viewer", permissions.get(3));
        User admin = new User("Admin", "Administrator", "admin@test.com");
        userManager.add(admin);
        assignmentManager.add(new PermanentAssignment(admin, roles.getFirst(),
                new AssignmentMetadata("SystemInit", LocalDate.now().toString(), "Initialization")));
        currentUser = "Admin";
        AuditLog.log("ADD USER",
                "SYSTEM",
                "User manager",
                "User Admin added");
        AuditLog.log("ADD ROLE",
                "SYSTEM",
                "Role manager",
                "Role Admin added with 6 permissions");
        AuditLog.log("ADD ROLE",
                "SYSTEM",
                "Role manager",
                "Role Manager added with 3 permissions");
        AuditLog.log("ADD ROLE",
                "SYSTEM",
                "Role manager",
                "Role Viewer added with 1 permissions");
        AuditLog.log("ASSIGN ROLE",
                "SYSTEM",
                "Assignment manager",
                "Role Admin assigned to user Admin permanently");
    }
    public String generateStatistics() {
        StringBuilder str = new StringBuilder("Users (");
        str.append(userManager.count()).append("):\n");
        List<String> strings = new ArrayList<>();
        userManager.findAll().forEach(user-> strings.add(user.format()));
        str.append(String.join("\n", strings)).append("\n\nRoles (").append(roleManager.count()).append("):\n");
        strings.clear();
        roleManager.findAll().forEach(role -> strings.add(role.format()));
        str.append(String.join("\n", strings)).append("\n\nAssignments (").append(assignmentManager.count()).append("):\n");
        strings.clear();
        assignmentManager.findAll().forEach(roleAssignment -> {
            if (roleAssignment instanceof PermanentAssignment o) {
                strings.add(o.toString());
            }
            else if (roleAssignment instanceof TemporaryAssignment o) {
                strings.add(o.toString());
            }
        });
        str.append(String.join("\n", strings)).append("\n");
        return str.toString();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public AssignmentManager getAssignmentManager() {
        return assignmentManager;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
