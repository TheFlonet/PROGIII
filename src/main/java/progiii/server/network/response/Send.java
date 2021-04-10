package progiii.server.network.response;

public class Send extends Response {
    public Send(boolean success, String status) {
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
