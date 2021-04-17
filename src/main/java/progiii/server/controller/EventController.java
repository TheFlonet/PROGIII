package progiii.server.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import progiii.server.util.log.Action;
import progiii.server.util.log.Event;

import java.text.SimpleDateFormat;

public class EventController {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm:ss");
    @FXML
    private ListView<Action> actionListView;
    @FXML
    private Label details;

    public void initialize(Event event) {
        details.setText(String.format("%s: request type: %s, sent from %s. Final state: %s",
                DATE_FORMAT.format(event.getDate()), event.getRequestType().toString(), event.getClient(), event.getType()));
        actionListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Action> call(ListView<Action> actionListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Action item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                            setStyle("-fx-control-inner-background: derive(-fx-base,80%);");
                        } else {
                            setText(item.toString());
                            String style = switch (item.getType()) {
                                case NORMAL -> "-fx-base,80%";
                                case ERROR -> "red,50%";
                                case EXCEPTION -> "orangered,50%";
                                case WARNING -> "yellow,50%";
                            };
                            setStyle("-fx-control-inner-background:derive(" + style + ");");
                        }
                    }
                };
            }
        });
        actionListView.setItems(FXCollections.observableArrayList(event.getActions()));
    }
}
