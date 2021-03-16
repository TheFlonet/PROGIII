package progiii.client.reader;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import progiii.common.Email;

import java.net.URL;
import java.util.ResourceBundle;

public class ReaderView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("/progiii/client/ReadEmail.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Reader");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void startReader(Stage stage, Email email) throws Exception {
        ReaderModel.getInstance().setEmail(email);
        start(stage);
    }
}
