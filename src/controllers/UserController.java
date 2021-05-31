package controllers;

import entities.UserEntity;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.mail.MailDao;
import models.UserModel;
import models.mail.YandexMailHandled;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController extends BaseController implements Initializable {

    public TextField username;
    public TextField personal;
    public PasswordField password;
    public Label mailDomain;

    public static MailDao yandexMailHandled = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserModel.getInstance().clearUser();
    }

    public void signIn() {
        if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
            UserModel.getInstance().clearUser();
            UserModel.getInstance().insertUser(new UserEntity(0, username.getText() + mailDomain.getText().trim(), password.getText(), personal.getText()));
            try {
                yandexMailHandled = YandexMailHandled.getInstance();
                getStage().close();
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_SUCCESS_LOGIN);
            } catch (MessagingException e) {
                yandexMailHandled = null;
                UserModel.getInstance().clearUser();
                newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
            }
        } else {
            newAlert(Alert.AlertType.ERROR, ERROR, ERROR_REQUIRED_FIELDS);
        }
    }

}
