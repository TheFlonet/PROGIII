package progiii.client.network.request;

import progiii.common.network.RequestType;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Pull extends Request{
    private final Set<Integer> localIdSet;

    public Pull(String email, Set<Integer> localIdSet) {
        super(email);
        this.localIdSet = localIdSet;
    }

    @Override
    public RequestType getType() {
        return RequestType.PULL;
    }

    @Override
    public String toString() {
        String emails = localIdSet.stream().map(Objects::toString).collect(Collectors.joining(", ", "[", "]"));
        return String.format("""
                Pull request {
                \tClient email: %s
                \tLocal email IDs: %s
                }""", email, emails);
    }
}
