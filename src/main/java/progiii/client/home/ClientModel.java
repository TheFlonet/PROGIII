package progiii.client.home;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import progiii.client.writer.WriterView;
import progiii.common.Email;
import progiii.common.EmailAddress;

import java.util.ArrayList;
import java.util.List;

public class ClientModel {
    private final SimpleBooleanProperty isConnected;
    private final WriterView writer;
    private final StringProperty status;
    private final EmailAddress email;
    private final List<Email> messages;

    public ClientModel() {
        this.email = new EmailAddress();
        this.email.setEmail("mariobifulco@gmail.com"); // TODO cambiare indirizzo per avviare client diversi
        messages = new ArrayList<>();
        isConnected = new SimpleBooleanProperty();
        isConnected.set(false);
        status = new SimpleStringProperty();
        status.set("");
        writer = new WriterView();
    }

    public EmailAddress getEmail() {
        return email;
    }

    public WriterView getWriter() {
        return writer;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public boolean isConnected() {
        return isConnected.get();
    }

    public void setConnected(boolean connected) {
        this.isConnected.set(connected);
    }

    public SimpleBooleanProperty connectedProperty() {
        return isConnected;
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
