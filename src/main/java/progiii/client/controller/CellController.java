package progiii.client.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import progiii.common.data.Email;

import java.io.IOException;

public class CellController extends ListCell<Email> {
    @FXML
    private Label subject;
    @FXML
    private Label from;
    @FXML
    private Label text;
    @FXML
    private Label date;
    @FXML
    private Parent container;
    private FXMLLoader loader;

    @Override
    protected void updateItem(Email email, boolean empty) {
        super.updateItem(email, empty);
        if (empty || email == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/progiii/client/cell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            subject.setText(email.getSubject());
            from.setText(email.getFrom());
            text.setText(email.getText());
            date.setText(email.getCompactDate());
            setText(null);
            setGraphic(container);
        }
    }
}
