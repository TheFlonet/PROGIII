package progiii.client.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import progiii.client.concurrency.GetEmail;
import progiii.common.data.Email;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Singleton con lista osservabile delle email
 * L'email locale
 * Il pool thread
 * Il service per il recupero delle email
 */
public class Model {
    private static Model INSTANCE;
    private final ObservableList<Email> receivedEmails = FXCollections.observableArrayList();
    private final SimpleStringProperty email = new SimpleStringProperty();
    private ScheduledExecutorService executorService;
    private GetEmail emailGetter;
    private final AtomicInteger idPlaceholder = new AtomicInteger();

    public Model() {
        if (INSTANCE != null)
            throw new IllegalStateException("Model has already been initialized");
        INSTANCE = this;
    }

    public static synchronized Model getInstance() {
        if (INSTANCE == null)
            throw new IllegalStateException("Model hasn't been initialized yet");
        return INSTANCE;
    }

    public int getIdPlaceholder() {
        return idPlaceholder.addAndGet(-1);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public SimpleStringProperty getEmailProperty() {
        return email;
    }

    /**
     *
     * @param executorService pool thread
     * @param emailGetter
     *
     * In mutex inizializza il pool thread e il getter delle email
     */
    public synchronized void initialize(ScheduledExecutorService executorService, GetEmail emailGetter) {
        if (this.executorService != null || this.emailGetter != null)
            throw new IllegalStateException("Model has already been initialized");
        this.executorService = executorService;
        this.emailGetter = emailGetter;
    }

    public ObservableList<Email> getReceivedEmails() {
        return receivedEmails;
    }

    public synchronized void startPullReq() {
        emailGetter.startService();
    }

    public synchronized void stopPullReq() {
        emailGetter.stopService();
    }

    public synchronized boolean pausePullReq(int delay) {
        return emailGetter.pauseService(delay);
    }
}
