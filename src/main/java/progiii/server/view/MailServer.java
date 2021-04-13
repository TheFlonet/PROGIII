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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MailServer extends Application {
    private static final int MAX_CONNECTION_HANDLERS = 5;
    private static ServerSocket serverSocket;
    private static DataManager dataManager;
    Future<Void> listener;
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

    @Override
    public void start(Stage stage) throws Exception {
        Map<String, String> runtimeParams = getParameters().getNamed();
        if (!runtimeParams.containsKey("datapath"))
            throw new IllegalArgumentException("Path to email folder is required");
        dataManager = new DataManager(Paths.get(runtimeParams.get("datapath")));
        int CONNECTION_HANDLERS;
        if (!runtimeParams.containsKey("max-handlers"))
            CONNECTION_HANDLERS = MAX_CONNECTION_HANDLERS;
        else {
            String handlers = runtimeParams.get("max-handlers");
            CONNECTION_HANDLERS = (handlers != null && Integer.parseInt(handlers) > 0) ?
                    Integer.parseInt(handlers) : MAX_CONNECTION_HANDLERS;
        }
        new Model();
        executorService = Executors.newFixedThreadPool(CONNECTION_HANDLERS);
        listener = executorService.submit(new ServerListener(executorService), null);
        initWindow();
    }

    private void initWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("progiii/server/main"));
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
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("progiii/server/icon.png"))));
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}