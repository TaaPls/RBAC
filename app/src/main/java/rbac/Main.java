package rbac;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RBACSystem rbacSystem = new RBACSystem();
        rbacSystem.initialize();
        CommandParser commandParser = CommandRegistry.getParser();
        Scanner scanner = new Scanner(System.in);
        String input;
        scanner.nextLine();

        while (true) {
            input = scanner.nextLine();
            commandParser.parseAndExecute(input, scanner, rbacSystem);
        }
    }
}