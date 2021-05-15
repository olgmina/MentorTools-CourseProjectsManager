package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import entities.UserEntity;
import javafx.stage.Stage;
import models.UserModel;
import models.YandexMail;

import javax.mail.MessagingException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController extends BaseController implements Initializable {

    public TextField username;
    public TextField personal;
    public TextField password;
    public Label mailDomain;
    public static YandexMail yandexMail = null;
    UserModel userModel = UserModel.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserEntity user = userModel.getUser();
        if (user == null)
            return;
        username.setText(user.getUsername().split("@")[0]);
        personal.setText(user.getPersonal());
        username.setText(user.getPassword());
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Вы уже вошли в аккаут электронной почты.\n" + "Вы уверены, что хотите сменить пользователя?");
        if (option.isPresent())
            if (option.get() != ButtonType.OK) {
                System.exit(0);
            }
    }

    public void signIn() {
        try {
            UserEntity user = new UserEntity(0, username.getText() + mailDomain.getText().trim(), password.getText(), personal.getText());
            yandexMail = new YandexMail(user.getUsername(), user.getPassword(), user.getPersonal());
            userModel.insertUser(user);
            newAlert(Alert.AlertType.INFORMATION, "Информация", "Вы успешно вошли в почтовый аккаунт");
        } catch (MessagingException e) {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Неверный логин / пароль\n" +
                    "или нестабильное интернет соединение");
        }
    }

}
