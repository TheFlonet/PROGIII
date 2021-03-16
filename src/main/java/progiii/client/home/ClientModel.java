package progiii.client.home;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import progiii.client.reader.ReaderView;
import progiii.client.writer.WriterView;
import progiii.common.Email;
import progiii.common.EmailAddress;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientModel {
    private final SimpleBooleanProperty isConnected;
    private final WriterView writer;
    private final ReaderView reader;
    private final StringProperty status;
    private final EmailAddress email;
    private List<Email> inbox;
    private List<Email> outbox;
    private Thread pipe;
    private Scanner input;
    private PrintWriter output;

    public ClientModel() {
        this.email = new EmailAddress("mariobifulco@gmail.com");
        this.email.setEmail("mariobifulco@gmail.com"); // TODO cambiare indirizzo per avviare client diversi
        isConnected = new SimpleBooleanProperty();
        isConnected.set(false);
        status = new SimpleStringProperty();
        status.set("");
        writer = new WriterView();
        reader = new ReaderView();

        //TODO rimuovere, inizializzazione di prova
        initMailList();
    }

    public List<Email> getInbox() {
        return inbox;
    }

    public List<Email> getOutbox() {
        return outbox;
    }

    private void initMailList() {
        List<EmailAddress> receivers = new ArrayList<>();
        receivers.add(new EmailAddress("ciccio@email.com"));
        receivers.add(new EmailAddress("pluto@email.com"));
        receivers.add(new EmailAddress("paperino@email.com"));
        receivers.add(new EmailAddress("paperino@email.com"));
        receivers.add(new EmailAddress("paperino@email.com"));
        receivers.add(new EmailAddress("paperino@email.com"));
        receivers.add(new EmailAddress("paperino@email.com"));
        receivers.add(new EmailAddress("paperino@email.com"));
        String text = "What is Lorem Ipsum?\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.\n" +
                "\n" +
                "Why do we use it?\n" +
                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).\n" +
                "\n" +
                "\n" +
                "Where does it come from?\n" +
                "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
                "\n" +
                "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.\n" +
                "\n" +
                "Where can I get some?\n" +
                "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc.";
        inbox = new ArrayList<>() {{
            add(new Email(new EmailAddress("prova@xyz.com"), receivers, "OGGETTO", text));
            add(new Email(new EmailAddress("prova@xyz.com"), receivers, "OGGETTO", "questo è un testo moooolto lungo per una mail di prova"));
            add(new Email(new EmailAddress("prova@xyz.com"), receivers, "OGGETTO", "questo è un testo moooolto lungo per una mail di prova"));
        }};
        outbox = new ArrayList<>() {{
            add(new Email(new EmailAddress("prova@xyz.com"), receivers, "OGGETTO", "questo è un testo moooolto lungo per una mail di prova"));
            add(new Email(new EmailAddress("prova@xyz.com"), receivers, "OGGETTO", "questo è un testo moooolto lungo per una mail di prova"));
            add(new Email(new EmailAddress("prova@xyz.com"), receivers, "OGGETTO", "questo è un testo moooolto lungo per una mail di prova"));
        }};

    }

    public EmailAddress getEmail() {
        return email;
    }

    public WriterView getWriter() {
        return writer;
    }

    public ReaderView getReader() {
        return reader;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public boolean isConnected() {
        return isConnected.get();
    }

    public SimpleBooleanProperty connectedProperty() {
        return isConnected;
    }

    public ObservableValue<String> getStatusProperty() {
        return status;
    }

    public String getStatus() {
        return status.get();
    }

    public void initConnection() {
        pipe = new Thread(() -> {
            try (Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), 8189)) {
                try (InputStream in = socket.getInputStream()) {
                    this.input = new Scanner(in);
                    try (OutputStream out = socket.getOutputStream()) {
                        this.output = new PrintWriter(out);
                        isConnected.set(true);
                    }
                }
            } catch (IOException e) {
                isConnected.set(false);
                e.printStackTrace();
            }
        });
    }
}