package rbac;

import java.util.*;
import java.util.stream.Collectors;

public class CommandParser {
    private static final Map<String, Command> commands = new HashMap<>();
    private static final Map<String, String> commandDescriptions = new HashMap<>();

    public void registerCommand(String name, String description, Command command) {
        commands.put(name, command);
        commandDescriptions.put(name, description);
    }
    public void executeCommand(String commandName, Scanner scanner, RBACSystem system) {
        commands.get(commandName).execute(scanner, system);
    }
    public static void printHelp() {
        System.out.println("help:\t"+commandDescriptions.get("help"));
        List<String> sortedKeys = commandDescriptions.keySet().stream().sorted().toList();
        sortedKeys.forEach(key-> System.out.println(key+":\t"+commandDescriptions.get(key)));
    }
    public void parseAndExecute(String input, Scanner scanner, RBACSystem system) {
        System.out.println("input = "+input);
        String[] args = input.trim().split("\\s+");
        String command = args[0];
        if (!commands.containsKey(command)) {
            System.out.println("Command \""+command+"\" does not exist");
            return;
        }
        executeCommand(command, scanner, system);
    }
}
