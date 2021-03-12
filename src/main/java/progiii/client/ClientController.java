package progiii.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientController {
    public static void main(String[] args) {
        try (Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), 8189)) {
            try (InputStream in = socket.getInputStream()){
                Scanner input = new Scanner(in);
                try (OutputStream out = socket.getOutputStream()) {
                    PrintWriter output = new PrintWriter(out);

                    // TODO gestire input e output
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
