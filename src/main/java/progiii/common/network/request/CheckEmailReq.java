package progiii.common.network.request;

import progiii.common.network.RequestType;

import java.util.HashSet;
import java.util.Set;

public class CheckEmailReq extends Request{
    private final Set<String> toCheck;

    public CheckEmailReq(String email) {
        super(email);
        toCheck = new HashSet<>();
        toCheck.add(email);
    }

    public CheckEmailReq(String email, Set<String> toCheck) {
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
