package progiii.server.network.response;

import java.util.Map;
import java.util.stream.Collectors;

public class EmailExistence extends Response {
    private final Map<String, Boolean> result;

    public EmailExistence(Map<String, Boolean> result) {
        super(true, "");
        this.result = result;
    }

    public Map<String, Boolean> getResult() {
        return result;
    }

    public boolean getResult(String email) {
        if (result.containsKey(email))
            return result.get(email);
        throw new IllegalArgumentException(String.format("Email -%s- isn't part of the original query", email));
    }

    @Override
    public ResponseType getType() {
        return ResponseType.EMAIL_EXISTENCE;
    }

    @Override
    public String toString() {
        if (!success)
            return String.format("Email existence: %s\n", status);
        String res = result.keySet().stream().map(email -> String.format("\t(%s, %b)", email, result.get(email))).collect(Collectors.joining());
        return String.format("Email existence -%s-", res);
    }
}
