package progiii.client.concurrency;

import javafx.application.Platform;
import progiii.common.network.NetworkConfig;
import progiii.common.network.request.Request;
import progiii.common.network.response.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 *
 * Classe di supporto per le richieste asincrone al server
 */
public class Helper {
    /**
     *
     * @param request richiesta per il server
     * @return
     *
     * Apre il socket verso il server, scrive la richiesta e aspetta la risposta (opzionale)
     */
    public static Callable<Optional<Response>> prepare(Request request) {
        return () -> {
            try (Socket s = new Socket(NetworkConfig.REMOTE_IP, NetworkConfig.SERVER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
                out.writeObject(request);
                return Optional.of((Response) in.readObject());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        };
    }

    /**
     *
     * @param timeout tempo limite per l'esecuzione del task
     * @param optionalFuture task da eseguire
     * @param caller stringa identificativa del chiamante
     * @param errorHandler gestore degli errori
     * @param timeoutHandler gestore in caso di timeout
     * @return
     *
     * Aspetta una potenziale risposta dal server, se non riceve nulla ritorna una risposta vuota
     */
    public static Optional<Response> waitForAnswer(int timeout, Future<Optional<Response>> optionalFuture,
                                                   String caller, Consumer<String> errorHandler,
                                                   Consumer<String> timeoutHandler) {
        Optional<Response> response = Optional.empty();
        try {
            response = optionalFuture.get(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println(caller + " service interrupted");
        } catch (ExecutionException e) {
            String msg;
            if (e.getCause() instanceof ConnectException || e.getCause() instanceof UnknownHostException) {
                msg = e.getCause().getMessage();
                if (msg.equals("Connection refused"))
                    msg = "Server unreachable";
            } else {
                e.printStackTrace();
                msg = e.getMessage();
            }

            final String finalMsg = msg;
            Platform.runLater(() -> errorHandler.accept("Error: " + finalMsg));
        } catch (TimeoutException e) {
            optionalFuture.cancel(true);
            Platform.runLater(() -> timeoutHandler.accept("Error, server unreachable"));
        }
        return response;
    }
}
