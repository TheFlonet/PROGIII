package progiii.client.concurrency.task;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import progiii.client.concurrency.Helper;
import progiii.client.controller.MainController;
import progiii.common.data.Email;
import progiii.common.network.ResponseType;
import progiii.common.network.request.CheckEmailReq;
import progiii.common.network.request.SendReq;
import progiii.common.network.response.EmailExistenceRes;
import progiii.common.network.response.Response;
import progiii.common.util.ValidatorCollector;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SendTask implements Runnable {
    private final ScheduledExecutorService executorService;
    private final Email toSend;
    private final Alert sendAlert;
    private final ValidatorCollector recipients;

    public SendTask(ScheduledExecutorService executorService, Email toSend, Alert sendAlert, ValidatorCollector recipients) {
        this.executorService = executorService;
        this.toSend = toSend;
        this.sendAlert = sendAlert;
        this.recipients = recipients;
    }

    /**
     *
     * Controlla se le mail di destinazione esistono (in modo asincrono)
     * Se esistono invia la mail a ogni destinatario
     * Se non esistono segnala l'errore
     */
    @Override
    public void run() {
        Consumer<String> error = (msg) -> {
            sendAlert.close();
            new Alert(Alert.AlertType.ERROR, msg).show();
        };

        CheckEmailReq existenceRequest = new CheckEmailReq(toSend.getFrom(), recipients.getEmails(true));
        Callable<Optional<Response>> callable = Helper.prepare(existenceRequest);
        Future<Optional<Response>> task = executorService.submit(callable);
        Optional<Response> response = waitResponse(task);
        EmailExistenceRes existenceResponse;

        try {
            existenceResponse = (EmailExistenceRes) response.orElseThrow();
        } catch (NoSuchElementException e) {
            Platform.runLater(() -> error.accept("Internal error"));
            e.printStackTrace();
            return;
        }

        EmailExistenceRes finalResponse = existenceResponse;
        String missEmails = existenceResponse.getResult().keySet().stream().filter(email -> !finalResponse.getResult()
                .get(email)).collect(Collectors.joining("\n\t"));

        if (!missEmails.isEmpty()) {
            Platform.runLater(() -> error.accept("Error, some emails do not exist"));
            return;
        }

        Callable<Optional<Response>> sendRequest = Helper.prepare(new SendReq(toSend));
        Future<Optional<Response>> future = executorService.submit(sendRequest);
        Optional<Response> response2 = waitResponse(future);
        Response sendResponse;
        try {
            sendResponse = response2.orElseThrow();
        } catch (NoSuchElementException e) {
            Platform.runLater(() -> error.accept("Internal error"));
            e.printStackTrace();
            return;
        }

        if (sendResponse.getType() == ResponseType.SEND)
            Platform.runLater(() -> {
                MainController.getInstance().closePopup(toSend, false);
                sendAlert.close();
                new Alert(Alert.AlertType.INFORMATION, "Email successfully sent").show();
            });
        else Platform.runLater(() -> error.accept(sendResponse.getStatus()));
    }

    /**
     *
     * @param optionalFuture task da eseguire in modo asincrono
     * @return
     *
     * Mostra un popup di errore al mancato ricevimento della risposta
     */
    private Optional<Response> waitResponse(Future<Optional<Response>> optionalFuture) {
        Consumer<String> error = (msg) -> {
            sendAlert.close();
            new Alert(Alert.AlertType.ERROR, msg).show();
        };

        return Helper.waitForAnswer(5, optionalFuture, "Sender", error, error);
    }
}
