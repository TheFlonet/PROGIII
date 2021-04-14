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

    private static void applySizeConstraints(final Stage stage) {
        final Parent root = stage.getScene().getRoot();
        stage.sizeToScene();
        final double deltaW = stage.getWidth() - root.getLayoutBounds().getWidth();
        final double deltaH = stage.getHeight() - root.getLayoutBounds().getHeight();
        final Bounds minBounds = getBounds(root, Node::minWidth, Node::minHeight);
        stage.setMinWidth(minBounds.getWidth() + deltaW);
        stage.setMinHeight(minBounds.getHeight() + deltaH);
        final Bounds prefBounds = getBounds(root, Node::prefWidth, Node::prefHeight);
        stage.setWidth(prefBounds.getWidth() + deltaW);
        stage.setHeight(prefBounds.getHeight() + deltaH);
    }

    private static Bounds getBounds(final Node node, final BiFunction<Node, Double, Double> widthFunction,
                                    final BiFunction<Node, Double, Double> heightFunction) {
        final Orientation bias = node.getContentBias();
        double prefWidth;
        double prefHeight;
        if (bias == Orientation.HORIZONTAL) {
            prefWidth = widthFunction.apply(node, (double) -1);
            prefHeight = heightFunction.apply(node, prefWidth);
        } else if (bias == Orientation.VERTICAL) {
            prefHeight = heightFunction.apply(node, (double) -1);
            prefWidth = widthFunction.apply(node, prefHeight);
        } else {
            prefWidth = widthFunction.apply(node, (double) -1);
            prefHeight = heightFunction.apply(node, (double) -1);
        }
        return new BoundingBox(0, 0, prefWidth, prefHeight);
    }

    public static MailClient getInstance() {
        return INSTANCE;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

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
        applySizeConstraints(stage);
        return stage;
    }

    public void showMain() {
        mainWindow.show();
        MainController.getInstance().clearStatusMsg();
    }

    public void hideMain() {
        mainWindow.hide();
    }

    public void setMainTitle(String title) {
        mainWindow.setTitle(title);
    }

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

    public void showLogin() {
        loginWindow.show();
        loginWindow.setAlwaysOnTop(true);
        loginController.setEmailText("example@email.com");
    }

    public void hideLogin() {
        loginWindow.setAlwaysOnTop(false);
        loginWindow.hide();
    }

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
