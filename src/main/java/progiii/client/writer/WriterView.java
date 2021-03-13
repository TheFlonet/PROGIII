package progiii.client.writer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WriterView extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass()
                .getResource("/progiii/client/WriteEmail.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Write");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
