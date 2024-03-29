package progiii.server.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import progiii.server.concurrency.ServerListener;
import progiii.server.model.Model;
import progiii.server.util.DataManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MailServer extends Application {
    private static final int MAX_CONNECTION_HANDLERS = 5;
    private static ServerSocket serverSocket;
    private static DataManager dataManager;
    private Future<Void> listener;
    private ExecutorService executorService;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setMaxConnectionHandlers(ServerSocket serverSocket) {
        Platform.runLater(() -> MailServer.serverSocket = serverSocket);
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    /**
     *
     * @param stage stage principale dell'applicazione
     * @throws Exception
     *
     * Inizializza DataManager, Model, pool thread, ServerListener e la GUI
     * NB il numero di thread è basso in quanto eseguito in locale
     * Una implementazione reale dovrebbe prevedere più thread
     * Questo permetterebbe maggiore scalabilità a parità di performance
     * Si implementa il pool thread e non thread semplici per scalabilità
     */
    @Override
    public void start(Stage stage) throws Exception {
        dataManager = new DataManager(Paths.get(Objects.requireNonNull(getClass().getResource("/progiii/server/data/")).toURI()));
        new Model();
        executorService = Executors.newFixedThreadPool(MAX_CONNECTION_HANDLERS);
        listener = executorService.submit(new ServerListener(executorService), null);
        initWindow();
    }

    /**
     *
     * @throws IOException
     *
     * La GUI carica l’fxml imposta il listener per la chiusura
     * il listener si occupa di chiudere il ServerSocket, interrompere il pool thread e chiudere l’app
     */
    private void initWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/progiii/server/main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setOnCloseRequest((event) -> {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            listener.cancel(true);
            executorService.shutdown();
            Platform.exit();
        });
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/progiii/server/icon.png"))));
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
