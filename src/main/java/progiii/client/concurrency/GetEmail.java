package progiii.client.concurrency;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import progiii.client.controller.MainController;
import progiii.client.model.Model;
import progiii.common.data.Email;
import progiii.common.network.NetworkConfig;
import progiii.common.network.request.PullReq;
import progiii.common.network.request.Request;
import progiii.common.network.response.NewEmailRes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.rmi.UnexpectedException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GetEmail implements Runnable {
    private final ScheduledExecutorService executorService;
    private final SimpleStringProperty emailAddr;
    private ScheduledFuture<?> currentExec = null;
    private int failedConnectionTries = 0;

    public GetEmail(ScheduledExecutorService executorService, SimpleStringProperty emailAddr) {
        this.executorService = executorService;
        this.emailAddr = emailAddr;
    }

    /**
     *
     * Apre il socket per comunicare con il server, il flusso in input e in output
     * Invia una richiesta di pull con gli id delle mail già scaricate
     * In modo asincrono aggiorna la lista
     * Se non riesce a connettersi periodicamente riprova
     */
    @Override
    public void run() {
        try (Socket s = new Socket(NetworkConfig.REMOTE_IP, NetworkConfig.SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

            Set<Integer> localIds = Model.getInstance().getReceivedEmails().stream()
                    .map(Email::getId).collect(Collectors.toCollection(HashSet::new));
            Request request = new PullReq(emailAddr.getValue(), localIds);
            out.writeObject(request);

            NewEmailRes response = (NewEmailRes) in.readObject();

            int oldTries = resetTries();
            FutureTask<Void> task = new FutureTask<>(() -> {
                Model.getInstance().getReceivedEmails().addAll(response.getEmailSet());
                if (response.getEmailSet().size() > 0)
                    MainController.getInstance().showStatusMsg(String.format("%d new emails", response.getEmailSet().size()), Color.BLACK);
                else if (oldTries != 0)
                    MainController.getInstance().showStatusMsg("Connection restored. No new emails received", Color.BLACK);
                return null;
            });

            Platform.runLater(task);
            task.get();
        } catch (UnexpectedException | ConnectException e) {
            int tries = ++failedConnectionTries;
            Platform.runLater(() -> {
                if (tries == 1)
                    new Alert(Alert.AlertType.WARNING, "Can't connect to server\nError: " + e.getMessage()).show();
                MainController.getInstance().showStatusError(String.format("Failed %d connection tries", tries));
            });
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for a task");
        } catch (IOException | ClassNotFoundException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (!executorService.isShutdown())
                nextRequest();
        }
    }

    /**
     *
     * Inizializza la richiesta periodica di email al server garantendo mutex
     */
    public synchronized void startService() {
        if (currentExec == null || currentExec.isDone())
            currentExec = executorService.schedule(this, NetworkConfig.PULL_PERIOD, TimeUnit.SECONDS);
        else throw new IllegalStateException("Pull service already started");
    }

    /**
     *
     * In mutex passa alla prossima richiesta
     */
    public synchronized void nextRequest() {
        scheduleNextRequest(NetworkConfig.PULL_PERIOD);
    }

    /**
     *
     * @param pullPeriod delay dopo il quale eseguire il task
     *
     * In mutex imposta la prossima richiesta dopo pullPeriod secondi
     */
    private synchronized void scheduleNextRequest(int pullPeriod) {
        if (currentExec != null)
            currentExec = executorService.schedule(this, pullPeriod, TimeUnit.SECONDS);
        else throw new IllegalStateException("Pull service isn't running");
    }

    /**
     *
     * In mutex interrompe il servizio
     */
    public synchronized void stopService() {
        if (currentExec != null)
            currentExec.cancel(true);
        else throw new IllegalStateException("Pull service isn't initialized");

    }

    /**
     *
     * @param delay interrompe il servizio di pull per x secondi
     * @return
     *
     * In mutex mette in pausa il servizio per delay secondi
     */
    public synchronized boolean pauseService(int delay) {
        if (currentExec != null) {
            int timeLeft = (int) currentExec.getDelay(TimeUnit.SECONDS);
            if (currentExec.cancel(false)) {
                scheduleNextRequest(timeLeft + delay);
                return true;
            }
            return false;
        } else throw new IllegalStateException("Pull service isn't initialized");
    }

    /**
     *
     * @return
     *
     * Resetta i tentativi di connessione
     */
    private int resetTries() {
        int old = failedConnectionTries;
        failedConnectionTries = 0;
        return old;
    }
}