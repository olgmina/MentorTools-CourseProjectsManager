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

    protected Optional<ButtonType> newAlert(Alert.AlertType alertType, String title, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }

    protected void showScene(String resourceFXML, String sceneTitle) {
        try {
            Stage primaryStage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourceFXML)));
            primaryStage.setTitle(sceneTitle);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException exception) { newAlert(Alert.AlertType.ERROR, "Ошибка", "Что-то пошло не так"); }
    }

}
