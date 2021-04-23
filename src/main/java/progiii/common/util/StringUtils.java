package progiii.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Controlla la ben formatezza di un indirizzo email
 */
public class StringUtils {
    private static final Pattern emailPattern = Pattern.compile("[A-Za-z0-9_.-]+@.+[A-Za-z0-9_-]+");

    public static String cleanEmail(String email) {
        if (email == null)
            throw new IllegalArgumentException("Email cannot be null");
        if (email.isEmpty())
            throw new IllegalArgumentException("Email cannot be empty");
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.find())
            return matcher.group().toLowerCase();
        throw new IllegalArgumentException("Malformed email: " + email);
    }

    public static ValidatorCollector cleanEmailList(String emailList) {
        if (emailList == null)
            throw new IllegalArgumentException("Email cannot be null");
        if (emailList.isEmpty())
            throw new IllegalArgumentException("Email cannot be empty");
        String[] emails = emailList.toLowerCase().split(",");
        ValidatorCollector checker = new ValidatorCollector();

        for (String email : emails) {
            Matcher matcher = emailPattern.matcher(email);
            boolean found = matcher.find();
            checker.put(found ? matcher.group() : email, found);
        }

        return checker;
    }

    public static boolean isValid(String text) {
        return emailPattern.matcher(text).find();
    }
}
