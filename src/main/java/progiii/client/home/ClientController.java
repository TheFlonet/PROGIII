package progiii.client.home;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.Scanner;

public class ClientController implements Initializable {
    @FXML
    public Label status;
    @FXML
    public Label email;
    @FXML
    public Button reconnectBtn;
    private ClientModel model;

    public void writeEmail(ActionEvent actionEvent) {
            try {
                model.getWriter().start(new Stage());
            } catch (Exception e) {
                model.setStatus("Unable to open writer window");
            }
    }

    public void reconnect(ActionEvent actionEvent) {
        //TODO provare la ri-connessione al server
        if (!model.isConnected())
            model.setStatus("Client not connected, unable to reach the server");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (model != null)
            throw new IllegalStateException("Model can be initialized only once");
        model = new ClientModel();
        email.textProperty().bind(model.getEmail().getEmailProperty());
        status.textProperty().bind(model.getStatusProperty());

        try (Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), 8189)) {
            try (InputStream in = socket.getInputStream()) {
                Scanner input = new Scanner(in);
                try (OutputStream out = socket.getOutputStream()) {
                    PrintWriter output = new PrintWriter(out);

                    // TODO gestire input e output
                    model.setConnected(true);
                    reconnectBtn.setDisable(true);
                }
            }
        } catch (UnknownHostException e) {
            model.setStatus("Client not connected, error while communicating");
        } catch (IOException e) {
            model.setStatus("Client not connected, unable to reach the server");
        }
    }
}
