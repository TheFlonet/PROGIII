package progiii.client.network.request;

import java.util.HashSet;
import java.util.Set;

public class CheckEmail extends Request{
    private final Set<String> toCheck;

    public CheckEmail(String email) {
        super(email);
        toCheck = new HashSet<>();
        toCheck.add(email);
    }

    public CheckEmail(String email, Set<String> toCheck) {
        super(email);
        this.toCheck = toCheck;
    }

    public Set<String> getToCheck() {
        return toCheck;
    }

    @Override
    public RequestType getType() {
        return RequestType.CHECK;
    }

    @Override
    public String toString() {
        return String.format("Check request, client: %s", email);
    }
}
