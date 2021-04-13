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
    ExecutorService executorService;

    public ServerListener(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(NetworkConfig.SERVER_PORT)) {
            System.out.println("Connection opened, listening on port " + NetworkConfig.SERVER_PORT);
            MailServer.setMaxConnectionHandlers(serverSocket);
            while (!serverSocket.isClosed()) {
                try {
                    Socket connection = serverSocket.accept();
                    System.out.println("\n" + new Date() + " established connection " + connection.toString());
                    ConnectionHandler connectionHandler = new ConnectionHandler(connection);
                    executorService.submit(connectionHandler);
                } catch (SocketException e) {
                    if (e.getMessage().equals("Socket closed"))
                        System.out.println("Socket closed");
                    else
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
