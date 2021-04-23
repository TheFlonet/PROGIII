package progiii.common.network.request;

import progiii.common.data.Email;
import progiii.common.network.RequestType;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteReq extends Request{
    private final Set<Integer> idToDelete;

    public DeleteReq(String email, Set<Email> idToDelete) {
        super(email);
        this.idToDelete = idToDelete.stream().map(Email::getId).collect(Collectors.toUnmodifiableSet());
    }

    public Set<Integer> getIdSetToDelete() {
        return idToDelete;
    }

    @Override
    public RequestType getType() {
        return RequestType.DELETE;
    }

    @Override
    public String toString() {
        return idToDelete.stream().map(Objects::toString).collect(Collectors.joining(", ", "Delete request: [","]"));
    }
}
