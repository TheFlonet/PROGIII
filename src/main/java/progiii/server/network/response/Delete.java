package progiii.server.network.response;

import com.sun.net.httpserver.Authenticator;

public class Delete extends Response {
    public Delete(boolean success, String status) {
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
