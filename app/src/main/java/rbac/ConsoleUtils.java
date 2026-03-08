package rbac;

import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {
    public static String promptString(Scanner scanner, String message, boolean required) {
        while (true)
        {
            System.out.println(message);
            String str = scanner.nextLine();
            if (required) {
                try {
                    ValidationUtils.requireNonEmpty(str);
                    return str.trim();
                } catch (NullPointerException e) {
                    System.out.println("Input is required");
                }
            } else
                return str.trim();
        }
    }
    public static int promptInt(Scanner scanner, String message, int min, int max) {
        int i;
        while (true) {
            System.out.println(message);
            i = scanner.nextInt();
            if (i <= max && i >= min)
                return i;
            System.out.println("Number must be between "+min+" and "+max);
        }
    }
    public static boolean promptYesNo(Scanner scanner, String message) {
        while (true)
        {
            System.out.println(message);
            String str = scanner.nextLine();
            switch (str) {
                case "yes": return true;
                case "n": return false;
            }
        }
    }
    public static <T> T promptChoice(Scanner scanner, String message, List<T> options) {
        System.out.println(message);
        int i = 0;
        for (;i < options.size(); i++) {
            System.out.println((i+1)+": "+options.get(i).toString());
        }
        if ((i = scanner.nextInt()) != 0 && i <= options.size()) {
            return options.get(i-1);
        } else {
            return null;
        }
    }
}
