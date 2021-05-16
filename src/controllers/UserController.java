package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import entities.UserEntity;
import models.UserModel;
import models.YandexMailModel;

import javax.mail.MessagingException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController extends BaseController implements Initializable {

    public TextField username;
    public TextField personal;
    public TextField password;
    public Label mailDomain;
    public static YandexMailModel yandexMail = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserModel.getInstance().clearUser();
    }

    public void signIn() {
        UserEntity user = new UserEntity(0, username.getText() + mailDomain.getText().trim(), password.getText(), personal.getText());
        UserModel.getInstance().clearUser();
        UserModel.getInstance().insertUser(user);
        try {
            yandexMail = YandexMailModel.getInstance();
            newAlert(Alert.AlertType.INFORMATION, "Информация", "Вы успешно вошли в почтовый аккаунт");
        } catch (MessagingException e) {
            UserModel.getInstance().clearUser();
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Неверный логин / пароль \nили нестабильное интернет соединение");
        }
    }

}
