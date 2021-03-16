package progiii.client.reader;

import progiii.common.Email;

public class ReaderModel {
    private static ReaderModel instance;
    private Email email;

    private ReaderModel() {}

    public static ReaderModel getInstance() {
        if (instance == null)
            instance = new ReaderModel();
        return instance;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public Email getEmail() {
        return email;
    }
}
