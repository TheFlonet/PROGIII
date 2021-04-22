package progiii.server.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import progiii.server.util.log.Event;

/**
 *
 * Singleton che contiene una lista osservabile di eventi (log)
 */
public class Model {
    private static Model INSTANCE;
    private final ObservableList<Event> events = FXCollections.observableArrayList();

    public Model() {
        if (INSTANCE != null)
            throw new IllegalStateException("Model already initialized");
        INSTANCE = this;
    }

    public static synchronized Model getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Model hasn't been initialized yet.");
        }
        return INSTANCE;
    }

    public ObservableList<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        Platform.runLater(() -> events.add(event));
    }
}
