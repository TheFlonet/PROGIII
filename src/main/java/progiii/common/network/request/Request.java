package progiii.common.network.request;

import progiii.common.network.RequestType;

import java.io.Serializable;

/**
 *
 * Astrae il concetto di richiesta dal client
 * Ogni tipo di richiesta (elencato in RequestType come enumerazione) ha la sua classe
 */
public abstract class Request implements Serializable {
    protected final String email;

    public Request(String email) {
        if (email == null)
            throw new IllegalArgumentException("Email cannot be null");
        if (email.isEmpty())
            throw new IllegalArgumentException("Email cannot be empty");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public abstract RequestType getType();

    @Override
    public abstract String toString();
}
