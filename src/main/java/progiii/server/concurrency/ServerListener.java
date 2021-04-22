package progiii.server.concurrency;

import progiii.common.network.NetworkConfig;
import progiii.server.view.MailServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.concurrent.ExecutorService;

public class ServerListener implements Runnable {
    private final ExecutorService executorService;

    public ServerListener(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     *
     * Inizializza il server socket
     * Fin quando non viene chiuso si mette in ascolto per un client
     * Quando trova una richiesta la inoltra al connection handler
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConfig.SERVER_PORT)) {
            MailServer.setMaxConnectionHandlers(serverSocket);
            while (!serverSocket.isClosed()) {
                try {
                    Socket connection = serverSocket.accept();
                    ConnectionHandler connectionHandler = new ConnectionHandler(connection);
                    executorService.submit(connectionHandler);
                } catch (SocketException e) {
                    if (!e.getMessage().equals("Socket closed"))
                        e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
