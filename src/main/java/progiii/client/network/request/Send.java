package progiii.client.network.request;

import progiii.common.data.Email;
import progiii.common.network.RequestType;

public class Send extends Request{
    private final Email email;

    public Send(Email email) {
        super(email.getFrom());
        this.email = email;
    }

    public Email getEmailObject() {
        return email;
    }

    @Override
    public RequestType getType() {
        return RequestType.SEND;
    }

    @Override
    public String toString() {
        return String.format("Send request: %s", email);
    }
}
