package controllers;

import entities.StudentEntity;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChangeStudentPersonalController extends BaseController implements Initializable {

    public TextField studentPersonal;

    public static StudentEntity student = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (student != null) {
            studentPersonal.setText(student.getPersonal());
        } else {
            newAlert(Alert.AlertType.ERROR, ERROR, ERROR_SOMETHING_GOING_WRONG);
        }
    }

    public void saveAction() {
        if (student != null) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_STUDENT_PERSONAL_CHANGE);
            if (option.isPresent())
                if (option.get() == ButtonType.OK) {
                    if (!studentPersonal.getText().isEmpty()) {
                        student.setPersonal(studentPersonal.getText());
                    } else {
                        newAlert(Alert.AlertType.ERROR, ERROR, ERROR_REQUIRED_FIELDS);
                    }
                }
        } else {
            newAlert(Alert.AlertType.ERROR, ERROR, ERROR_SOMETHING_GOING_WRONG);
        }

    }
}
