package progiii.server;

import javafx.beans.property.SimpleStringProperty;

public class ServerModel {
    private final SimpleStringProperty log;

    public ServerModel() {
        log = new SimpleStringProperty();
        log.set("");
    }

    protected SimpleStringProperty getLog() {
        return log;
    }

    public String getLogText() {
        synchronized (log) {
            return log.get();
        }
    }

    public void log(String text) {
        synchronized (log) {
            log.set(log.get() + "\n" + text);
            System.out.println(log.get());
        }
    }

    public void clearLog() {
        log.set("");
    }
}
