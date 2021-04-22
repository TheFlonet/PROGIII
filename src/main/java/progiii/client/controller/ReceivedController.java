package progiii.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import progiii.client.concurrency.task.DeleteTask;
import progiii.client.model.Model;
import progiii.client.view.MailClient;
import progiii.common.data.Email;
import progiii.common.util.StringUtils;

import java.util.Date;
import java.util.Set;

/**
 *
 * Controller per la lettura di una email
 * Gestisce anche le varie opzioni (reply, reply all, forward, delete)
 */
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

    /**
     *
     * @param email
     *
     * Imposta i bind (mono o bi direzionali) tra controller e model
     */
    public void initialize(Email email) {
        super.initialize(email);
        tab.textProperty().bind(email.subjectProperty());
        ((Label) fromNode).textProperty().bind(email.fromProperty());
        ((Label) subjectNode).textProperty().bind(email.subjectProperty());
        ((Label) toNode).textProperty().bind(email.toProperty());
        ((Label) textNode).textProperty().bind(email.textProperty());
        date.setText(Email.DATE_FORMAT.format(email.getDate()));
    }

    /**
     *
     * @return
     *
     * Imposta il testo iniziale per una risposta
     */
    private String replyString() {
        return String.format(replyFormat, email.getFormattedDate(), email.getFrom(), email.getTo(), email.getSubject(), email.getText());
    }

    @Override
    public boolean isDraft() {
        return false;
    }

    /**
     *
     * @param event
     *
     * Inizia una richiesta di eliminazione
     */
    @FXML
    private void handleDelete(MouseEvent event) {
        MailClient.getInstance().getExecutorService().submit(
                new DeleteTask(Set.of(email), Model.getInstance().getEmail(), MailClient.getInstance().getExecutorService()));
    }

    /**
     *
     * @param event
     *
     * Apre una nuova tab per scrivere una email di risposta
     * Imposta già i campi (da, a, oggetto, testo di partenza)
     */
    @FXML
    private void handleReply(MouseEvent event) {
        Email replyEmail = new Email(-1, Model.getInstance().getEmail(), email.getFrom(),
                "Re: " + email.getSubject(), replyString(), new Date());
        Tab replyTab = MainController.getInstance().openNewEmailTab(replyEmail);
        MainController.getInstance().setCurrentTab(replyTab);
    }

    /**
     *
     * @param event
     *
     * Apre una nuova tab per inoltrare una email
     * Imposta già i campi (da, a, oggetto, testo di partenza)
     */
    @FXML
    private void handleForward(MouseEvent event) {
        Email forwardEmail = new Email(-1, Model.getInstance().getEmail(), "",
                "Fwd: " + email.getSubject(), replyString(), new Date());
        Tab forwardTab = MainController.getInstance().openNewEmailTab(forwardEmail);
        MainController.getInstance().setCurrentTab(forwardTab);
    }

    /**
     *
     * @param event
     *
     * Apre una nuova tab per scrivere una mail di risposta a più utenti
     * Imposta già i campi (da, a, oggetto, testo di partenza)
     */
    @FXML
    private void handleReplyAll(MouseEvent event) {
        Set<String> recipients = StringUtils.cleanEmailList(email.getTo()).getEmails(true);
        String localEmail = Model.getInstance().getEmail();
        recipients.remove(localEmail);
        if (!email.getFrom().equals(localEmail))
            recipients.add(email.getFrom());
        Email replyEmail = new Email(-1, Model.getInstance().getEmail(), String.join(", ", recipients),
                "Re: " + email.getSubject(), replyString(), new Date());
        Tab replyTab = MainController.getInstance().openNewEmailTab(replyEmail);
        MainController.getInstance().setCurrentTab(replyTab);
    }
}
