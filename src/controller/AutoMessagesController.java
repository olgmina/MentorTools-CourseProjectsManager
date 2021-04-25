package controller;

import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import model.DbHandler;

import java.net.URL;
import java.util.ResourceBundle;


public class AutoMessagesController implements Initializable {

    public TextArea THEME_WRONG_STAGE;
    public TextArea THEME_WRONG_FORMAT;
    public TextArea TEXT_YOU_HAVE_NOT_DONE_STAGE;
    public TextArea TEXT_WRONG_FORMAT;
    public TextArea TEXT_STAGE_IS_ALREADY_COMPLETED;
    public TextArea TEXT_ANSWERED_ON_DATE;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DbHandler dbHandler = DbHandler.getInstance();
    }

    public void saveAction() {
    }
}
