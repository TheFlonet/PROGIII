package progiii.client;

import progiii.common.Email;
import progiii.common.EmailAddress;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {
    private final EmailAddress email;
    private List<Email> messages;

    public ClientModel(EmailAddress email) {
        this.email = email;
        messages = new ArrayList<>();
    }

    // TODO rimuovere (solo per test)
    public EmailAddress getEmail() {
        return email;
    }

    //TODO rimuovere (solo per test)
    public List<Email> getMessages() {
        return messages;
    }

    public void addMessage(Email email) {
        messages.add(email);
    }
}
