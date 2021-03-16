package progiii.common;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class Email implements Serializable {
    private int ID = 0;
    private final EmailAddress sender;
    private final List<EmailAddress> receiver;
    private final String subject;
    private final String text;
    private final ZonedDateTime sentDate;

    @Override
    public String toString() {
        return "Email{" +
                "ID=" + ID +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return ID == email.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public Email(EmailAddress sender, List<EmailAddress> receiver, String subject, String text) {
        ID = ID++;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.text = text;
        sentDate = ZonedDateTime.now();
    }

    public String getReceiversString() {
        List<EmailAddress> receivers = getReceiver();
        StringBuilder out = new StringBuilder();
        for (EmailAddress receiver : receivers)
            out.append(out.toString().equals("") ? receiver.getEmail() : ", " + receiver.getEmail());
        return out.toString();
    }

    public int getID() {
        return ID;
    }

    public EmailAddress getSender() {
        return sender;
    }

    public List<EmailAddress> getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public ZonedDateTime getSentDate() {
        return sentDate;
    }
}
