package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import entities.AutoMessageEntity;
import models.AutoMessageModel;

import java.net.URL;
import java.util.*;


public class AutoMessageController extends BaseController implements Initializable {

    public TextArea THEME_WRONG_STAGE;
    public TextArea THEME_WRONG_FORMAT;
    public TextArea TEXT_YOU_HAVE_NOT_DONE_STAGE;
    public TextArea TEXT_WRONG_FORMAT;
    public TextArea TEXT_STAGE_IS_ALREADY_COMPLETED;
    public TextArea TEXT_ANSWERED_ON_DATE;
    public AutoMessageModel autoMessageModel = AutoMessageModel.getInstance();
    public Map<String, TextArea> textAreaStringMap = new HashMap<>();
    public Label message;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textAreaStringMap.put(AutoMessageModel.THEME_WRONG_STAGE, THEME_WRONG_STAGE);
        textAreaStringMap.put(AutoMessageModel.THEME_WRONG_FORMAT, THEME_WRONG_FORMAT);
        textAreaStringMap.put(AutoMessageModel.TEXT_YOU_HAVE_NOT_DONE_STAGE, TEXT_YOU_HAVE_NOT_DONE_STAGE);
        textAreaStringMap.put(AutoMessageModel.TEXT_WRONG_FORMAT, TEXT_WRONG_FORMAT);
        textAreaStringMap.put(AutoMessageModel.TEXT_STAGE_IS_ALREADY_COMPLETED, TEXT_STAGE_IS_ALREADY_COMPLETED);
        textAreaStringMap.put(AutoMessageModel.TEXT_ANSWERED_ON_DATE, TEXT_ANSWERED_ON_DATE);
        textAreaStringMap.forEach(
                (name, textArea) -> textArea.setText(autoMessageModel.getAutoMessage(name).getText())
        );
    }

    public void saveAction() {
        textAreaStringMap.forEach(
                (name, textArea) -> {
                    AutoMessageEntity autoMessage = autoMessageModel.getAutoMessage(name);
                    autoMessage.setText(textArea.getText());
                    autoMessageModel.saveAutoMessage(autoMessage);
                }
        );
        message.setText("Данные успешно сохранены");
        message.setStyle("-fx-text-fill: green;");
    }
}
