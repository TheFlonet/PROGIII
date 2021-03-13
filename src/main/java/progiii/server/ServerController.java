package progiii.server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements Initializable {
    private static final int THREAD_NUM = 15;
    private ServerModel model;
    @FXML
    public TextArea logText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (model != null)
            throw new IllegalStateException("Model can be initialized only once");
        model = new ServerModel();
        logText.textProperty().bind(model.getLog());

        try {
            ExecutorService pool = Executors.newFixedThreadPool(THREAD_NUM);
            ServerSocket server = new ServerSocket(8189);

            // TODO testare connessione client server
            new Thread(() -> {
                int threadId = 0;
                while (true) {
                    Socket incoming = null;
                    try {
                        incoming = server.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Accepting client: " + threadId);
                    pool.execute(new ClientHandler(incoming, threadId++, model));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dumpLog(ActionEvent actionEvent) {
        String output = model.getLogText();
        String fileName = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss").format(LocalDateTime.now()) + ".log";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(output);
            model.log("Log successfully dumped in file " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearLog(ActionEvent actionEvent) {
        model.clearLog();
    }
}

class ClientHandler implements Runnable {
    private final Socket incoming;
    private final int id;
    private final ServerModel model;

    public ClientHandler(Socket incoming, int id, ServerModel model) {
        this.incoming = incoming;
        this.id = id;
        this.model = model;
    }

    @Override
    public void run() {
        model.log("Connected client: " + id);
        try (incoming) {
            try (InputStream in = incoming.getInputStream()) {
                Scanner input = new Scanner(in);
                try (OutputStream out = incoming.getOutputStream()) {
                    PrintWriter output = new PrintWriter(out);

                    //TODO gestire input output
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.log("Disconnected client: " + id);
    }
}