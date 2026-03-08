package rbac;

import java.util.List;

public class FormatUtils {
    public static String formatTable(String[] headers, List<String[]> rows) {
        int columnCount = headers.length;
        int[] columnWidths = new int[columnCount];

        for (int i = 0; i < columnCount; i++) {
            columnWidths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }
        StringBuilder table = new StringBuilder().append("+");
        for (int i = 0; i < columnCount; i++) {
            table.append("-".repeat(columnWidths[i])).append("+");
        }
        table.append("\n");
        for (int i = 0; i < columnCount; i++) {
            table.append("|").append(padRight(headers[i], columnWidths[i]));
        }
        table.append("|\n+");
        for (int i = 0; i < columnCount; i++) {
            table.append("-".repeat(columnWidths[i])).append("+");
        }
        table.append("\n");
        for (String[] row : rows) {
            for (int i = 0; i < columnCount; i++) {
                table.append("|").append(padRight(row[i], columnWidths[i]));
            }
            table.append("|\n");
        }
        table.append("+");
        for (int i = 0; i < columnCount; i++) {
            table.append("-".repeat(columnWidths[i])).append("+");
        }
        return table.toString();
    }
    public static String formatBox(String text) {
        return "+" + "-".repeat(text.length()) + "+\n|" + text + "|\n" + "+" +
                "-".repeat(text.length()) + "+";
    }
    public static String formatHeader(String text) {
        String[] headers = text.split(" ");
        int columnCount = headers.length;
        int[] columnWidths = new int[columnCount];

        for (int i = 0; i < columnCount; i++) {
            columnWidths[i] = headers[i].length();
        }
        StringBuilder header = new StringBuilder().append("+");
        for (int i = 0; i < columnCount; i++) {
            header.append("-".repeat(columnWidths[i])).append("+");
        }
        header.append("\n");
        for (int i = 0; i < columnCount; i++) {
            header.append("|").append(padRight(headers[i], columnWidths[i]));
        }
        header.append("|\n+");
        for (int i = 0; i < columnCount; i++) {
            header.append("-".repeat(columnWidths[i])).append("+");
        }
        return header.toString();
    }
    public static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength)
            return text;
        text = text.substring(0, maxLength-3);
        return text.concat("...");
    }
    public static String padRight(String text, int length) {
        if (text.length() >= length)
            return text;
        return text.concat(" ".repeat(length-text.length()));
    }
    public static String padLeft(String text, int length) {
        if (text.length() >= length) return text;
        return " ".repeat(length-text.length()) + text;
    }
}
