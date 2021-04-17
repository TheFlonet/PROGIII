package progiii.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import progiii.client.concurrency.task.SendTask;
import progiii.client.model.Model;
import progiii.client.view.MailClient;
import progiii.common.data.Email;
import progiii.common.util.StringUtils;
import progiii.common.util.ValidatorCollector;

import java.util.Date;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;

public class NewEmailController extends TabController {
    @Override
    public void initialize(Email email) {
        super.initialize(email);
        tab.textProperty().bind(((TextField) subjectNode).textProperty());
        ((TextField) subjectNode).textProperty().bindBidirectional(email.subjectProperty());
        email.fromProperty().bindBidirectional(Model.getInstance().getEmailProperty());
        ((Label) fromNode).textProperty().bindBidirectional(email.fromProperty());
        ((TextField) toNode).textProperty().bindBidirectional(email.toProperty());
        ((TextArea) textNode).textProperty().bindBidirectional(email.textProperty());
    }

    @Override
    public boolean isDraft() {
        return true;
    }

    @FXML
    private void handleSend(MouseEvent event) {
        String error = null;
        ValidatorCollector results = null;
        try {
            results = StringUtils.cleanEmailList(email.getTo());
        } catch (IllegalArgumentException e) {
            error = e.getMessage();
        }

        if (error == null && email.getTo().isEmpty())
            error = "An email must have at least one recipient";
        if (error != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, error);
            alert.showAndWait();
            return;
        }
        if (results == null) {
            throw new RuntimeException();
        }
        if (!results.areAllEmailsGood()) {
            error = "Malformed email(s):\n";
            error += results.emailsString(false);
            Alert alert = new Alert(Alert.AlertType.ERROR, error);
            alert.showAndWait();
            return;
        }
        email.setDate(new Date());
        ScheduledExecutorService service = MailClient.getInstance().getExecutorService();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sending email", ButtonType.CANCEL);
        FutureTask<Void> sender = new FutureTask<>(new SendTask(service, email, alert, results), null);
        alert.setOnCloseRequest(closeEvent -> sender.cancel(true));
        service.submit(sender);
        alert.show();
    }

    @FXML
    private void handleDeleteClick(MouseEvent event) {
        MainController.getInstance().deleteEmail(email);
    }
}
