package progiii.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import progiii.client.concurrency.task.LoginTask;

import java.util.concurrent.ScheduledExecutorService;

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

    @FXML
    public void loginButtonClick(MouseEvent event) {
        toggleInterface(false);
        showMsg("Connection attempt");
        executorService.submit(new LoginTask(executorService, this));
    }
}
