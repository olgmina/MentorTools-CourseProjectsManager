package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import entities.UserEntity;
import models.UserModel;
import models.YandexMailModel;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController extends BaseController implements Initializable {

    public TextField username;
    public TextField personal;
    public TextField password;
    public Label mailDomain;

    public static YandexMailModel yandexMailModel = null;
    public static UserModel userModel        = UserModel.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userModel.clearUser();
    }

    public void signIn() {
        userModel.clearUser();
        userModel.insertUser(new UserEntity(0, username.getText() + mailDomain.getText().trim(), password.getText(), personal.getText()));
        try {
            yandexMailModel = YandexMailModel.getInstance();
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_SUCCESS_LOGIN);
        } catch (MessagingException e) {
            yandexMailModel = null;
            userModel.clearUser();
            newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
        }
    }

}
