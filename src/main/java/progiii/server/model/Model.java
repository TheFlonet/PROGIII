package progiii.server.model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import progiii.server.util.log.Event;

public class Model {
    private static Model INSTANCE;
    private ObservableList<Event> events = FXCollections.observableArrayList();

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
