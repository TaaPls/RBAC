package rbac;

import java.time.LocalDate;
import java.util.*;

public class CommandRegistry {
    public static CommandParser getParser() {
        CommandParser commandParser = new CommandParser();
        commandParser.registerCommand("user-list",
                "List of all users",
                (Scanner scanner, RBACSystem system) -> {
                    System.out.println("field value or empty");
                    String field, value, input;
                    scanner.nextLine();
                    input = scanner.nextLine();
                    if (input == "") {
                        UserManager manager = system.getUserManager();
                        System.out.println("Users (" + manager.count() + "):");
                        manager.findAll().forEach(user-> System.out.println(user.format()));
                        return;
                    }
                    field = input.split("\\s+")[0];
                    value = input.split("\\s+")[1];
                    UserFilter filter;
                    switch (field) {
                        case "username":
                            filter = UserFilters.byUsernameContains(value);
                            break;
                        case "email":
                            filter = UserFilters.byEmail(value);
                            break;
                        case "email-domain":
                            filter = UserFilters.byEmailDomain(value);
                            break;
                        case "fullname":
                            filter = UserFilters.byFullNameContains(value);
                            break;
                        default:
                            System.out.println("field \"" + field + "\" does not exist");
                            return;
                    }
                    system.getUserManager().findAll(filter, UserSorters.byUsername()).
                            forEach(user-> System.out.println(user.format()));
                });
        commandParser.registerCommand("user-create",
                "Add new user",
                (Scanner scanner, RBACSystem system) -> {
                    String username, fullname, email;
                    User user;
                    System.out.println("Username:");
                    username = scanner.next();
                    System.out.println("Full name:");
                    scanner.nextLine();
                    fullname = scanner.nextLine();
                    System.out.println("Email:");
                    email = scanner.next();
                    try {
                        user = User.validate(username, fullname, email);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                    system.getUserManager().add(user);
                    System.out.println("User added successfully");
                });
        commandParser.registerCommand("user-view",
                "Show user info",
                (Scanner scanner, RBACSystem system) -> {
                    String username;
                    System.out.println("Enter username");
                    username = scanner.next();
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    var assignments = system.getAssignmentManager().findByFilter(
                            AssignmentFilters.byUser(user.get()).and(AssignmentFilters.activeOnly()));
                    System.out.println(user.get().format());
                    System.out.println("Roles ("+assignments.size()+"):");
                    assignments.forEach(roleAssignment -> System.out.println(roleAssignment.role().format()));
                });
        commandParser.registerCommand("user-update",
                "Update user data",
                (Scanner scanner, RBACSystem system) -> {
                    String username, newFullname, newEmail;
                    System.out.println("Enter username");
                    username = scanner.next();
                    System.out.println("Enter new full name");
                    scanner.nextLine();
                    newFullname = scanner.nextLine();
                    System.out.println("Enter new email");
                    newEmail = scanner.next();
                    try {
                        User.validate(username, newFullname, newEmail);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    system.getUserManager().update(username, newFullname, newEmail);
                    System.out.println("User updated successfully");
                });
        commandParser.registerCommand("user-delete",
                "Delete user",
                (Scanner scanner, RBACSystem system) -> {
                    String username;
                    System.out.println("Enter username");
                    username = scanner.next();
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    system.getUserManager().remove(user.get());
                    System.out.println("User deleted successfully");
                });
        commandParser.registerCommand("user-search",
                "Apply filters to search",
                (Scanner scanner, RBACSystem system) ->{
                    String field, value;
                    System.out.println("""
                Enter: field of search
                Fields:
                username
                email
                email-domain
                fullname""");
                    field = scanner.next();
                    System.out.println("Enter: value");
                    scanner.nextLine();
                    value = scanner.nextLine();
                    UserFilter filter;
                    switch (field) {
                        case "username":
                            filter = UserFilters.byUsernameContains(value);
                            break;
                        case "email":
                            filter = UserFilters.byEmail(value);
                            break;
                        case "email-domain":
                            filter = UserFilters.byEmailDomain(value);
                            break;
                        case "fullname":
                            filter = UserFilters.byFullNameContains(value);
                            break;
                        default:
                            System.out.println("field \"" + field + "\" does not exist");
                            return;
                    }
                    system.getUserManager().findAll(filter, UserSorters.byUsername()).
                            forEach(user-> System.out.println(user.format()));
                });
        commandParser.registerCommand("role-list",
                "List all roles",
                (Scanner scanner, RBACSystem system) -> {
                    system.getRoleManager().findAll().forEach(role -> System.out.println(role.format()));
                });
        commandParser.registerCommand("role-create",
                "Add new role",
                (Scanner scanner, RBACSystem system) -> {
                    String name, description;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    System.out.println("Enter role description");
                    //scanner.nextLine();
                    description = scanner.nextLine();
                    String permName, resource, permDesc, q;
                    List<Permission> permissions = new ArrayList<>();
                    while (true) {
                        System.out.println("Add permissions to new role? y/n?");
                        q = scanner.next();
                        if (!q.equals("n")) {
                            System.out.println("Permission name");
                            permName = scanner.next();
                            System.out.println("Permission resource");
                            scanner.nextLine();
                            resource = scanner.nextLine();
                            System.out.println("Permission description");
                            //scanner.nextLine();
                            permDesc = scanner.nextLine();
                            try {
                                permissions.add(new Permission(permName, resource, permDesc));
                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                                return;
                            }
                        } else {
                            break;
                        }
                    }
                    Role newRole = new Role(name);
                    newRole.description = description;
                    permissions.forEach(newRole::addPermission);
                    system.getRoleManager().add(newRole);
                    System.out.println("Role added successfully");
                });
        commandParser.registerCommand("role-view",
                "Show role info",
                (Scanner scanner, RBACSystem system) -> {
                    String name;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    System.out.println(role.get().format());
                });
        commandParser.registerCommand("role-update",
                "Set new description for role",
                (Scanner scanner, RBACSystem system) -> {
                    String name, newDescription;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    System.out.println("Enter new description");
                    newDescription = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    role.get().description = newDescription;
                    System.out.println("Role updated successfully");
                });
        commandParser.registerCommand("role-delete",
                "Delete role",
                (Scanner scanner, RBACSystem system) -> {
                    String name;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    List<RoleAssignment> assignments = system.getAssignmentManager().
                            findByFilter(AssignmentFilters.byRole(role.get()).
                                    and(AssignmentFilters.activeOnly()));
                    if (!assignments.isEmpty()) {
                        System.out.println("Role is assigned to users:");
                        assignments.forEach(assignment -> System.out.println(assignment.user().format()));
                        System.out.println("Confirm role deletion: yes/n");
                        String q;
                        q = scanner.next();
                        if (!q.equals("n")) {
                            system.getRoleManager().remove(role.get());
                            System.out.println("Role deletion successful");
                        } else {
                            System.out.println("Role deletion aborted");
                        }
                    }
                    else {
                        system.getRoleManager().remove(role.get());
                        System.out.println("Role deletion successful");
                    }
                });
        commandParser.registerCommand("role-add-permission",
                "Add new permission to role",
                (Scanner scanner, RBACSystem system) -> {
                    String name, permName, permResource, permDescription;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    System.out.println("Enter permission name");
                    permName = scanner.nextLine();
                    System.out.println("Enter permission resource");
                    permResource = scanner.nextLine();
                    System.out.println("Enter permission description");
                    //scanner.nextLine();
                    permDescription = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    system.getRoleManager().addPermissionToRole(name, new Permission(permName, permResource, permDescription));
                    System.out.println("Permission added to role");
                });
        commandParser.registerCommand("role-remove-permission",
                "Remove permission from role",
                (Scanner scanner, RBACSystem system) -> {
                    String name;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    List<Permission> permissions = role.get().getPermissions().stream().toList();
                    System.out.println("Choose permission to delete (0 to abandon):");
                    int i = 0;
                    for (;i < permissions.size(); i++) {
                        System.out.println((i+1)+": "+permissions.get(i).format());
                    }
                    if ((i = scanner.nextInt()) != 0 && i <= permissions.size()) {
                        system.getRoleManager().removePermissionFromRole(name, permissions.get(i-1));
                        System.out.println("Permission removed successfully");
                    } else {
                        System.out.println("Permission removal aborted");
                    }
                });
        commandParser.registerCommand("role-search",
                "Filter role list",
                (Scanner scanner, RBACSystem system) -> {
                    String field, value;
                    System.out.println("""
                Enter: field
                Fields:
                name
                permission name resource
                min-permission-count""");
                    field = scanner.next();
                    System.out.println("Enter value");
                    scanner.nextLine();
                    value = scanner.nextLine();
                    RoleFilter filter;
                    switch (field) {
                        case "name":
                            filter = RoleFilters.byName(value);
                            break;
                        case "permission":
                            filter = RoleFilters.hasPermission(value.split("\\s+")[0], value.split("\\s+")[1]);
                            break;
                        case "min-permission-count":
                            filter = RoleFilters.hasAtLeastNPermissions(Integer.parseInt(value));
                            break;
                        default:
                            System.out.println("field \"" + field + "\" does not exist");
                            return;
                    }
                    system.getRoleManager().findByFilter(filter).
                            forEach(role-> System.out.println(role.format()));
                });
        commandParser.registerCommand("assign-role",
                "Assign role to user",
                (Scanner scanner, RBACSystem system) -> {
                    String username;
                    System.out.println("Enter username");
                    username = scanner.next();
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    System.out.println("Available roles to choose:");
                    system.getRoleManager().findAll().forEach(role -> System.out.println(role.format()));
                    String name, type, reason, expiresAt;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    System.out.println("Enter assignment type: temporary/permanent");
                    type = scanner.next();
                    System.out.println("Enter reason");
                    scanner.nextLine();
                    reason = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    switch (type) {
                        case "permanent":
                            system.getAssignmentManager().add(new PermanentAssignment(user.get(), role.get(),
                                    new AssignmentMetadata(system.getCurrentUser(), LocalDate.now().toString(),
                                            reason)));
                            System.out.println("Role assigned successfully");
                            break;
                        case "temporary":
                            System.out.println("Enter: expire date YYYY-MM-DD");
                            expiresAt = scanner.next();
                            TemporaryAssignment roleAssignment = new TemporaryAssignment(user.get(), role.get(),
                                    new AssignmentMetadata(system.getCurrentUser(), LocalDate.now().toString(), reason));
                            roleAssignment.extend(expiresAt);
                            system.getAssignmentManager().add(roleAssignment);
                            System.out.println("Role assigned successfully");
                            break;
                        default:
                            System.out.println("type: temporary/permanent");
                    }
                });
        commandParser.registerCommand("revoke-role",
                "Revoke role from user",
                (Scanner scanner, RBACSystem system) -> {
                    System.out.println("Enter username");
                    String username;
                    username = scanner.next();
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    System.out.println("Active assignments:");
                    var assignments = system.getAssignmentManager().findByFilter(
                            AssignmentFilters.byUser(user.get()).and(AssignmentFilters.activeOnly()));
                    assignments.forEach(System.out::println);
                    String name;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    AssignmentFilter filter = AssignmentFilters.activeOnly().and(
                            AssignmentFilters.byRole(role.get())).and(
                            AssignmentFilters.byUser(user.get()));
                    String id = system.getAssignmentManager().findByFilter(filter).getFirst().assignmentId();
                    system.getAssignmentManager().revokeAssignment(id);
                    System.out.println("Assignment revoked successfully");
                });
        commandParser.registerCommand("assignment-list",
                "List all assignments",
                (Scanner scanner, RBACSystem system) -> {
                    system.getAssignmentManager().findAll().forEach(System.out::println);
                });
        commandParser.registerCommand("assignment-list-user",
                "List all assignments for user",
                (Scanner scanner, RBACSystem system) -> {
                    String username;
                    System.out.println("Enter username");
                    username = scanner.next();
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    system.getAssignmentManager().
                            findByFilter(AssignmentFilters.byUsername(username)).
                            forEach(System.out::println);
                });
        commandParser.registerCommand("assignment-list-role",
                "List all assignments for role",
                (Scanner scanner, RBACSystem system) -> {
                    String name;
                    System.out.println("Enter role name");
                    scanner.nextLine();
                    name = scanner.nextLine();
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    system.getAssignmentManager().
                            findByFilter(AssignmentFilters.byRole(role.get())).
                            forEach(System.out::println);
                });
        commandParser.registerCommand("assignment-list-active",
                "List all active assignments",
                (Scanner scanner, RBACSystem system) -> {
                    system.getAssignmentManager().
                            findByFilter(AssignmentFilters.activeOnly()).
                            forEach(System.out::println);
                });
        commandParser.registerCommand("assignment-list-expired",
                "List all expired assignments",
                (Scanner scanner, RBACSystem system) -> {
                    system.getAssignmentManager().
                            findByFilter(AssignmentFilters.expiringBefore(LocalDate.now().toString())).
                            forEach(System.out::println);
                });
        commandParser.registerCommand("assignment-extend",
                "Extend a temporary assignment",
                (Scanner scanner, RBACSystem system) -> {
                    String type;
                    System.out.println("u+r/id");
                    type = scanner.next();
                    TemporaryAssignment assignment;
                    switch (type) {
                        case "id":
                            String id;
                            System.out.println("Enter: id");
                            id = scanner.next();
                            Optional<RoleAssignment> roleAssignment = system.getAssignmentManager().findById(id);
                            if (roleAssignment.isEmpty()) {
                                System.out.println("No assignment with id "+id);
                                return;
                            }
                            if (roleAssignment.get().assignmentType().equals("PERMANENT")) {
                                System.out.println("This assignment is permanent");
                            }
                            assignment = (TemporaryAssignment) roleAssignment.get();
                            break;
                        case "u+r":
                            String username, name;
                            System.out.println("Enter username");
                            username = scanner.next();
                            System.out.println("Enter role name");
                            scanner.nextLine();
                            name = scanner.nextLine();
                            Optional<Role> role = system.getRoleManager().findByName(name);
                            if (role.isEmpty()) {
                                System.out.println("Role "+name+" not found");
                                return;
                            }
                            Optional<User> user = system.getUserManager().findByUsername(username);
                            if (user.isEmpty()) {
                                System.out.println("User "+username+" not found");
                                return;
                            }
                            AssignmentFilter filter = AssignmentFilters.byRole(role.get()).and(
                                    AssignmentFilters.byUser(user.get()));
                            List<RoleAssignment> assignments = system.getAssignmentManager().findByFilter(filter);
                            if (assignments.isEmpty()) {
                                System.out.println("No assignment found");
                                return;
                            }
                            if (assignments.getFirst().assignmentType().equals("PERMANENT")) {
                                System.out.println("This assignment is permanent");
                            }
                            assignment = (TemporaryAssignment) assignments.getFirst();
                            break;
                        default:
                            System.out.println("id or u+r");
                            return;
                    }
                    System.out.println("Enter: expire date YYYY-MM-DD");
                    String expiresAt;
                    expiresAt = scanner.next();
                    assignment.extend(expiresAt);
                    System.out.println("Assignment extended successfully");
                });
        commandParser.registerCommand("assignment-search",
                "Filter assignments list",
                (Scanner scanner, RBACSystem system) -> {
                    String field, value;
                    System.out.println("""
                Enter: field
                Fields:
                user
                role
                type
                state
                assigned-after
                expire-before""");
                    field = scanner.next();
                    System.out.println("Enter value");
                    scanner.nextLine();
                    value = scanner.nextLine();
                    AssignmentFilter filter;
                    switch (field) {
                        case "user":
                            filter = AssignmentFilters.byUsername(value);
                            break;
                        case "role":
                            filter = AssignmentFilters.byRoleName(value);
                            break;
                        case "type":
                            filter = AssignmentFilters.byType(value);
                            break;
                        case "state":
                            if (value.equalsIgnoreCase("active")) filter = AssignmentFilters.activeOnly();
                            else if (value.equalsIgnoreCase("inactive")) filter = AssignmentFilters.inactiveOnly();
                            else {
                                System.out.println("active/inactive");
                                return;
                            }
                            break;
                        case "assigned-after":
                            filter = AssignmentFilters.assignedAfter(value);
                            break;
                        case "expire-before":
                            filter = AssignmentFilters.expiringBefore(value);
                            break;
                        default:
                            System.out.println("field \"" + field + "\" does not exist");
                            return;
                    }
                    system.getAssignmentManager().findByFilter(filter).
                            forEach(assignment -> {
                                if (assignment instanceof TemporaryAssignment o) {
                                    System.out.println(o.summary());
                                } else if (assignment instanceof PermanentAssignment o) {
                                    System.out.println(o.summary());
                                }
                            });
                });
        commandParser.registerCommand("permissions-user",
                "List permissions of user",
                (Scanner scanner, RBACSystem system) -> {
                    String username;
                    System.out.println("Enter username");
                    username = scanner.next();
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    system.getAssignmentManager().getUserPermissions(user.get()).
                            forEach(permission -> System.out.println(permission.format()));
                });
        commandParser.registerCommand("permissions-check",
                "Check if user has permission",
                (Scanner scanner, RBACSystem system) -> {
                    String username, name, resource;
                    System.out.println("Enter username");
                    username = scanner.next();
                    System.out.println("Enter permission name");
                    name = scanner.next();
                    System.out.println("Enter permission resource");
                    scanner.nextLine();
                    resource = scanner.nextLine();
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    if (system.getAssignmentManager().
                            userHasPermission(user.get(), name, resource)) {
                        System.out.println(username+" has this permission from:");
                        System.out.println(system.getAssignmentManager().
                                findByUser(user.get()).stream().
                                filter(roleAssignment -> roleAssignment.role().hasPermission(name, resource)).
                                findFirst().get().role().format());
                    }
                    else {
                        System.out.println(username+" does not have this permission");
                    }
                });
        commandParser.registerCommand("help",
                "Prints all command descriptions",
                (Scanner scanner, RBACSystem system) -> {
                    CommandParser.printHelp();
                });
        commandParser.registerCommand("stats",
                "Shows system stats",
                (Scanner scanner, RBACSystem system) -> {
                    System.out.println(system.generateStatistics());
                });
        commandParser.registerCommand("clear",
                "Clears console",
                (Scanner scanner, RBACSystem system) -> {
                    //System.out.println("\033[H\033[2J");
                    //System.out.flush();
                    for (int i = 0; i < 50; i++) { // Print a sufficient number of newlines
                        System.out.println();
                    }
                });
        commandParser.registerCommand("exit",
                "Exits application",
                (Scanner scanner, RBACSystem system) -> {
                    System.out.println("Are you sure you want to quit the application? yes/n");
                    String input;
                    input = scanner.next();
                    if (input.equals("yes")) {
                        System.exit(0);
                    }
                });
        return commandParser;
    }
}
