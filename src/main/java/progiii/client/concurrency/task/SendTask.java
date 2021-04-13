package progiii.client.concurrency.task;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import progiii.client.concurrency.Helper;
import progiii.common.data.Email;
import progiii.common.network.ResponseType;
import progiii.common.network.request.CheckEmail;
import progiii.common.network.request.SendReq;
import progiii.common.network.response.EmailExistence;
import progiii.common.network.response.Error;
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
    private ValidatorCollector recipients;

    public SendTask(ScheduledExecutorService executorService, Email toSend, Alert sendAlert, ValidatorCollector recipients) {
        this.executorService = executorService;
        this.toSend = toSend;
        this.sendAlert = sendAlert;
        this.recipients = recipients;
    }

    @Override
    public void run() {
        Consumer<String> error = (msg) -> {
            sendAlert.close();
            new Alert(Alert.AlertType.ERROR, msg).show();
        };

        CheckEmail existenceRequest = new CheckEmail(toSend.getFrom(), recipients.getEmails(true));
        Callable<Optional<Response>> callable = Helper.prepare(existenceRequest);
        Future<Optional<Response>> task = executorService.submit(callable);
        Optional<Response> response = waitResponse(task);
        EmailExistence existenceResponse = null;

        try {
            existenceResponse = (EmailExistence) response.orElseThrow();
        } catch (NoSuchElementException e) {
            Platform.runLater(() -> error.accept("Internal error"));
            e.printStackTrace();
            return;
        }

        System.out.println(existenceResponse);
        EmailExistence finalResponse = existenceResponse;
        String missEmails = existenceResponse.getResult().keySet().stream().filter(email -> !finalResponse.getResult()
                .get(email)).collect(Collectors.joining("\n\t"));

        if (!missEmails.isEmpty()) {
            Platform.runLater(() -> error.accept("Error, some emails do not exist"));
            return;
        }

        Callable<Optional<Response>> sendRequest = Helper.prepare(new SendReq(toSend));
        Future<Optional<Response>> future = executorService.submit(sendRequest);
        Optional<Response> response2 = waitResponse(future);
        Response sendResponse = null;
        try {
            sendResponse = response2.orElseThrow();
        } catch (NoSuchElementException e) {
            Platform.runLater(() -> error.accept("Internal error"));
            e.printStackTrace();
            return;
        }

        if (sendResponse.getType() == ResponseType.SEND)
            Platform.runLater(() -> {
                MainController.getInstance().closeEmailTab(toSend, false);
                sendAlert.close();
                new Alert(Alert.AlertType.INFORMATION, "Email successfully sent").show();
            });
        else {
            Error errorResponse = (Error) sendResponse;
            Platform.runLater(() -> error.accept(errorResponse.getStatus()));
        }
    }

    private Optional<Response> waitResponse(Future<Optional<Response>> optionalFuture) {
        Consumer<String> error = (msg) -> {
            sendAlert.close();
            new Alert(Alert.AlertType.ERROR, msg).show();
        };

        return Helper.waitForAnswer(5, optionalFuture, "Sender", error, error);
    }
}
