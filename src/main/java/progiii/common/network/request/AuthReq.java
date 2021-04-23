package progiii.common.network.request;

import progiii.common.network.RequestType;

public class AuthReq extends Request{

    public AuthReq(String email) {
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
