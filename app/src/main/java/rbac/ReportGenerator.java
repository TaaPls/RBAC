package rbac;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReportGenerator {
    public static String generateUserReport(UserManager userManager, AssignmentManager
            assignmentManager) {
        StringBuilder str = new StringBuilder();
        userManager.findAll().forEach(user -> str.append(user.username()).append(": ").append(
                String.join(", ", assignmentManager.findByUser(user).
                        parallelStream().map(assignment -> assignment.role().name).toList())).append("\n"));
        return str.toString();
    }
    public static String generateRoleReport(RoleManager roleManager, AssignmentManager
            assignmentManager) {
        StringBuilder str = new StringBuilder();
        roleManager.findAll().forEach(role -> str.append(role.name).append(": ").
                append(assignmentManager.findByFilter(
                        AssignmentFilters.activeOnly().and(AssignmentFilters.byRole(role))).size()).append("\n"));
        return str.toString();
    }
    public static String generatePermissionMatrix(UserManager userManager, AssignmentManager
            assignmentManager) {
        Set<User> users = new HashSet<>(userManager.findAll());
        Set<Permission> permissions = assignmentManager.findAll().
                parallelStream().flatMap(roleAssignment -> roleAssignment.
                        role().getPermissions().stream()).collect(Collectors.toSet());
        Set<String> resources = permissions.stream().map(Permission::resource).collect(Collectors.toSet());

        StringBuilder matrix = new StringBuilder();

        matrix.append(String.format("%-15s", "User"));
        for (String resource : resources) {
            matrix.append(String.format(" | %-20s", resource));
        }
        matrix.append("\n");

        matrix.append("-".repeat(15 + 25 * resources.size()));
        matrix.append("\n");

        for (User user : users) {
            matrix.append(String.format("%-15s", user.username()));

            for (String resource : resources) {
                List<String> permissionTypes = new ArrayList<>();

                for (Permission permission : permissions) {
                    if (permission.resource().equals(resource) &&
                            assignmentManager.userHasPermission(user, permission.name(), resource)) {
                        permissionTypes.add(permission.name());
                    }
                }

                String cellValue;
                if (permissionTypes.isEmpty()) {
                    cellValue = "-";
                } else {
                    cellValue = String.join(", ", permissionTypes);
                }

                matrix.append(String.format(" | %-20s", cellValue));
            }
            matrix.append("\n");
        }

        return matrix.toString();
    }
    public static void exportToFile(String report, String filename) {
        try (FileWriter writer = new FileWriter(filename.concat(".txt"))) {
            writer.write(report);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
