package progiii.client.reader;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ReaderController implements Initializable {
    @FXML
    public TextArea text;
    @FXML
    public Label sender;
    @FXML
    public Label cc;
    @FXML
    public Label subject;
    @FXML
    public Label receivers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ReaderModel model = ReaderModel.getInstance();
        sender.setText(model.getEmail().getSender().getEmail());
        cc.setText("TI SEI SCORDATO I CC");
        subject.setText(model.getEmail().getSubject());
        text.setText(model.getEmail().getText());
        receivers.setText(model.getEmail().getReceiversString());
    }
}
