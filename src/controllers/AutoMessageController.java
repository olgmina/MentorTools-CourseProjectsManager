package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import entities.AutoMessageEntity;
import models.AutoMessageModel;

import java.net.URL;
import java.util.*;


public class AutoMessageController extends BaseController implements Initializable {

    public TextArea THEME;
    public TextArea TEXT_YOU_HAVE_NOT_DONE_STAGE;
    public TextArea TEXT_WRONG_FORMAT;
    public TextArea TEXT_STAGE_IS_ALREADY_COMPLETED;
    public TextArea TEXT_ANSWERED_ON_DATE;
    public TextArea TEXT_NOT_NEXT_STAGE;
    public TextArea TEXT_COURSE_PROJECT_COMPLETED;
    public TextArea TEXT_STATUS_CHANGED;
    public Label message;
    public Map<String, TextArea> textAreaStringMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textAreaStringMap.put(AutoMessageModel.THEME, THEME);
        textAreaStringMap.put(AutoMessageModel.TEXT_YOU_HAVE_NOT_DONE_STAGE, TEXT_YOU_HAVE_NOT_DONE_STAGE);
        textAreaStringMap.put(AutoMessageModel.TEXT_WRONG_FORMAT, TEXT_WRONG_FORMAT);
        textAreaStringMap.put(AutoMessageModel.TEXT_STAGE_IS_ALREADY_COMPLETED, TEXT_STAGE_IS_ALREADY_COMPLETED);
        textAreaStringMap.put(AutoMessageModel.TEXT_NOT_NEXT_STAGE, TEXT_NOT_NEXT_STAGE);
        textAreaStringMap.put(AutoMessageModel.TEXT_COURSE_PROJECT_COMPLETED, TEXT_COURSE_PROJECT_COMPLETED);
        textAreaStringMap.put(AutoMessageModel.TEXT_STATUS_CHANGED, TEXT_STATUS_CHANGED);
        textAreaStringMap.forEach(
                (name, textArea) -> textArea.setText(AutoMessageModel.getInstance().getAutoMessage(name).getText())
        );
    }

    public void saveAction() {
        textAreaStringMap.forEach(
                (name, textArea) -> {
                    AutoMessageEntity autoMessage = AutoMessageModel.getInstance().getAutoMessage(name);
                    autoMessage.setText(textArea.getText());
                    AutoMessageModel.getInstance().saveAutoMessage(autoMessage);
                }
        );
        newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_SUCCESS_SAVE);
    }
}
