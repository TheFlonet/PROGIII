package progiii.client.concurrency.task;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import progiii.client.concurrency.Helper;
import progiii.client.controller.LoginController;
import progiii.client.model.Model;
import progiii.client.view.MailClient;
import progiii.common.network.ResponseType;
import progiii.common.network.request.AuthReq;
import progiii.common.network.request.CheckEmailReq;
import progiii.common.network.response.EmailExistenceRes;
import progiii.common.network.response.ErrorRes;
import progiii.common.network.response.NewEmailRes;
import progiii.common.network.response.Response;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class LoginTask implements Runnable {
    private final ScheduledExecutorService executorService;
    private final LoginController controller;

    public LoginTask(ScheduledExecutorService executorService, LoginController controller) {
        this.executorService = executorService;
        this.controller = controller;
    }

    /**
     *
     * Ottiene l'email del client attuale
     * Invia una richiesta asincrona al server per verificare se esiste la casella
     * Se ritorna errore lo mostra a finestra
     * Se l'indirizzo è ben formato ma non esiste chiede di crearlo
     * Se l'email esiste o si sceglie di crearla viene mandata una richiesta di autenticazione
     */
    @Override
    public void run() {
        String email;
        try {
            FutureTask<String> emailGetter = new FutureTask<>(controller::getEmailText);
            Platform.runLater(emailGetter);
            email = emailGetter.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        Callable<Optional<Response>> check = Helper.prepare(new CheckEmailReq(email));
        Future<Optional<Response>> futureTask = executorService.submit(check);
        Response response;
        try {
            response = waitResponse(futureTask).orElseThrow();
        } catch (NoSuchElementException e) {
            return;
        }

        ResponseType type = response.getType();
        if (type == ResponseType.ERROR) {
            ErrorRes finalResponse = (ErrorRes) response;
            Platform.runLater(() -> {
                controller.showMsg(String.format("Error %s", finalResponse.getStatus()));
                controller.toggleInterface(true);
            });
        } else if (type == ResponseType.EMAIL_EXISTENCE) {
            EmailExistenceRes finalResponse = (EmailExistenceRes) response;
            boolean emailExist = finalResponse.getResult(email);
            Platform.runLater(() -> {
                controller.showMsg(String.format("Mail %s", emailExist ? "exist" : "doesn't exist"));
                controller.toggleInterface(true);
            });
            Optional<ButtonType> choice = Optional.empty();
            if (!emailExist) {
                FutureTask<Optional<ButtonType>> dialog = new FutureTask<>(() -> {
                    Alert creationAlert = new Alert(Alert.AlertType.CONFIRMATION, "New mail, create now?", ButtonType.YES, ButtonType.CANCEL);
                    creationAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    ((Stage) creationAlert.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
                    return creationAlert.showAndWait();
                });
                Platform.runLater(dialog);

                try {
                    choice = dialog.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }

            if (emailExist || choice.orElse(ButtonType.CANCEL) == ButtonType.YES) {
                Callable<Optional<Response>> auth = Helper.prepare(new AuthReq(email));
                Future<Optional<Response>> futureTask2 = executorService.submit(auth);
                Response response2;
                try {
                    response2 = waitResponse(futureTask2).orElseThrow();
                } catch (NoSuchElementException e) {
                    return;
                }

                ResponseType type2 = response2.getType();
                if (type2 == ResponseType.ERROR)
                    Platform.runLater(() -> {
                        controller.showMsg(String.format("Error %s", response2.getStatus()));
                        controller.toggleInterface(true);
                    });
                else if (type2 == ResponseType.NEW_EMAILS)
                    Platform.runLater(() -> {
                        controller.hideMsg();
                        Model model = Model.getInstance();
                        model.getReceivedEmails().addAll(((NewEmailRes) response2).getEmailSet());
                        model.setEmail(email);
                        model.startPullReq();
                        MailClient client = MailClient.getInstance();
                        client.setMainTitle(email);
                        client.hideLogin();
                        client.showMain();
                    });
                else {
                    Platform.runLater(() -> {
                        controller.showMsg("Internal error");
                        controller.toggleInterface(true);
                    });
                    throw new RuntimeException("Unhandled response type" + response2.getType());
                }
            } else {
                Platform.runLater(() -> {
                    controller.hideMsg();
                    controller.toggleInterface(true);
                });
            }
        } else {
            Platform.runLater(() -> {
                controller.showMsg("Internal error");
                controller.toggleInterface(true);
            });
            throw new RuntimeException("Unhandled response Type: " + response.getType());
        }
    }

    /**
     *
     * @param task task da eseguire in modo asincrono
     * @return
     *
     * Mostra un popup di errore al mancato ricevimento della risposta
     */
    private Optional<Response> waitResponse(Future<Optional<Response>> task) {
        Consumer<String> error = (msg) -> {
            controller.showMsg(msg);
            controller.toggleInterface(true);
        };
        return Helper.waitForAnswer(5, task, "Access", error, error);
    }
}
