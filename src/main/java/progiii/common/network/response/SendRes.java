package progiii.common.network.response;

import progiii.common.network.ResponseType;

public class SendRes extends Response {
    public SendRes(boolean success, String status) {
        super(success, status);
    }

    @Override
    public ResponseType getType() {
        return ResponseType.SEND;
    }

    @Override
    public String toString() {
        return String.format("Send: %s\n\tStatus: %s", success ? "Succeeded" : "Failed", status);
    }
}
