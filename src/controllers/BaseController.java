package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseController {

    public final String ERROR                                 = "Ошибка";
    public final String INFORMATION                           = "Информация";
    public final String CONFIRMATION                          = "Подтверждение";
    public final String ERROR_WRONG_LOGIN_OR_PASSWORD         = "Неверный логин / пароль \n или нестабильное интернет соединение";
    public final String ERROR_NO_UNREAD_MESSAGES              = "У вас нет непрочитанных сообщений \nили нестабильное интернет соединение";
    public final String INFORMATION_NO_STUDENTS               = "В базе данных нет студентов.";
    public final String INFORMATION_NO_USER                   = "Вы не вошли в почтовый аккаунт";
    public final String INFORMATION_SUCCESS_LOGIN             = "Вы успешно вошли в почтовый аккаунт";
    public final String INFORMATION_SUCCESS_SAVE              = "Данные успешно сохранены";
    public final String CONFIRMATION_SURE_TO_QUIT_ACCOUNT     = "Вы уверены, что хотите выйти из почтового аккаунта?";
    public final String CONFIRMATION_SURE_TO_QUIT             = "Вы уверены, что хотите завершить работу программы?";
    public final String CONFIRMATION_SURE_TO_CHANGE_DIRECTORY = "Вы уверены, что хотите изменить директорию для загрузки файлов? \nДанная директория изменится для всех существующих студентов";

    public Optional<ButtonType> newAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }

    public Stage getScene(String resourceFXML, String sceneTitle) {
        try {
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourceFXML)));
            primaryStage.setTitle(sceneTitle);
            primaryStage.setScene(new Scene(root));
            return primaryStage;
        } catch (IOException exception) {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Что-то пошло не так");
            return null;
        }
    }
}
