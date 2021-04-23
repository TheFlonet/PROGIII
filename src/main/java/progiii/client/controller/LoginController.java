package progiii.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import progiii.client.concurrency.task.LoginTask;
import progiii.common.util.StringUtils;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Gestisce la finestra di login
 */
public class LoginController {
    private ScheduledExecutorService executorService;
    @FXML
    private Label status;
    @FXML
    private TextField email;
    @FXML
    private Button loginBtn;

    public String getEmailText() {
        return email.getText();
    }

    public void setEmailText(String email) {
        if (email.equals("example@email.com"))
            this.email.setPromptText(email);
        else
            this.email.setText(email);
    }

    public void initialize(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    public void toggleInterface(boolean enabled) {
        email.setDisable(!enabled);
        loginBtn.setDisable(!enabled);
    }

    public void showMsg(String msg) {
        if (msg != null && !msg.isEmpty())
            status.setText(msg);
        status.setVisible(true);
    }

    public void hideMsg() {
        status.setVisible(false);
    }

    /**
     *
     * @param event
     *
     * Gestisce la richiesta di login
     * Se non viene inserita l'email o non è valida non prosegue
     */
    @FXML
    public void loginButtonClick(MouseEvent event) {
        if (email.getText().isBlank() || email.getText().isEmpty() || !StringUtils.isValid(email.getText()))
            showMsg("Invalid email");
        else {
            toggleInterface(false);
            showMsg("Connection attempt");
            executorService.submit(new LoginTask(executorService, this));
        }
    }
}
