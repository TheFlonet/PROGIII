package progiii.common.data;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.beans.property.*;

public class Email implements Serializable, Comparable<Email> {
    public transient static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("'Sent:' EEEE d MMMM yyyy 'at' H:m");
    private transient SimpleIntegerProperty id;
    private transient SimpleStringProperty from;
    private transient SimpleStringProperty to;
    private transient SimpleStringProperty subject;
    private transient SimpleStringProperty text;
    // todo: aggiungere una label per la data nella grafica dell'email ricevuta.
    private transient SimpleObjectProperty<Date> date;

    private Email() {
    }

    public Email(int id, String from, String to, String subject, String text, Date date) {
        this.id = new SimpleIntegerProperty(id);
        this.from = new SimpleStringProperty(from);
        this.to = new SimpleStringProperty(to);
        this.subject = new SimpleStringProperty(subject);
        this.text = new SimpleStringProperty(text);
        this.date = new SimpleObjectProperty<>(date);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getFrom() {
        return from.get();
    }

    public void setFrom(String from) {
        this.from.set(from);
    }

    public SimpleStringProperty fromProperty() {
        return from;
    }

    public String getTo() {
        return to.get();
    }

    public void setTo(String to) {
        this.to.set(to);
    }

    public SimpleStringProperty toProperty() {
        return to;
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public SimpleStringProperty textProperty() {
        return text;
    }

    public Date getDate() {
        return date.get();
    }

    public void setDate(Date date) {
        this.date.set(date);
    }

    public SimpleObjectProperty<Date> dateProperty() {
        return date;
    }

    // TODO capire se va bene questo toString o copiare l'altro
    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", subject=" + subject +
                ", text=" + text +
                ", date=" + date +
                '}';
    }

    public String formattedDate() {
        return DATE_FORMAT.format(date.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return id.get() == email.id.get();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.get());
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(id.intValue());
        out.writeUTF(from.getValue());
        out.writeUTF(to.getValue());
        out.writeObject(date.getValue());
        out.writeUTF(subject.getValue());
        out.writeUTF(text.getValue());
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.id = new SimpleIntegerProperty(in.readInt());
        this.from = new SimpleStringProperty(in.readUTF());
        this.to = new SimpleStringProperty(in.readUTF());
        this.date = new SimpleObjectProperty<>((Date) in.readObject());
        this.subject = new SimpleStringProperty(in.readUTF());
        this.text = new SimpleStringProperty(in.readUTF());
    }

    @Override
    public int compareTo(Email o) {
        return Integer.compare(o.id.get(), id.get());
    }

    public static class Adapter extends TypeAdapter<Email> {

        @Override
        public void write(JsonWriter jsonWriter, Email email) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("id");
            jsonWriter.value(email.getId());
            jsonWriter.name("from");
            jsonWriter.value(email.getFrom());
            jsonWriter.name("to");
            jsonWriter.value(email.getTo());
            jsonWriter.name("subject");
            jsonWriter.value(email.getSubject());
            jsonWriter.name("text");
            jsonWriter.value(email.getText());
            jsonWriter.name("date");
            jsonWriter.value(email.getDate().getTime()); //date as long
            jsonWriter.endObject();
        }

        @Override
        public Email read(JsonReader jsonReader) throws IOException {
            Email email = new Email();

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "id" -> email.id = new SimpleIntegerProperty(jsonReader.nextInt());
                    case "from" -> email.from = new SimpleStringProperty(jsonReader.nextString());
                    case "to" -> email.to = new SimpleStringProperty(jsonReader.nextString());
                    case "subject" -> email.subject = new SimpleStringProperty(jsonReader.nextString());
                    case "text" -> email.text = new SimpleStringProperty(jsonReader.nextString());
                    case "date" -> email.date = new SimpleObjectProperty<>(new Date(jsonReader.nextLong()));
                    default -> jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

            return email;
        }
    }
}
