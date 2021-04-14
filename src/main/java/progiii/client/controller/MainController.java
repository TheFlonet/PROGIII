package progiii.client.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import progiii.client.concurrency.task.DeleteTask;
import progiii.client.model.Model;
import progiii.client.view.MailClient;
import progiii.common.data.Email;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

public class MainController implements Initializable {
    private static MainController INSTANCE;
    @FXML
    public ListView<Email> receivedEmails;
    @FXML
    public TabPane tabPane;
    @FXML
    public Label status;
    private final Map<Email, TabController> openEmails = new HashMap<>();
    private Model model;
    private URL receivedStructure;
    private URL draftStructure;

    public MainController() {
        setSingleton(this);
    }

    public static MainController getInstance() {
        return INSTANCE;
    }

    private static synchronized void setSingleton(MainController controller) {
        if (INSTANCE == null)
            INSTANCE = controller;
        else throw new RuntimeException("Controller can only be initialized once");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = Model.getInstance();
        receivedStructure = getClass().getResource("/progiii/client/received.fxml");
        draftStructure = getClass().getResource("/progiii/client/draft.fxml");
        receivedEmails.setCellFactory(listView -> new CellController());
        receivedEmails.setItems(model.getReceivedEmails().sorted());
        receivedEmails.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void handleEmailClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY))
            if (event.getClickCount() == 2) {
                Email selected = receivedEmails.getSelectionModel().getSelectedItem();
                Tab newTab = openEmailsTab(selected);
                if (newTab != null)
                    setCurrentTab(newTab);
            }
    }

    @FXML
    private void handleEmailTyped(KeyEvent event) {
        if (new KeyCodeCombination(KeyCode.ENTER).match(event))
            for (Email selected : receivedEmails.getSelectionModel().getSelectedItems()) {
                Tab newTab = openEmailsTab(selected);
                if (newTab != null)
                    setCurrentTab(newTab);
            }
        else if (new KeyCodeCombination(KeyCode.DELETE).match(event)) {
            Set<Email> selected = Set.copyOf(receivedEmails.getSelectionModel().getSelectedItems());
            MailClient.getInstance().getExecutorService().submit(new DeleteTask(selected, Model.getInstance().getEmail(), MailClient.getInstance().getExecutorService()));
        }
    }

    public void showStatusError(String msg) {
        showStatusMsg(msg, Color.RED);
    }

    public void showStatusMsg(String msg, Color color) {
        Date date = new Date();
        status.setText(String.format("%tH:%tM - %s", date, date, msg));
        status.setTextFill(color);
    }

    public void clearStatusMsg() {
        status.setText("");
    }

    @FXML
    private Tab openEmailTab() {
        Email newEmail = new Email(-1, "from@me.com", "", "Nuova Email", "", new Date());
        Tab newEmailTab = openTab(newEmail, draftStructure, this::closeTab);
        setCurrentTab(newEmailTab);
        return newEmailTab;
    }

    public Tab openEmailTab(Email email) {
        return openTab(email, draftStructure, this::closeTab);
    }

    private Tab openEmailsTab(Email selected) {
        if (!openEmails.containsKey(selected))
            return openTab(selected, receivedStructure, this::closeTab);
        else return null;
    }

    public void setCurrentTab(Tab curr) {
        tabPane.getSelectionModel().select(curr);
    }

    private Tab openTab(Email email, URL tabStructure, BiConsumer<Email, Event> closerMethod) {
        FXMLLoader loader = new FXMLLoader(tabStructure);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TabController controller = loader.getController();
        controller.initialize(email);
        openEmails.put(email, controller);
        Tab newTab = controller.getTab();
        tabPane.getTabs().add(newTab);
        newTab.setOnCloseRequest(event -> closerMethod.accept(email, event));
        return newTab;
    }

    public void deleteEmail(Email selected) {
        closeTab(selected);
        model.getReceivedEmails().remove(selected);
    }

    private void closeTab(Email selected, Event event) {
        closeTab(selected);
        event.consume();
    }

    private boolean closeTab(Email selected) {
        return closePopup(selected, true);
    }

    public boolean closePopup(Email selected, boolean askClosing) {
        if (openEmails.containsKey(selected)) {
            TabController controller = openEmails.get(selected);
            Alert alert = null;
            ButtonType yes = new ButtonType("Discart");
            if (askClosing && controller.isDraft()) {
                alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to discart this draft?", yes, new ButtonType("Cancel"));
                alert.showAndWait();
            }
            if (alert == null || alert.getResult() == yes) {
                tabPane.getTabs().remove(controller.getTab());
                openEmails.remove(selected);
                return true;
            } else return false;
        }
        return false;
    }

    public boolean closeAll(String prompt) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, prompt);
        ButtonType choice = confirmationAlert.showAndWait().orElse(ButtonType.CANCEL);
        if (choice == ButtonType.OK) return closeAll();
        else return false;
    }

    public boolean closeAll() {
        Email[] emails = openEmails.keySet().toArray(Email[]::new);
        for (Email email : emails)
            if (!closeTab(email))
                return false;
        return true;
    }

    @FXML
    private void logoutClick(MouseEvent event) {
        if (closeAll("Do you want to logout?")) {
            Model.getInstance().stopPullReq();
            model.getReceivedEmails().clear();
            MailClient.getInstance().showLogin();
            MailClient.getInstance().hideMain();
        }
    }
}
