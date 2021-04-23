package progiii.common.network.response;

import progiii.common.network.ResponseType;

public class DeleteRes extends Response {
    public DeleteRes(boolean success, String status) {
        super(success, status);
    }

    @Override
    public ResponseType getType() {
        return ResponseType.DELETE;
    }

    @Override
    public String toString() {
        return String.format("Delete response: %b, %s", success, status);
    }

}
