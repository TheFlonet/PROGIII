package progiii.client.home;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import progiii.common.Email;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    @FXML
    public Label status;
    @FXML
    public Label email;
    @FXML
    public Button reconnectBtn;
    @FXML
    public ListView<Email> inbox;
    @FXML
    public ListView<Email> outbox;
    private ClientModel model;

    public void reconnect(ActionEvent actionEvent) {
        setConnection();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (model != null)
            throw new IllegalStateException("Model can be initialized only once");
        model = new ClientModel();

        email.textProperty().bind(model.getEmail().getEmailProperty());
        status.textProperty().bindBidirectional((Property<String>) model.getStatusProperty());

        reconnectBtn.disableProperty().bind(model.connectedProperty());
        setConnection();

        initMailList(BoxType.INBOX);
        initMailList(BoxType.OUTBOX);
    }

    private void initMailList(BoxType type) {
        ObservableList<Email> inboxEmails = FXCollections.observableList(type == BoxType.INBOX ? model.getInbox() : model.getOutbox());
        ListView<Email> box = type == BoxType.INBOX ? inbox : outbox;
        box.setItems(inboxEmails);
        box.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Email> call(ListView listView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Email email, boolean b) {
                        super.updateItem(email, b);
                        if (email != null)
                            if (type == BoxType.INBOX)
                                setText(email.getSender().getEmail() + "\n" + email.getSubject());
                            else
                                setText(email.getReceiversString() + "\n" + email.getSubject());
                    }
                };
            }
        });

        box.setOnMouseClicked(mouseEvent -> {
            try {
                model.getReader().startReader(new Stage(), box.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                model.setStatus("Unable to open reader window");
            }
        });
    }

    public void writeEmail(ActionEvent actionEvent) {
        try {
            model.getWriter().start(new Stage());
        } catch (Exception e) {
            model.setStatus("Unable to open writer window");
        }
    }

    private void setConnection() {
        if (!model.isConnected()) model.initConnection();

        if (model.isConnected()) model.setStatus("Client connected");
        else model.setStatus("Client not connected");
    }
}

enum BoxType {
    INBOX,
    OUTBOX
}