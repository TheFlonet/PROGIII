package progiii.common.network.response;

import progiii.common.network.ResponseType;

import java.io.Serializable;

/**
 *
 * Astrae il concetto di risposta dal server
 * Ogni tipo di risposta (enum ResponseType) ha la sua relativa implementazione
 */
public abstract class Response implements Serializable {
    protected final boolean success;
    protected final String status;

    public Response(boolean success, String status) {
        this.success = success;
        this.status = status;
    }

    public abstract ResponseType getType();

    public boolean isSuccess() {
        return success;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public abstract String toString();
}
