package progiii.client.home;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

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
    private ClientModel model;

    public void writeEmail(ActionEvent actionEvent) {

    }

    public void reconnect(ActionEvent actionEvent) {
        if (model.isConnected())
            model.setStatus("Already connected");
        else
            model.setStatus("Client not connected");
        //TODO provare la ri-connessione al server
    }

    public void about(ActionEvent actionEvent) {
        String about = "App developed by Barraco Cristian and Bifulco Mario. This EMail client is written in java using JavaFX and MVC pattern. It communicates with the server via Json messages.";
        if (model.getStatus().equals(about))
            model.setStatus("");
        else
            model.setStatus(about);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (model != null)
            throw new IllegalStateException("Model can be initialized only once");
        model = new ClientModel();
        status.textProperty().bind(model.getStatusProperty());

        try (Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), 8189)) {
            try (InputStream in = socket.getInputStream()) {
                Scanner input = new Scanner(in);
                try (OutputStream out = socket.getOutputStream()) {
                    PrintWriter output = new PrintWriter(out);

                    // TODO gestire input e output
                    model.setConnected(true);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Error while initializing input and output");
            model.setStatus("Client not connected");
        } catch (IOException e) {
            System.err.println("Unable to connect to the server");
            model.setStatus("Client not connected");
        }
    }
}
