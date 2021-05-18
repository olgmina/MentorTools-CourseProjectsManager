package controllers;

import entities.StageEntity;
import entities.StudentEntity;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.persistence.criteria.CriteriaBuilder;
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
    private File rootFolder;

    private StudentModel studentModel = StudentModel.getInstance();
    private UserModel userModel = UserModel.getInstance();
    public static StageEntity selectedStageForDialog = null;

    public MainController() {
        if (!userModel.isLogged()) {
            changeUser();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootFolder = new java.io.File(
                System.getProperty("user.dir") + File.separator +
                        "src" + File.separator +
                        "resources" + File.separator +
                        "students"
        );

        studentsTable.setOnMouseClicked(e -> {
            try {
                StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
                toEmail.setText(student.getEmailAddress());
                themeStage.setText(String.valueOf(student.getStage().getName()));
            } catch (NullPointerException ignore) {
            }
        });

        textArea.setEditable(false);

        students = studentModel.getStudents();
        ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        PERSONAL.setCellValueFactory(new PropertyValueFactory<>("personal"));
        EMAIL.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        FOLDER.setCellValueFactory(new PropertyValueFactory<>("folderPath"));
        STAGE.setCellValueFactory(new PropertyValueFactory<>("stage"));
        STATUS.setCellValueFactory(new PropertyValueFactory<>("status"));
        FILES.setCellValueFactory(new PropertyValueFactory<>("fileCount"));
        studentsTable.setItems(students);
    }

    //TODO: изменить пенсональные данные
    private void newPersonalWindow(StudentEntity student) {

    }

    private void chooseStageForDialog(StudentEntity student) {
        /*GridPane grid = newGridScene(600, 150,3, 4, 30);

        Scene chooseStage = new Scene(grid, grid.getPrefWidth(), grid.getPrefHeight());

        Text sceneTitle = new Text("Выбор темы сообщений");
        sceneTitle.setTextAlignment(TextAlignment.CENTER);
        sceneTitle.setWrappingWidth(200);
        sceneTitle.setFont(new Font(18));

        grid.add(sceneTitle, 1, 0);

        Label userName = new Label("Этапы:");
        grid.add(userName, 0, 1);

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll("Все этапы", "Задание", "Разработка программы", "Оформление пояснительной записки");
        choiceBox.setValue("Все этапы");
        grid.add(choiceBox, 1, 1);

        Button btn = new Button("Просмотреть");
        grid.add(btn, 2, 2);

        Stage stageWindow = new Stage();
        stageWindow.setTitle("Выбор темы");
        stageWindow.setScene(chooseStage);
        stageWindow.setResizable(false);

        btn.setOnAction(e -> {
            StringBuilder dialog = new StringBuilder();
            ArrayList<Message> dialogMessages = new ArrayList<>();
            switch (choiceBox.getValue()) {
                case "Все этапы":
                    dialogMessages = yandex.getInboxDialogMessages(student.getEmailAddress(), yandex.getFromEmail(), "Задание", "Разработка программы", "Оформление пояснительной записки");
                    break;
                case "Задание":
                    dialogMessages = yandex.getInboxDialogMessages(student.getEmailAddress(), yandex.getFromEmail(), "Задание");
                    break;
                case "Разработка программы":
                    dialogMessages = yandex.getInboxDialogMessages(student.getEmailAddress(), yandex.getFromEmail(), "Разработка программы");
                    break;
                case "Оформление пояснительной записки":
                    dialogMessages = yandex.getInboxDialogMessages(student.getEmailAddress(), yandex.getFromEmail(), "Оформление пояснительной записки");
                    break;
            }
            if (!dialogMessages.isEmpty()) {
                for (Message dialogMessage : dialogMessages)
                    dialog.append(EmailMessageReader.getShortMessage(dialogMessage));
                textArea.setText(dialog.toString());
            } else
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, "Диалог пуст");
            stageWindow.close();
        });
        stageWindow.showAndWait();*/
        try {
            selectedStageForDialog = null;
            Stage stage = getScene("../views/ChooseStage.fxml", "Выбор этапа для просмотра сообщений с выбранным студентом");
            if (stage != null) stage.showAndWait();
            StringBuilder dialog = new StringBuilder();
            ArrayList<Message> dialogMessages = YandexMailModel.getInstance().getInboxDialogMessages(student.getEmailAddress(), "Задание");

            if (!dialogMessages.isEmpty()) {
                for (Message dialogMessage : dialogMessages)
                    dialog.append(EmailMessageReader.getShortMessage(dialogMessage));
                textArea.setText(dialog.toString());
            } else
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_DIALOG_IS_EMPTY);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDirectory(File path) {
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
        return (path.delete());
    } // ✓

    public void close() {
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_SURE_TO_QUIT);
        if (option.isPresent())
            if (option.get() == ButtonType.OK)
                System.exit(200);
    } // ✓

    public void inboxCount() {
        if (userModel.isLogged()) {
            try {
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, YandexMailModel.getInstance().inboxMessagesCount() + " входящих сообщений. \n(" + YandexMailModel.getInstance().notSeenMessagesCount() + " непрочитанных)");
            } catch (MessagingException e) {
                newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
            }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
        }
    } // ✓

    public void inboxNotSeenCount() {
        if (userModel.isLogged()) {
            try {
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, YandexMailModel.getInstance().notSeenMessagesCount() + " непрочитанных сообщений");
            } catch (MessagingException e) {
                newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
            }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
        }
    } // ✓

    public void showNotSeenMessages() {
        if (userModel.isLogged()) {
            try {
                if (YandexMailModel.getInstance().notSeenMessagesCount() > 0) {
                    ArrayList<Message> messages = YandexMailModel.getInstance().getNotSeenInboxMessages();
                    textArea.setText("");
                    for (Message value : messages) {
                        textArea.setText(textArea.getText() + EmailMessageReader.getAllMessage(value));
                        textArea.setText(textArea.getText() + "\n");
                    }
                } else
                    newAlert(Alert.AlertType.ERROR, ERROR, ERROR_NO_UNREAD_MESSAGES);
            } catch (MessagingException e) {
                newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
            }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
        }
    }

    public void loadNotSeenMessages() {
        if (userModel.isLogged()) {
            try {
                if (YandexMailModel.getInstance().notSeenMessagesCount() > 0) {
                    YandexMailModel.getInstance().loadNotSeenInboxMessage(rootFolder);
                } else
                    newAlert(Alert.AlertType.ERROR, ERROR, ERROR_NO_UNREAD_MESSAGES);
            } catch (MessagingException e) {
                newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
            }
            showAllStudents();
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
        }
    }

    public void showAllStudents() {
        students = studentModel.getStudents();
        if (students == null)
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_STUDENTS);
        else
            studentsTable.setItems(students);
    }

    public void yandexQuit() {
        if (userModel.isLogged()) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_SURE_TO_QUIT_ACCOUNT);
            if (option.isPresent())
                if (option.get() == ButtonType.OK) {
                    changeUser();
                }
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
        }
    }

    public void sendEmail() {
        if (userModel.isLogged()) {
            newAlert(Alert.AlertType.ERROR, ERROR, "Вы не вошли в почтовый аккаунт (Яндекс почта -> Войти в почту)");
        } else if (message.getText().isEmpty()) {
            newAlert(Alert.AlertType.ERROR, ERROR, "Вы не ввели сообщение");
        } else if (toEmail.getText().isEmpty() || themeStage.getText().isEmpty()) {
            newAlert(Alert.AlertType.ERROR, ERROR, "Вы не выбрали студента из таблицы");
        } else {
            try {
                YandexMailModel.getInstance().sendMessage(toEmail.getText(), themeStage.getText(), message.getText());
                newAlert(Alert.AlertType.INFORMATION, INFORMATION, "Сообщение успешно отправлено");
            } catch (MessagingException e) {
                newAlert(Alert.AlertType.ERROR, ERROR, "Сообщение не было отправлено");
            }
        }
    }

    public void deleteStudent() {
        try {
            StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
            if (student != null) {
                File file = new File(student.getFolderPath());
                if (file.exists()) {
                    Optional<ButtonType> option1 = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, "Удалить папку студента?");
                    if (option1.isPresent())
                        if (option1.get() == ButtonType.OK)
                            deleteDirectory(file);
                }
                Optional<ButtonType> option2 = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_DELETE_DIALOG);
                if (option2.isPresent())
                    if (option2.get() == ButtonType.OK) {
                        if (userModel.isLogged())
                            YandexMailModel.getInstance().deleteInboxDialogMessages(student.getEmailAddress(), null);
                        else
                            changeUser();
                    }
                if (new File(student.getFolderPath()).exists() && YandexMailModel.getInstance().getInboxDialogMessages(student.getEmailAddress(), null).isEmpty()) {
                    studentModel.deleteStudent(student.getId());
                    showAllStudents();
                }
            } else {
                newAlert(Alert.AlertType.ERROR, ERROR, "Вы не выбрали студента");
            }
        } catch (MessagingException e) {
            newAlert(Alert.AlertType.ERROR, ERROR, ERROR_WRONG_LOGIN_OR_PASSWORD);
        }
    }

    public void changeDir() {
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_SURE_TO_CHANGE_DIRECTORY);
        if (option.isPresent())
            if (option.get() == ButtonType.OK) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(rootFolder);
                fileChooser.setDialogTitle("Выберите папку");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);

                if (fileChooser.showOpenDialog(new JButton()) == JFileChooser.APPROVE_OPTION) {
                    rootFolder = fileChooser.getSelectedFile();
                    if (rootFolder != null) {
                        studentModel.changeDir(rootFolder);
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
            newAlert(Alert.AlertType.ERROR, ERROR, "Вы не выбрали студента");
        }
    }

    public void changeStatus() {
        /*if (userModel.isLogged()) {
            Student student = studentsTable.getSelectionModel().getSelectedItem();
            if (student != null) {
                Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, "Вы действительно хотите изменить статус выбранного студента на " + student.getNextStatus() + "?");
                if (option.isPresent()) {
                    if (option.get() == ButtonType.OK) {
                        String curStat = student.getStatus();
                        String nextStat = student.getNextStatus();
                        if (!curStat.trim().equalsIgnoreCase(nextStat.trim())) {
                            student.setStatus(student.getNextStatusInt());
                            if (!nextStat.trim().equalsIgnoreCase("завершено")) {
                                try {
                                    yandex.sendMessage(student.getEmailAddress(), student.getStage(), "Статус этапа '" + student.getStage() + "' изменен на '" + student.getStatus() + "'.");
                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String curStage = student.getStage();
                                String nextStage = student.getNextStage();
                                if (!curStage.trim().equalsIgnoreCase(nextStage.trim())) {
                                    try {
                                        yandex.sendMessage(student.getEmailAddress(), student.getStage(), "Статус этапа '" + student.getStage() + "' изменен на '" + student.getStatus() + "'.\n" +
                                                "Можете переходить на этап '" + nextStage + "'.");
                                    } catch (MessagingException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        yandex.sendMessage(student.getEmailAddress(), student.getStage(), "Статус этапа '" + student.getStage() + "' изменен на '" + student.getStatus() + "'.\n" +
                                                "Вы завершили курсовой проект.");
                                    } catch (MessagingException e) {
                                        e.printStackTrace();
                                    }
                                    option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, "Вы хотите удалить все данные данного студента? (почту, файлы)");
                                    if (option.isPresent())
                                        if (option.get() == ButtonType.OK) {
                                            File file = new File(student.getFolderPath());
                                            file.delete();

                                        }
                                }
                            }
                            dataBase.updateStudent(student);
                            showAllStudents();
                        }
                    }
                }
            } else {
                newAlert(Alert.AlertType.ERROR, ERROR, "Вы не выбрали студента");
            }
        } else
            changeUser();*/
    }

    public void changePersonal() {
        StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
        if (student != null) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, "Вы действительно хотите изменить персональные данные студента?");
            if (option.isPresent())
                if (option.get() == ButtonType.OK) {
                    newPersonalWindow(student);
                }
        } else {
            newAlert(Alert.AlertType.ERROR, ERROR, "Вы не выбрали студента");
        }
    }

    public void getDialogWithStudent() {
        if (userModel.isLogged()) {
            StudentEntity student = studentsTable.getSelectionModel().getSelectedItem();
            if (student != null) {
                chooseStageForDialog(student);
            } else newAlert(Alert.AlertType.ERROR, ERROR, ERROR_CHOOSE_STUDENT);
        } else {
            newAlert(Alert.AlertType.INFORMATION, INFORMATION, INFORMATION_NO_USER);
        }
    }

    public void changeAutoMessages() {
        Stage stage = getScene("../views/AutoMessagesView.fxml", "Авто-сообщения");
        if (stage != null) stage.showAndWait();
    } // ✓

    public void changeUser() {
        if (userModel.isLogged()) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, CONFIRMATION, CONFIRMATION_LOGGED_SURE_TO_QUIT_ACCOUNT);
            if (option.isPresent()) {
                if (option.get() != ButtonType.OK)
                    return;
            } else {
                return;
            }
        }
        Stage stage = getScene("../views/UserView.fxml", "Войдите в аккаунт");
        if (stage != null) stage.showAndWait();
    } // ✓
}