package progiii.client.concurrency.task;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import progiii.client.concurrency.Helper;
import progiii.client.controller.MainController;
import progiii.client.model.Model;
import progiii.common.data.Email;
import progiii.common.network.ResponseType;
import progiii.common.network.request.DeleteReq;
import progiii.common.network.response.Response;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DeleteTask implements Runnable {
    private final Set<Email> toDelete;
    private final String owner;
    private final ExecutorService executorService;

    public DeleteTask(Set<Email> toDelete, String owner, ExecutorService executorService) {
        this.toDelete = toDelete;
        this.owner = owner;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        FutureTask<Boolean> pullDelayer = new FutureTask<>(() -> Model.getInstance().pausePullReq(2));
        Platform.runLater(pullDelayer);
        try {
            if (!pullDelayer.get())
                Thread.sleep(2000);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        Callable<Optional<Response>> delete = Helper.prepare(new DeleteReq(owner, toDelete));
        Future<Optional<Response>> futureResponse = executorService.submit(delete);
        Response response;
        try {
            response = waitResponse(5, futureResponse).orElseThrow();
        } catch (NoSuchElementException e) {
            return;
        }
        if (response.getType() == ResponseType.ERROR)
            Platform.runLater(() -> new Alert(Alert.AlertType.WARNING, String.format("Error while cancelling emails %s", response.getStatus())).show());
        else if (response.getType() == ResponseType.DELETE) {
            System.out.println(response);
            Platform.runLater(() -> {
                MainController.getInstance().showStatusMsg("Emails deleted", Color.GREEN);
                toDelete.forEach(MainController.getInstance()::deleteEmail);
            });
        } else {
            Platform.runLater(() -> new Alert(Alert.AlertType.WARNING, "Internal error").show());
            throw new RuntimeException("Unhandled response type " + response.getType());
        }
    }

    private Optional<Response> waitResponse(int timeout, Future<Optional<Response>> future) {
        Consumer<String> error = (msg) -> new Alert(Alert.AlertType.WARNING, "Error while cancelling emails\n" + msg).show();

        return Helper.waitForAnswer(timeout, future, "Delete", error, error);
    }
}
