package progiii.client.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import progiii.common.data.Email;

public abstract class TabController {
    @FXML
    protected Tab tab;
    @FXML
    protected Node subjectNode;
    @FXML
    protected Node fromNode;
    @FXML
    protected Node toNode;
    @FXML
    protected Node textNode;
    protected Email email;

    public Tab getTab() {
        return tab;
    }

    public void initialize(Email email) {
        if (this.email == null)
            this.email = email;
        else throw new RuntimeException("Tab controller has already been initialized");
    }

    public Email getEmail() {
        return email;
    }

    public abstract boolean isDraft();
}
