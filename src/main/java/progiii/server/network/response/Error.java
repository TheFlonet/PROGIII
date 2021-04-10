package progiii.server.network.response;

public class Error extends Response{
    public Error(String status) {
        super(false, status);
    }

    @Override
    public ResponseType getType() {
        return ResponseType.ERROR;
    }

    @Override
    public String toString() {
        return String.format("Error: %s", status);
    }
}
