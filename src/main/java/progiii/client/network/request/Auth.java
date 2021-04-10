package progiii.client.network.request;

import progiii.common.network.RequestType;

public class Auth extends Request{

    public Auth(String email) {
        super(email);
    }

    @Override
    public RequestType getType() {
        return RequestType.AUTH;
    }

    @Override
    public String toString() {
        return String.format("Authentication request: %s", email);
    }
}
