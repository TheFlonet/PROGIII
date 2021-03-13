package progiii.client.popup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class About {
    public void display() {
        Stage popUp = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/progiii/client/AboutPopUp.fxml"));
        popUp.setResizable(false);
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        popUp.setTitle("About");
        popUp.setScene(new Scene(Objects.requireNonNull(root)));
        popUp.showAndWait();
    }
}
