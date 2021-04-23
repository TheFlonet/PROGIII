package progiii.client.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import progiii.client.model.Model;
import progiii.client.view.MailClient;
import progiii.common.data.Email;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;

/**
 *
 * Controller principale con lista delle email, gestisce le varie tab dell'applicazione
 */
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

    /**
     *
     * @param controller
     *
     * In mutex inizializza il controller
     */
    private static synchronized void setSingleton(MainController controller) {
        if (INSTANCE == null)
            INSTANCE = controller;
        else throw new RuntimeException("Controller can only be initialized once");
    }

    /**
     *
     * @param location
     * @param resources
     *
     * Inizializza il model, la grafica delle tab apribili, la factory per le celle
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = Model.getInstance();
        receivedStructure = getClass().getResource("/progiii/client/received.fxml");
        draftStructure = getClass().getResource("/progiii/client/draft.fxml");
        receivedEmails.setCellFactory(listView -> new CellController());
        receivedEmails.setItems(model.getReceivedEmails().sorted());
        receivedEmails.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     *
     * @param event
     *
     * Gestisce il doppio click su una cella della listView
     * Apre una tab per visualizzare l'email
     */
    @FXML
    private void handleEmailClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY))
            if (event.getClickCount() == 2) {
                Email selected = receivedEmails.getSelectionModel().getSelectedItem();
                Tab newTab = openReceivedTab(selected);
                if (newTab != null)
                    setCurrentTab(newTab);
            }
    }

    /**
     *
     * @param msg
     *
     * Mostra l'errore colorandolo di rosso
     */
    public void showStatusError(String msg) {
        showStatusMsg(msg, Color.RED);
    }

    /**
     *
     * @param msg
     * @param color
     *
     * Mostra un generico messaggio colorandolo
     */
    public void showStatusMsg(String msg, Color color) {
        Date date = new Date();
        status.setText(String.format("%tH:%tM - %s", date, date, msg));
        status.setTextFill(color);
    }

    /**
     *
     * Resetta la label di stato
     */
    public void clearStatusMsg() {
        status.setText("");
    }

    /**
     *
     * @return
     *
     * Apre una tab per scrivere l'email
     */
    @FXML
    private Tab openNewEmailTab() {
        Email newEmail = new Email(-1, "from@me.com", "", "Nuova Email", "", new Date());
        Tab newEmailTab = openEmailTab(newEmail, draftStructure, this::closeTab);
        setCurrentTab(newEmailTab);
        return newEmailTab;
    }

    public Tab openNewEmailTab(Email email) {
        return openEmailTab(email, draftStructure, this::closeTab);
    }

    /**
     *
     * @param selected
     * @return
     *
     * Apre una tab per leggere un'email
     */
    private Tab openReceivedTab(Email selected) {
        if (!openEmails.containsKey(selected))
            return openEmailTab(selected, receivedStructure, this::closeTab);
        else return null;
    }

    /**
     *
     * @param curr
     *
     * Sposta il focus del tab pane sulla tab appena aperta
     */
    public void setCurrentTab(Tab curr) {
        tabPane.getSelectionModel().select(curr);
    }

    /**
     *
     * @param email
     * @param tabStructure
     * @param closerMethod
     * @return
     *
     * Apre una generica tab e imposta il listener per la chiusura
     */
    private Tab openEmailTab(Email email, URL tabStructure, BiConsumer<Email, Event> closerMethod) {
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

    /**
     *
     * @param selected
     *
     * Chiude la tab e cancella l'email in scrittura
     */
    public void deleteEmail(Email selected) {
        closeTab(selected);
        model.getReceivedEmails().remove(selected);
    }

    /**
     *
     * @param selected
     * @param event
     *
     * Chiude la tab chiedendo prima all'utente conferma con un popup
     */
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

    /**
     *
     * @param prompt
     * @return
     *
     * Chiude tutte le tab aperte
     */
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

    /**
     *
     * @param event
     *
     * Gestisce la richiesta di logout
     * NB il logout è solo lato client, il server non si aspetta logout
     * La richiesta di autenticazione iniziale serve solo per garantire che la mail esista
     */
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
