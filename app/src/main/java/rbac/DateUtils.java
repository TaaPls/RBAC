package rbac;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {
    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }
    public static String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }
    public static boolean isBefore(String date1, String date2) {
        return date1.compareTo(date2) < 0;
    }
    public static boolean isAfter(String date1, String date2) {
        return date1.compareTo(date2) > 0;
    }
    public static String addDays(String date, int days) {
        String[] s = date.split("-");
        int dd = Integer.parseInt(s[2]) + days, mm = Integer.parseInt(s[1]), yyyy = Integer.parseInt(s[0]);
        if (dd > 31) {
            mm += 1;
            dd = dd % 32 + 1;
        }
        if (mm > 12) {
            yyyy += 1;
            mm = mm % 13 + 1;
        }
        return String.format("%d-%02d-%02d", yyyy, mm, dd);
    }
    public static String formatRelativeTime(String date) {
        return LocalDate.parse(date).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy").withLocale(Locale.ENGLISH));
    }
}
