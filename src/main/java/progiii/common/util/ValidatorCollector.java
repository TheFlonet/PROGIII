package progiii.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidatorCollector {
    private boolean areAllEmailsGood;
    private final Map<String, Boolean> validator;

    public ValidatorCollector() {
        this.areAllEmailsGood = true;
        this.validator = new HashMap<>();
    }

    public boolean areAllEmailsGood() {
        return areAllEmailsGood;
    }

    public Set<String> getEmails(boolean condition) {
        return validator.keySet().stream().filter(email -> validator.get(email) == condition).collect(Collectors.toSet());
    }

    public String emailsString(boolean condition) {
        return validator.keySet().stream().filter(email -> validator.get(email) == condition).map(recipient -> "\t- " + recipient + "\n").collect(Collectors.joining());
    }

    public void put(String email, boolean found) {
        validator.put(email, found);
        if (!found)
            areAllEmailsGood = false;
    }
}
