package controllers;

import entities.StatusEntity;
import entities.StudentEntity;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.StatusModel;
import models.StudentModel;
import models.UserModel;
import models.YandexMailModel;
import models.messageReader.EmailMessageReader;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController extends BaseController implements Initializable {

    public TextArea textArea;
    public TableView<StudentEntity> studentsTable;
    public TableColumn<StudentEntity, Integer> ID;
    public TableColumn<StudentEntity, String> PERSONAL;
    public TableColumn<StudentEntity, String> EMAIL;
    public TableColumn<StudentEntity, String> FOLDER;
    public TableColumn<StudentEntity, String> STAGE;
    public TableColumn<StudentEntity, String> STATUS;
    public TableColumn<StudentEntity, Integer> FILES;
    public TextField toEmail;
    public TextField themeStage;
    public TextArea message;

    private ObservableList<StudentEntity> students = null;
    private File studentsFolder = new java.io.File(System.getenv("APPDATA") + File.separator + "CourseProjectsManager" + File.separator + "students");

    public MainController() {
        if (!UserModel.getInstance().isLogged()) {
            changeUser();
        } else {
            try {
                UserController.yandexMailModel = YandexMailModel.getInstance();
            } catch (MessagingException e) {
                UserController.yandexMailModel = null;
                newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
            }
        }
    }

    //TODO: change user personal
    public void changeUserPersonal() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!studentsFolder.exists()) {
            studentsFolder.mkdirs();
        }

        studentsTable.setOnMouseClicked(e -> {
            try {
                StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
                toEmail.setText(student.getEmailAddress());
                themeStage.setText(String.valueOf(student.getStage().getName()));
            } catch (NullPointerException ignore) {
            }
        });

        textArea.setEditable(false);

        students = StudentModel.getInstance().getStudents();
        ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        PERSONAL.setCellValueFactory(new PropertyValueFactory<>("personal"));
        EMAIL.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        FOLDER.setCellValueFactory(new PropertyValueFactory<>("folderPath"));
        STAGE.setCellValueFactory(new PropertyValueFactory<>("stage"));
        STATUS.setCellValueFactory(new PropertyValueFactory<>("status"));
        FILES.setCellValueFactory(new PropertyValueFactory<>("fileCount"));
        studentsTable.setItems(students);
    }

    private void chooseStageForDialog(StudentEntity student) {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            Stage stage = getScene("views/ChooseStageView.fxml", "Выбор этапа для просмотра сообщений с выбранным студентом");
            if (stage != null) stage.showAndWait();
            StringBuilder dialog = new StringBuilder();
            ArrayList<Message> dialogMessages = UserController.yandexMailModel.getInboxDialogMessages(student.getEmailAddress(), ChooseStageController.stage.getName());

            if (!dialogMessages.isEmpty()) {
                for (Message dialogMessage : dialogMessages)
                    dialog.append(EmailMessageReader.getShortMessage(dialogMessage));
                textArea.setText(dialog.toString());
            } else
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_DIALOG_IS_EMPTY);
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    private void deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null)
                for (File file : files) {
                    if (file.isDirectory())
                        deleteDirectory(file);
                    else
                        file.delete();
                }
        }
        path.delete();
    }

    public void close() {
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_SURE_TO_QUIT);
        if (option.isPresent())
            if (option.get() == ButtonType.OK)
                System.exit(200);
    }

    public void inboxCount() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, UserController.yandexMailModel.inboxMessagesCount() + " входящих сообщений. \n(" + UserController.yandexMailModel.notSeenMessagesCount() + " непрочитанных)");
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void inboxNotSeenCount() {
        if (UserController.yandexMailModel != null || UserModel.getInstance().isLogged()) {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, UserController.yandexMailModel.notSeenMessagesCount() + " непрочитанных сообщений");
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void showNotSeenMessages() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            if (UserController.yandexMailModel.notSeenMessagesCount() > 0) {
                ArrayList<Message> messages = UserController.yandexMailModel.getNotSeenInboxMessages();
                textArea.setText("");
                for (Message message : messages) {
                    textArea.setText(textArea.getText() + EmailMessageReader.getAllMessage(message));
                    textArea.setText(textArea.getText() + "\n");
                    try {
                        message.setFlag(Flags.Flag.SEEN, false);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            } else
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_UNREAD_MESSAGES);
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void loadNotSeenMessages() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            if (UserController.yandexMailModel.notSeenMessagesCount() > 0) {
                UserController.yandexMailModel.loadNotSeenInboxMessage(studentsFolder);
                showAllStudents();
            } else
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_UNREAD_MESSAGES);
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void yandexQuit() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_SURE_TO_QUIT_ACCOUNT);
            if (option.isPresent())
                if (option.get() == ButtonType.OK)
                    changeUser();
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void sendEmail() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            if (message.getText().isEmpty()) {
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_EMPTY_MESSAGE);
            } else if (toEmail.getText().isEmpty() || themeStage.getText().isEmpty()) {
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_STUDENT_IS_EMPTY);
            } else {
                try {
                    UserController.yandexMailModel.sendMessage(toEmail.getText(), themeStage.getText(), message.getText());
                } catch (MessagingException e) {
                    newAlert(Alert.AlertType.ERROR, ERROR, ERROR_MESSAGE_NOT_SENT);
                }
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_MESSAGE_SENT);
            }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void showAllStudents() {
        students = StudentModel.getInstance().getStudents();
        if (students == null) {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_STUDENTS);
        } else
            studentsTable.setItems(students);
    }

    public void deleteStudent() {
        StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
        if (student != null) {
            File file = new File(student.getFolderPath());
            boolean fileExists = file.exists();
            if (fileExists) {
                Optional<ButtonType> option1 = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, "Удалить папку студента?");
                if (option1.isPresent())
                    if (option1.get() == ButtonType.OK)
                        deleteDirectory(file);
            }

            if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
                Optional<ButtonType> option2 = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_DELETE_DIALOG);
                if (option2.isPresent())
                    if (option2.get() == ButtonType.OK) {
                        UserController.yandexMailModel.deleteInboxDialogMessages(student.getEmailAddress(), null);
                    }
            } else {
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
                changeUser();
            }

            if (!fileExists && UserController.yandexMailModel.getInboxDialogMessages(student.getEmailAddress(), null).isEmpty()) {
                StudentModel.getInstance().deleteStudent(student.getId());
                showAllStudents();
            }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_CHOOSE_STUDENT);
        }
    }

    public void changeDir() {
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_SURE_TO_CHANGE_DIRECTORY);
        if (option.isPresent())
            if (option.get() == ButtonType.OK) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(studentsFolder);
                fileChooser.setDialogTitle("Выберите папку");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                if (fileChooser.showOpenDialog(new JButton()) == JFileChooser.APPROVE_OPTION) {
                    studentsFolder = fileChooser.getSelectedFile();
                    if (studentsFolder != null) {
                        StudentModel.getInstance().changeDir(studentsFolder);
                        showAllStudents();
                    }
                }
            }

    }

    public void openDir() {
        StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
        if (student != null) {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(new File(student.getFolderPath()));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_CHOOSE_STUDENT);
        }
    }

    public void changeStatus() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
            if (student != null) {
                StatusEntity currentStatus = student.getStatus();
                StatusEntity nextStatus = StatusModel.getInstance().getNextStatus(student.getStatus());
                if (nextStatus != null) {
                    Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_CHANGE_STATUS.replaceAll("#ТЕКУЩИЙ_СТАТУС#", currentStatus.getName()).replaceAll("#СЛЕДУЮЩИЙ_СТАТУС#", nextStatus.getName()));
                    if (option.isPresent()) {
                        if (option.get() == ButtonType.OK) {
                            if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
                                UserController.yandexMailModel.changeStatus(student, nextStatus);
                                student.setStatus(nextStatus);
                                StudentModel.getInstance().updateStudent(student);
                                showAllStudents();
                            } else {
                                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
                                changeUser();
                            }
                        }
                    }
                } else {
                    newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_NEXT_STATUS);
                }
            } else {
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_CHOOSE_STUDENT);
            }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void changePersonal() {
        StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
        if (student != null) {
            ChangeStudentPersonalController.student = student;
            Stage stage = getScene("views/ChangeStudentPersonalView.fxml", "Изменение персональных данных студента");
            if (stage != null) stage.showAndWait();
            showAllStudents();
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_CHOOSE_STUDENT);
        }
    }

    public void getDialogWithStudent() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
            if (student != null) {
                chooseStageForDialog(student);
            } else newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_CHOOSE_STUDENT);
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }

    public void changeAutoMessages() {
        Stage stage = getScene("views/AutoMessageView.fxml", "Авто-сообщения");
        if (stage != null) stage.showAndWait();
    }

    public void changeUser() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_LOGGED_SURE_TO_QUIT_ACCOUNT);
            if (option.isPresent()) {
                if (option.get() != ButtonType.OK)
                    return;
            } else {
                return;
            }
        }
        Stage stage = getScene("views/UserView.fxml", "Войдите в аккаунт");
        if (stage != null) stage.showAndWait();
    }

    public void markMessagesAsSeen() {
        if (UserController.yandexMailModel != null && UserModel.getInstance().isLogged()) {
            if (UserController.yandexMailModel.notSeenMessagesCount() > 0) {
                ArrayList<Message> messages = UserController.yandexMailModel.getNotSeenInboxMessages();
                for (Message message : messages) {
                    try {
                        message.setFlag(Flags.Flag.SEEN, true);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            } else
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_UNREAD_MESSAGES);
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
            changeUser();
        }
    }
}