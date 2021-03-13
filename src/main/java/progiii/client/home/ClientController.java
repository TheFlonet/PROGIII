package progiii.client.home;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import progiii.client.popup.About;

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
    private ClientModel model;

    public void writeEmail(ActionEvent actionEvent) {

    }

    public void reconnect(ActionEvent actionEvent) {
    }

    public void about(ActionEvent actionEvent) {
        (new About()).display();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (model != null)
            throw new IllegalStateException("Model can be initialized only once");
        model = new ClientModel();

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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
