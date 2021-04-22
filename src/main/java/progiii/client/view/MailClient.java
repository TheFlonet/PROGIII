package progiii.client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import progiii.client.concurrency.GetEmail;
import progiii.client.controller.LoginController;
import progiii.client.controller.MainController;
import progiii.client.model.Model;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class MailClient extends Application {
    private static MailClient INSTANCE;
    private ScheduledExecutorService executorService;
    private Stage loginWindow;
    private LoginController loginController;
    private Stage mainWindow;

    public static void main(String[] args) {
        launch(args);
    }

    public static MailClient getInstance() {
        return INSTANCE;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    /**
     *
     * @param stage
     * @throws Exception
     *
     * Associa l’istanza caricata a quella per la singleton
     * Inizializza il model
     * Inizializza il pool thread (per gestire le attività possibili in parallelo)
     * Inizializza il getter delle email, la finestra di login e quella principale
     * Infine mostra la finestra di login
     */
    @Override
    public void start(Stage stage) throws Exception {
        INSTANCE = this;
        Model model = new Model();
        executorService = Executors.newScheduledThreadPool(5);
        GetEmail emailGetter = new GetEmail(executorService, model.getEmailProperty());
        model.initialize(executorService, emailGetter);
        loginWindow = initLogin();
        mainWindow = initMain();
        showLogin();
    }

    /**
     *
     * @return
     * @throws IOException
     *
     * Carica la scena della schermata principale e imposta il listener per la chiusura
     */
    public Stage initMain() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/progiii/client/main.fxml"));
        Scene scene = new Scene(loader.load());
        MainController mainController = loader.getController();
        stage.setOnCloseRequest((event) -> {
            if (!mainController.closeAll("Do you want to exit?"))
                event.consume();
            else
                Model.getInstance().stopPullReq();
        });
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/progiii/client/icon.png"))));
        stage.setScene(scene);
        stage.sizeToScene();
        return stage;
    }

    /**
     *
     * Mostra la finestra principale
     */
    public void showMain() {
        mainWindow.show();
        MainController.getInstance().clearStatusMsg();
    }

    /**
     *
     * Nasconde la finestra principale
     */
    public void hideMain() {
        mainWindow.hide();
    }

    /**
     *
     * @param title
     *
     * Imposta il titolo della finestra principale (mail con cui si è loggati)
     */
    public void setMainTitle(String title) {
        mainWindow.setTitle(title);
    }

    /**
     *
     * @return
     * @throws IOException
     *
     * Carica la finestra di login da xml
     */
    public Stage initLogin() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/progiii/client/login.fxml"));
        Scene scene = new Scene(loader.load());
        loginController = loader.getController();
        loginController.initialize(executorService);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.initStyle(StageStyle.UTILITY);
        return stage;
    }

    /**
     *
     * Mostra la finestra di login come popup
     */
    public void showLogin() {
        loginWindow.show();
        loginWindow.setAlwaysOnTop(true);
        loginController.setEmailText("example@email.com");
    }

    /**
     *
     * Nasconde la finestra di login
     */
    public void hideLogin() {
        loginWindow.setAlwaysOnTop(false);
        loginWindow.hide();
    }

    /**
     *
     * @throws Exception
     *
     * Termina il pool thread
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS))
                executorService.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
