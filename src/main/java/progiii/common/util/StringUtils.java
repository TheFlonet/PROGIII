package progiii.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String cleanEmail(String email) {
        if (email == null)
            throw new IllegalArgumentException("Email cannot be null");
        if (email.isEmpty())
            throw new IllegalArgumentException("Email cannot be empty");
        Pattern emailPattern = Pattern.compile("[A-Za-z0-9_.-]+@.+[A-Za-z0-9_-]+");
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
        Pattern emailPattern = Pattern.compile("[A-Za-z0-9_.-]+@.+[A-Za-z0-9_-]+");

        for (String email : emails) {
            Matcher matcher = emailPattern.matcher(email);
            boolean found = matcher.find();
            checker.put(found ? matcher.group() : email, found);
        }

        return checker;
    }
}
