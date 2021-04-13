package progiii.common.network.response;

import progiii.common.network.ResponseType;

public class ErrorRes extends Response{
    public ErrorRes(String status) {
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
