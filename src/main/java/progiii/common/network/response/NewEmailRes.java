package progiii.common.network.response;

import progiii.common.data.Email;
import progiii.common.network.ResponseType;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class NewEmailRes extends Response {
    private final Set<Email> emailSet;

    public NewEmailRes(Set<Email> emailSet) {
        super(true, "OK");
        this.emailSet = emailSet;
    }

    public NewEmailRes(String status, Set<Email> emailSet) {
        super(true, status);
        this.emailSet = emailSet;
    }

    public Set<Email> getEmailSet() {
        return emailSet;
    }

    @Override
    public ResponseType getType() {
        return ResponseType.NEW_EMAILS;
    }

    @Override
    public String toString() {
        if (!success || emailSet == null)
            return String.format("New email: %s\n", status);
        String res = emailSet.stream().map(email -> Arrays.stream(email.toString().split("\n"))
                .collect(Collectors.joining("\n\t", "\t", "\n")))
                .collect(Collectors.joining("\n"));
        return String.format("""
                New email {
                status: %s
                emails: %s
                }""", status, res);
    }
}
