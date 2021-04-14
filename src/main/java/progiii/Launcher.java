package progiii;

import progiii.client.view.MailClient;
import progiii.server.view.MailServer;

import java.time.temporal.TemporalUnit;

public class Launcher {
    public static void main(String[] args) throws InterruptedException {
        String[] serverArgs = {
                "--datapath=\"C:\\Users\\Flonet\\Desktop\\test\"",
                "--max-handlers=5"
        };
        MailServer.main(serverArgs);

        String[][] clientArgs = {
                {"--start-email=marnaarduino@yahoo.it"},
                {"--start-email=cristianbarraco@gmail.com"},
                {"--start-email=mariobifulco@libero.it"}
        };
        MailClient.main(clientArgs[0]);
        MailClient.main(clientArgs[1]);
        MailClient.main(clientArgs[2]);
    }
}
