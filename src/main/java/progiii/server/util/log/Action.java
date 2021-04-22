package progiii.server.util.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Stampa il messaggio sulla console del server
 */
public class Action {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("H:m:s");
    private final String message;
    private final MsgType type;

    public Action(String message, MsgType type) {
        this.message = DATE_FORMAT.format(new Date()) + " " + message;
        this.type = type;
    }

    @Override
    public String toString() {
        return message;
    }

    public MsgType getType() {
        return type;
    }
}
