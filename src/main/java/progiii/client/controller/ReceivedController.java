package progiii.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import progiii.client.concurrency.task.DeleteTask;
import progiii.client.model.Model;
import progiii.client.view.MailClient;
import progiii.common.data.Email;
import progiii.common.util.StringUtils;

import java.awt.*;
import java.util.Date;
import java.util.Set;

public class ReceivedController extends TabController {
    private static final String replyFormat = """
                        
            ---
            %s
            From: %s
            To: %s
            Subject: %s
                        
            %s
            """;

    @FXML
    private Label date;

    public void initialize(Email email) {
        super.initialize(email);
        tab.textProperty().bind(email.subjectProperty());
        ((javafx.scene.control.Label) fromNode).textProperty().bind(email.fromProperty());
        ((javafx.scene.control.Label) subjectNode).textProperty().bind(email.subjectProperty());
        ((javafx.scene.control.Label) toNode).textProperty().bind(email.toProperty());
        ((javafx.scene.control.Label) textNode).textProperty().bind(email.textProperty());
        date.setText(Email.DATE_FORMAT.format(email.getDate()));
    }

    private String replyString() {
        return String.format(replyFormat, email.getDate(), email.getFrom(), email.getTo(), email.getSubject(), email.getText());
    }

    @Override
    public boolean isDraft() {
        return false;
    }

    @FXML
    private void handleDelete(MouseEvent event) {
        MailClient.getInstance().getExecutorService().submit(new DeleteTask(Set.of(email), Model.getInstance().getEmail(), MailClient.getInstance().getExecutorService()));
    }

    @FXML
    private void handleReply(MouseEvent event) {
        Email replyEmail = new Email(-1, Model.getInstance().getEmail(), email.getFrom(),
                "Re: " + email.getSubject(), replyString(), new Date());
        Tab replyTab = MainController.getInstance().openEmailTab(replyEmail);
        MainController.getInstance().setCurrentTab(replyTab);
    }

    @FXML
    private void handleForward(MouseEvent event) {
        Email forwardEmail = new Email(-1, Model.getInstance().getEmail(), "",
                "Fwd: " + email.getSubject(), replyString(), new Date());
        Tab forwardTab = MainController.getInstance().openEmailTab(forwardEmail);
        MainController.getInstance().setCurrentTab(forwardTab);
    }

    @FXML
    private void handleReplyAll(MouseEvent event) {
        Set<String> recipients = StringUtils.cleanEmailList(email.getTo()).getEmails(true);
        String localEmail = Model.getInstance().getEmail();
        recipients.remove(localEmail);
        if (!email.getFrom().equals(localEmail))
            recipients.add(email.getFrom());
        Email replyEmail = new Email(-1, Model.getInstance().getEmail(), String.join(", ", recipients),
                "Re: " + email.getSubject(), replyString(), new Date());
        Tab replyTab = MainController.getInstance().openEmailTab(replyEmail);
        MainController.getInstance().setCurrentTab(replyTab);
    }
}
