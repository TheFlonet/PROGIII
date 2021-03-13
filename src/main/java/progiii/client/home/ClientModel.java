package progiii.client.home;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import progiii.common.Email;
import progiii.common.EmailAddress;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {
    private SimpleBooleanProperty connected;
    private SimpleStringProperty status;
    private final EmailAddress email;
    private List<Email> messages;

    public ClientModel() {
        this.email = new EmailAddress();
        messages = new ArrayList<>();
        connected = new SimpleBooleanProperty();
        connected.set(false);
        status = new SimpleStringProperty();
        status.set("");
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public boolean isConnected() {
        return connected.get();
    }

    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    public SimpleBooleanProperty connectedProperty() {
        return connected;
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

    public ObservableValue<String> getStatusProperty() {
        return status;
    }

    public String getStatus() {
        return status.get();
    }
}
