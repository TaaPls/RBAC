package rbac;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommandRegistry {
    public static CommandParser getParser() {
        CommandParser commandParser = new CommandParser();
        commandParser.registerCommand("user-list",
                "List of all users",
                (Scanner scanner, RBACSystem system) -> {
                    String field, value, input;
                    input = ConsoleUtils.promptString(scanner,
                            "field value or empty", false);
                    if (input == "") {
                        UserManager manager = system.getUserManager();
                        System.out.println("Users (" + manager.count() + "):");
                        List<String[]> rows = new ArrayList<>();
                        manager.findAll().forEach(user-> rows.add(new String[]{user.username(), user.fullName(),
                        "<"+user.email()+">"}));
                        System.out.println(FormatUtils.formatTable(new String[]{"Username", "Full Name", "Email"}, rows));
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
                    username = ConsoleUtils.promptString(scanner, "Enter username:", true);
                    fullname = ConsoleUtils.promptString(scanner, "Enter full name:", true);
                    email = ConsoleUtils.promptString(scanner, "Enter email:", true);
                    try {
                        user = User.validate(username, fullname, email);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return;
                    }
                    system.getUserManager().add(user);
                    AuditLog.log("ADD USER",
                            system.getCurrentUser(),
                            "User manager",
                            "User "+username+" added");
                    System.out.println("User added successfully");
                });
        commandParser.registerCommand("user-view",
                "Show user info",
                (Scanner scanner, RBACSystem system) -> {
                    String username;
                    username = ConsoleUtils.promptString(scanner, "Enter username:", true);
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
                    username = ConsoleUtils.promptString(scanner, "Enter username:", true);
                    newFullname = ConsoleUtils.promptString(scanner, "Enter new full name:", true);
                    newEmail = ConsoleUtils.promptString(scanner, "Enter new email:", true);
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
                    username = ConsoleUtils.promptString(scanner, "Enter username:", true);
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    system.getUserManager().remove(user.get());
                    AuditLog.log("DELETE USER",
                            system.getCurrentUser(),
                            "User manager",
                            "User "+username+" deleted");
                    System.out.println("User deleted successfully");
                });
        commandParser.registerCommand("user-search",
                "Apply filters to search",
                (Scanner scanner, RBACSystem system) ->{
                    String field, value;
                    field = ConsoleUtils.promptString(scanner,
                            "Enter: field of search\nFields:\nusername\nemail\nemail-domain\nfullname",
                            true);
                    value = ConsoleUtils.promptString(scanner, "Enter value:", true);
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
                    name = ConsoleUtils.promptString(scanner, "Enter role name:", true);
                    description = ConsoleUtils.promptString(scanner, "Enter role description:", false);
                    String permName, resource, permDesc, q;
                    List<Permission> permissions = new ArrayList<>();
                    while (true) {
                        if (ConsoleUtils.promptYesNo(scanner, "Add permissions to new role? yes/n?")) {
                            permName = ConsoleUtils.promptString(scanner, "Enter permission name", true);
                            resource = ConsoleUtils.promptString(scanner, "Enter permission resource", true);
                            permDesc = ConsoleUtils.promptString(scanner, "Enter permission description", true);
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
                    AuditLog.log("ADD ROLE",
                            system.getCurrentUser(),
                            "Role manager",
                            "Role "+name+" added with "+permissions.size()+" permissions");
                    System.out.println("Role added successfully");
                });
        commandParser.registerCommand("role-view",
                "Show role info",
                (Scanner scanner, RBACSystem system) -> {
                    String name;
                    name = ConsoleUtils.promptString(scanner, "Enter role name", true);;
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
                    name = ConsoleUtils.promptString(scanner, "Enter role name", true);
                    newDescription = ConsoleUtils.promptString(scanner, "Enter new permission description", true);
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
                    name = ConsoleUtils.promptString(scanner, "Enter role name", true);
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
                        if (ConsoleUtils.promptYesNo(scanner, "Confirm role deletion: yes/n")) {
                            system.getRoleManager().remove(role.get());
                            AuditLog.log("DELETE ROLE",
                                    system.getCurrentUser(),
                                    "Role manager",
                                    "Role "+role.get().name+" deleted with assigned users"+String.join(", ",
                                            assignments.stream().map(
                                                    roleAssignment -> roleAssignment.user().username()).
                                            toList()));
                            System.out.println("Role deletion successful");
                        } else {
                            System.out.println("Role deletion aborted");
                        }
                    }
                    else {
                        system.getRoleManager().remove(role.get());
                        AuditLog.log("DELETE ROLE",
                                system.getCurrentUser(),
                                "Role manager",
                                "Role "+role.get().name+" deleted with no assigned users");
                        System.out.println("Role deletion successful");
                    }
                });
        commandParser.registerCommand("role-add-permission",
                "Add new permission to role",
                (Scanner scanner, RBACSystem system) -> {
                    String name, permName, permResource, permDescription;
                    name = ConsoleUtils.promptString(scanner, "Enter role name:", true);
                    permName = ConsoleUtils.promptString(scanner, "Enter permission name:", true);
                    permResource = ConsoleUtils.promptString(scanner, "Enter permission resource:", true);
                    permDescription = ConsoleUtils.promptString(scanner, "Enter permission description:", true);
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
                    name = ConsoleUtils.promptString(scanner, "Enter role name:", true);
                    Optional<Role> role = system.getRoleManager().findByName(name);
                    if (role.isEmpty()) {
                        System.out.println("Role "+name+" not found");
                        return;
                    }
                    List<Permission> permissions = role.get().getPermissions().stream().toList();
                    System.out.println();
                    Permission i = ConsoleUtils.promptChoice(scanner, "Choose permission to delete (0 to abandon):",
                            permissions);
                    if (i == null) {
                        System.out.println("Permission removal aborted");
                        return;
                    }
                    system.getRoleManager().removePermissionFromRole(name, i);
                    System.out.println("Permission removed successfully");
//                    int i = 0;
//                    for (;i < permissions.size(); i++) {
//                        System.out.println((i+1)+": "+permissions.get(i).format());
//                    }
//                    if ((i = scanner.nextInt()) != 0 && i <= permissions.size()) {
//                        system.getRoleManager().removePermissionFromRole(name, permissions.get(i-1));
//                        System.out.println("Permission removed successfully");
//                    } else {
//                        System.out.println("Permission removal aborted");
//                    }
                });
        commandParser.registerCommand("role-search",
                "Filter role list",
                (Scanner scanner, RBACSystem system) -> {
                    String field, value;
                    field = ConsoleUtils.promptString(scanner, """
                    Enter: field
                    Fields:
                    name
                    permission name resource
                    min-permission-count""", true);
                    value = ConsoleUtils.promptString(scanner, "Enter value:", true);
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
                    username = ConsoleUtils.promptString(scanner, "Enter username", true);
                    Optional<User> user = system.getUserManager().findByUsername(username);
                    if (user.isEmpty()) {
                        System.out.println("User "+username+" not found");
                        return;
                    }
                    System.out.println("Available roles to choose:");
                    system.getRoleManager().findAll().forEach(role -> System.out.println(role.format()));
                    String name, type, reason, expiresAt;
                    name = ConsoleUtils.promptString(scanner, "Enter role name", true);
                    type = ConsoleUtils.promptString(scanner, "Enter assignment type: temporary/permanent", true);
                    reason = ConsoleUtils.promptString(scanner, "Enter reason", true);
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
                            AuditLog.log("ASSIGN ROLE",
                                    system.getCurrentUser(),
                                    "Assignment manager",
                                    "Role "+role.get().name+" assigned to user "+user.get().username()+" until "+
                                            LocalDate.now().plusMonths(1));
                            System.out.println("Role assigned successfully");
                            break;
                        case "temporary":
                            expiresAt = ConsoleUtils.promptString(scanner, "Enter: expire date YYYY-MM-DD", true);
                            if (!ValidationUtils.isValidDate(expiresAt)) {
                                System.out.println("Date is not valid");
                                return;
                            }
                            TemporaryAssignment roleAssignment = new TemporaryAssignment(user.get(), role.get(),
                                    new AssignmentMetadata(system.getCurrentUser(), LocalDate.now().toString(), reason));
                            roleAssignment.extend(expiresAt);
                            system.getAssignmentManager().add(roleAssignment);
                            AuditLog.log("ASSIGN ROLE",
                                    system.getCurrentUser(),
                                    "Assignment manager",
                                    "Role "+role.get().name+" assigned to user "+user.get().username()+" permanently");
                            System.out.println("Role assigned successfully");
                            break;
                        default:
                            System.out.println("type: temporary/permanent");
                    }
                });
        commandParser.registerCommand("revoke-role",
                "Revoke role from user",
                (Scanner scanner, RBACSystem system) -> {
                    String username;
                    username = ConsoleUtils.promptString(scanner, "Enter username", true);
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
                    name = ConsoleUtils.promptString(scanner, "Enter role name", true);
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
                    AuditLog.log("REVOKE ROLE",
                            system.getCurrentUser(),
                            "Assignment manager",
                            "Role "+role.get().name+" revoked from user "+user.get().username());
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
                    username = ConsoleUtils.promptString(scanner, "Enter username", true);
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
                    name = ConsoleUtils.promptString(scanner, "Enter role name", true);
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
                    type = ConsoleUtils.promptString(scanner, "u+r/id", true);
                    TemporaryAssignment assignment;
                    switch (type) {
                        case "id":
                            String id;
                            id = ConsoleUtils.promptString(scanner, "Enter: id", true);
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
                            username = ConsoleUtils.promptString(scanner, "Enter: username", true);
                            name = ConsoleUtils.promptString(scanner, "Enter role name", true);
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
                    String expiresAt;
                    expiresAt = ConsoleUtils.promptString(scanner, "Enter: expire date YYYY-MM-DD", true);
                    if (!ValidationUtils.isValidDate(expiresAt)) {
                        System.out.println("Date is not valid");
                        return;
                    }
                    assignment.extend(expiresAt);
                    System.out.println("Assignment extended successfully");
                });
        commandParser.registerCommand("assignment-search",
                "Filter assignments list",
                (Scanner scanner, RBACSystem system) -> {
                    String field, value;
                    field = ConsoleUtils.promptString(scanner, """
                    Enter: field
                    Fields:
                    user
                    role
                    type
                    state
                    assigned-after
                    expire-before""", true);
                    value = ConsoleUtils.promptString(scanner, "Enter value", true);
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
                    username = ConsoleUtils.promptString(scanner, "Enter username", true);
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
                    username = ConsoleUtils.promptString(scanner, "Enter username:", true);
                    name = ConsoleUtils.promptString(scanner, "Enter permission name:", true);
                    resource = ConsoleUtils.promptString(scanner, "Enter permission resource:", true);
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
                    System.out.println("\033[H\033[2J");
                    System.out.flush();
                });
        commandParser.registerCommand("exit",
                "Exits application",
                (Scanner scanner, RBACSystem system) -> {
                    if (ConsoleUtils.promptYesNo(scanner, "Are you sure you want to quit the application? yes/n")) {
                        system.getExecutorService().shutdown();
                        try {
                            system.getExecutorService().awaitTermination(10, TimeUnit.SECONDS);
                            AuditLog.shutdown();
                            system.getScheduledExecutorService().shutdown();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.exit(0);
                    }
                });
        commandParser.registerCommand("audit-log",
                "List log",
                (Scanner scanner, RBACSystem system) -> AuditLog.printLog());
        commandParser.registerCommand("report-users",
                "Generate and save users report",
                (Scanner scanner, RBACSystem system) -> {
                    String report = ReportGenerator.generateUserReport(
                            system.getUserManager(), system.getAssignmentManager());
                    System.out.println(report);
                    String input;
                    input = ConsoleUtils.promptString(scanner, "\nSave report? filename/n", true);
                    if (input.equals("n"))
                        return;
                    ReportGenerator.exportToFile(report, input);
                    System.out.println("Report saved successfully");
                });
        commandParser.registerCommand("report-roles",
                "Generate and save roles report",
                (Scanner scanner, RBACSystem system) -> {
                    String report = ReportGenerator.generateRoleReport(
                            system.getRoleManager(), system.getAssignmentManager());
                    System.out.println(report);
                    String input;
                    input = ConsoleUtils.promptString(scanner, "\nSave report? filename/n", true);
                    if (input.equals("n"))
                        return;
                    ReportGenerator.exportToFile(report, input);
                    System.out.println("Report saved successfully");
                });
        commandParser.registerCommand("report-matrix",
                "Generate and save users x resources report",
                (Scanner scanner, RBACSystem system) -> {
                    String report = ReportGenerator.generatePermissionMatrix(
                            system.getUserManager(), system.getAssignmentManager());
                    System.out.println(report);
                    String input;
                    input = ConsoleUtils.promptString(scanner, "\nSave report? filename/n", true);
                    if (input.equals("n"))
                        return;
                    ReportGenerator.exportToFile(report, input);
                    System.out.println("Report saved successfully");
                });
        commandParser.registerCommand("report-users-async",
                "Generate users report asynchronously",
                (Scanner scanner, RBACSystem system) -> {
                    system.getExecutorService().submit(() -> {
                        String report = ReportGenerator.generateUserReport(
                                system.getUserManager(), system.getAssignmentManager());
                        ReportGenerator.exportToFile(report, "user_report");
                        System.out.println("Report saved async successfully");
                    });
                });
        commandParser.registerCommand("save-async",
                "Save data asynchronously",
                (Scanner scanner, RBACSystem system) -> {
                    system.getExecutorService().submit(() -> {
                        String str = ReportGenerator.generateUserReport(system.getUserManager(),
                                system.getAssignmentManager()) +
                                "\n\n\n" +
                                ReportGenerator.generateRoleReport(system.getRoleManager(),
                                        system.getAssignmentManager()) +
                                "\n\n\n" +
                                ReportGenerator.generatePermissionMatrix(system.getUserManager(),
                                        system.getAssignmentManager());
                        ReportGenerator.exportToFile(str, "system_data");
                    });
                });
        return commandParser;
    }
}
