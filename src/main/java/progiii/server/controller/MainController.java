package progiii.server.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import progiii.server.model.Model;
import progiii.server.util.log.Event;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private ListView<Event> eventList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventList.setCellFactory(eventListView -> new ListCell<>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("-fx-control-inner-background: derive(-fx-base,80%);"); //todo provare a rimuovere
                } else {
                    setText(item.toString());
                    String style = switch (item.getType()) {
                        case SUCCESS -> "palegreen";
                        case ERROR -> "red";
                        case EXCEPTION -> "orangered";
                        case UNDEFINED -> "yellow";
                    };
                    setStyle("-fx-control-inner-background:derive(" + style + ",50%);");
                }
            }
        });
        eventList.setItems(Model.getInstance().getEvents().sorted());
    }

    private void openEventWindow(Event selectedItem) {
        Stage stage = new Stage();
        FXMLLoader eventWindow = new FXMLLoader(getClass().getResource("progiii/server/serverEvent.fxml"));
        Scene scene;

        try {
            scene = new Scene(eventWindow.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EventController controller = eventWindow.getController();
        controller.initialize(selectedItem);
        stage.setTitle(selectedItem.toString());
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    @FXML
    public void handleEventListMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
            if (mouseEvent.getClickCount() == 2)
                openEventWindow(eventList.getSelectionModel().getSelectedItem());
    }
}
