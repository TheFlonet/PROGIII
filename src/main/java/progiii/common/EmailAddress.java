package progiii.common;

import javafx.beans.property.SimpleStringProperty;

import java.util.regex.Pattern;

public class EmailAddress {
    private SimpleStringProperty email = new SimpleStringProperty();

    public void setEmail(String email) {
        if (isValid(email))
            this.email.set(email);
        else
            throw new InvalidEmailException("E-mail not valid. A valid email is like: example@email.com");
    }

    private boolean isValid(String email) {
        String emailRegEx = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegEx);
        return pattern.matcher(email).matches();
    }

    public SimpleStringProperty getEmailProperty() {
        return email;
    }

    public String getEmail() {
        return email.get();
    }
}
