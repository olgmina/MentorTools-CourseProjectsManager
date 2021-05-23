package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public abstract class BaseController {

    // Ошибки
    public final String ERROR                                    = "Ошибка";
    public final String ERROR_WRONG_LOGIN_OR_PASSWORD            = "Неверный логин / пароль \n или нестабильное интернет соединение";
    public final String ERROR_SOMETHING_GOING_WRONG              = "Что-то пошло не так";
    public final String ERROR_MESSAGE_NOT_SENT                   = "Сообщение не было отправлено";
    public final String ERROR_REQUIRED_FIELDS                    = "Заполните все обязательные поля";
    // Информация
    public final String INFORMATION                              = "Информация";
    public final String INFORMATION_NO_UNREAD_MESSAGES           = "У вас нет непрочитанных сообщений \nили нестабильное интернет соединение";
    public final String INFORMATION_CHOOSE_STUDENT               = "Вы не выбрали студента";
    public final String INFORMATION_NO_STUDENTS                  = "В базе данных нет студентов.";
    public final String INFORMATION_NO_USER                      = "Вы не вошли в почтовый аккаунт";
    public final String INFORMATION_SUCCESS_LOGIN                = "Вы успешно вошли в почтовый аккаунт";
    public final String INFORMATION_SUCCESS_SAVE                 = "Данные успешно сохранены";
    public final String INFORMATION_EMPTY_MESSAGE                = "Вы не ввели сообщение";
    public final String INFORMATION_DIALOG_IS_EMPTY              = "Диалог пуст";
    public final String INFORMATION_STUDENT_IS_EMPTY             = "Вы не выбрали студента из таблицы";
    public final String INFORMATION_MESSAGE_SENT                 = "Сообщение успешно отправлено";
    public final String INFORMATION_NO_NEXT_STATUS               = "Это последний статус";
    // Подтверждения
    public final String CONFIRMATION                             = "Подтверждение";
    public final String CONFIRMATION_SURE_TO_QUIT_ACCOUNT        = "Вы уверены, что хотите выйти из почтового аккаунта?";
    public final String CONFIRMATION_LOGGED_SURE_TO_QUIT_ACCOUNT = "Вы уже вошли в почтовый аккаунт. Вы уверены, что хотите выйти?";
    public final String CONFIRMATION_SURE_TO_QUIT                = "Вы уверены, что хотите завершить работу программы?";
    public final String CONFIRMATION_SURE_TO_CHANGE_DIRECTORY    = "Вы уверены, что хотите изменить директорию для загрузки файлов? \nДанная директория изменится для всех существующих студентов";
    public final String CONFIRMATION_DELETE_DIALOG               = "Удалить переписку со студентом?";
    public final String CONFIRMATION_CHANGE_STATUS               = "Вы действительно хотите изменить статус выбранного студента c '#ТЕКУЩИЙ_СТАТУС#' на '#СЛЕДУЮЩИЙ_СТАТУС#'?";
    public final String CONFIRMATION_STUDENT_PERSONAL_CHANGE     = "Вы действительно хотите изменить персональные данные студента?";

    private static Stage stage = new Stage();

    public Optional<ButtonType> newAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }


    public Stage getScene(String resourceFXML, String sceneTitle) {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(resourceFXML));
            stage.setTitle(sceneTitle);
            stage.setScene(new Scene(root));
        } catch (IOException exception) {
            newAlert(Alert.AlertType.ERROR, ERROR, ERROR_SOMETHING_GOING_WRONG);
        }
        return stage;
    }

    public Stage getStage() {
        return stage;
    }
}
