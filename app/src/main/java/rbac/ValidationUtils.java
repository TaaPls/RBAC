package rbac;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidUsername(String username) {
        return username != null &&
                username.length() >= 3 &&
                username.length() <= 20 &&
                Pattern.compile("^\\w+$").matcher(username).matches();
    }
    public static boolean isValidEmail(String email) {
        return email != null && Pattern.compile("^\\w+@\\w+\\.\\w+$").matcher(email).matches();
    }
    public static boolean isValidDate(String date) {
        if (date == null || !Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$").matcher(date).matches())
            return false;

        String[] d = date.split("-");
        return Integer.parseInt(d[1]) <= 12 && Integer.parseInt(d[2]) <= 31;
    }
    public static String normalizeString(String input) {
        StringBuilder str = new StringBuilder(input.trim().toLowerCase());
        str.setCharAt(0, Character.toUpperCase(str.charAt(0)));
        return str.toString();
    }
    public static void requireNonEmpty(String value) {
        if (value == null || value.isEmpty())
            throw new NullPointerException("Provided string is empty");
    }
}
