package controllers;

import entities.UserEntity;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.UserModel;
import models.YandexMailModel;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController extends BaseController implements Initializable {

    public TextField username;
    public TextField personal;
    public PasswordField password;
    public Label mailDomain;

    public static YandexMailModel yandexMailModel = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserModel.getInstance().clearUser();
    }

    public void signIn() {
        UserModel.getInstance().clearUser();
        UserModel.getInstance().insertUser(new UserEntity(0, username.getText() + mailDomain.getText().trim(), password.getText(), personal.getText()));
        try {
            yandexMailModel = YandexMailModel.getInstance();
            getStage().close();
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_SUCCESS_LOGIN);
        } catch (MessagingException e) {
            yandexMailModel = null;
            UserModel.getInstance().clearUser();
            newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
        }
    }

}
