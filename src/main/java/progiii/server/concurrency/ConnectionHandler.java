package progiii.server.concurrency;

import progiii.common.data.Email;
import progiii.common.network.request.*;
import progiii.common.network.response.*;
import progiii.common.util.ClosableRes;
import progiii.common.util.StringUtils;
import progiii.common.util.ValidatorCollector;
import progiii.server.model.Model;
import progiii.server.util.log.Event;
import progiii.server.view.MailServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConnectionHandler implements Runnable {
    private Socket connection;

    public ConnectionHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        Event event = new Event();
        event.addNormalMsg("Received connection from " + connection.getInetAddress().toString());
        try (ClosableRes toClose = event::conclude) {
            try (ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream())) {
                Response response = new ErrorRes("Server side error");
                try {
                    Request request = (Request) in.readObject();
                    event.setRequest(request);
                    System.out.println(request);

                    switch (request.getType()) {
                        case PULL -> {
                            PullReq pull = (PullReq) request;
                            Set<Email> emails = MailServer.getDataManager().getMissingUserEmails(pull.getEmail(), pull.getLocalIdSet());
                            response = new NewEmailRes(emails);
                            event.addNormalMsg(String.format("%d new email(s)", emails.size()));
                            System.out.println(response);
                            out.writeObject(response);
                        }
                        case CHECK -> {
                            CheckEmailReq check = (CheckEmailReq) request;
                            Map<String, Boolean> existence = null;
                            existence = check.getToCheck().stream().collect(Collectors.toMap(email -> email,
                                    email -> MailServer.getDataManager().userExists(email), (a, b) -> b, HashMap::new));
                            response = new EmailExistenceRes(existence);
                            event.addNormalMsg(String.format("%d email(s) do not exists",
                                    existence.keySet().stream().map(existence::get).filter(v -> !v).count()));
                            out.writeObject(response);
                        }
                        case AUTH -> {
                            AuthReq auth = (AuthReq) request;
                            try {
                                if (!MailServer.getDataManager().userExists(auth.getEmail())) {
                                    event.addNormalMsg("Unregistered email");
                                    MailServer.getDataManager().registerEmail(auth.getEmail());
                                    response = new NewEmailRes("Email registered successfully", new HashSet<>());
                                    event.addNormalMsg("Email registered successfully");
                                } else {
                                    Set<Email> emails = MailServer.getDataManager()
                                            .getMissingUserEmails(auth.getEmail(), new HashSet<>());
                                    response = new NewEmailRes(emails);
                                    event.addNormalMsg("Email registered successfully. Fetching email(s)");
                                }
                            } catch (IOException e) {
                                response = new ErrorRes(e.getMessage());
                                event.addError(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                        case SEND -> {
                            SendReq sendReq = (SendReq) request;
                            SendRes sendRes = null;
                            ValidatorCollector recipients = StringUtils.cleanEmailList(sendReq.getEmailObject().getTo());
                            try {
                                StringUtils.cleanEmail(sendReq.getEmailObject().getFrom());
                            } catch (IllegalArgumentException e) {
                                sendRes = new SendRes(false, e.getMessage());
                                event.addError(e.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                                event.addException(e);
                            }

                            if (sendRes == null) {
                                if (recipients.areAllEmailsGood()) {
                                    int reservedId = MailServer.getDataManager().reserveId();
                                    Email toSend = sendReq.getEmailObject();
                                    toSend.setId(reservedId);
                                    recipients.getEmails(true).forEach(recipient -> MailServer.getDataManager().deliver(recipient, toSend));
                                    response = new SendRes(true, "OK");
                                    event.addNormalMsg("Email delivered");
                                } else {
                                    response = new SendRes(false, "Invalid email(s): " + recipients.emailsString(false));
                                    event.addNormalMsg("Some email(s) are not valid: " + recipients.emailsString(false));
                                }
                            }
                        }
                        case DELETE -> {
                            DeleteReq delete = (DeleteReq) request;
                            String email = delete.getEmail();
                            if (MailServer.getDataManager().userExists(email)) {
                                event.addNormalMsg(String.format("%s exist", email));
                                boolean success = true;
                                StringBuilder missEmails = new StringBuilder("[");
                                for (int id : delete.getIdSetToDelete()) {
                                    boolean opRes = MailServer.getDataManager().delete(email, id);
                                    if (!opRes) {
                                        success = false;
                                        missEmails.append(id).append(" ");
                                    }
                                }
                                missEmails.append(']');
                                response = new DeleteRes(success, missEmails.toString());
                                event.addWarning(String.format("%d email(s) not found", missEmails.length() - 2));
                            } else {
                                response = new ErrorRes(String.format("%s do not exist", email));
                                event.addError(String.format("%s do not exist", email));
                            }
                        }
                        default -> {
                            String msg = String.format("Request type %s not supported", request.getType());
                            System.err.println(msg);
                            response = new ErrorRes(msg);
                            event.addError(msg);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    event.addException(e);
                    response = new ErrorRes(e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    event.addException(e);
                } finally {
                    out.writeObject(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
                event.addException(e);
            } finally {
                connection.close();
                event.addNormalMsg("Connection closed");
            }
        } catch (IOException e) {
            e.printStackTrace();
            event.addException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Model.getInstance().addEvent(event);
    }
}
