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
