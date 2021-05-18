package controllers;

import entities.StudentEntity;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChangeStudentPersonalController extends BaseController implements Initializable {

    public static StudentEntity student = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (student != null) {

        }
    }

    public void saveAction() {
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_STUDENT_PERSONAL_CHANGE);
        if (option.isPresent())
            if (option.get() == ButtonType.OK) {

            }
    }
}
