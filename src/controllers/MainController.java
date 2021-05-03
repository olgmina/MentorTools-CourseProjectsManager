package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.*;

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
    public TableView<Student> studentsTable;
    public TableColumn<Student, Integer> ID;
    public TableColumn<Student, String> PERSONAL;
    public TableColumn<Student, String> EMAIL;
    public TableColumn<Student, String> FOLDER;
    public TableColumn<Student, String> STAGE;
    public TableColumn<Student, String> STATUS;
    public TableColumn<Student, String> FILES;
    public TextField toEmail;
    public TextField themeStage;
    public TextArea message;
    private DbHandler dataBase;
    private Mail yandex;
    private boolean signIn;
    private ObservableList<Student> students = null;
    private File rootFolder;

    @Override
    public void initialize( URL location, ResourceBundle resources) {
        rootFolder = new java.io.File("D:\\students\\");

        studentsTable.setOnMouseClicked(e -> {
            try {
                Student student = studentsTable.getSelectionModel().getSelectedItem();
                toEmail.setText(student.getEmailAddress());
                themeStage.setText(student.getStage());
            } catch (NullPointerException ignore) {
            }
        });

        textArea.setEditable(false);
        dataBase = DbHandler.getInstance();

        changeUser();

        students = dataBase.getStudents();
        ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        PERSONAL.setCellValueFactory(new PropertyValueFactory<>("personal"));
        EMAIL.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));
        FOLDER.setCellValueFactory(new PropertyValueFactory<>("folderPath"));
        STAGE.setCellValueFactory(new PropertyValueFactory<>("stage"));
        STATUS.setCellValueFactory(new PropertyValueFactory<>("status"));
        FILES.setCellValueFactory(new PropertyValueFactory<>("fileCount"));
        studentsTable.setItems(students);
    }

    //TODO: изменять пенсональные данные
    private void newPersonalWindow(Student student) {}

    private void chooseStageForDialog(Student student) {
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
                newAlert(Alert.AlertType.INFORMATION, "Информация", "Диалог пуст");
            stageWindow.close();
        });
        stageWindow.showAndWait();*/
    }

    private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null)
                for (File file : files) {
                    if (file.isDirectory())
                        deleteDirectory(file);
                    else
                        System.out.println(file.delete());
                }
        }
        return (path.delete());
    }

    public void changeUser() {
        showScene("../view/UserView.fxml", "Sign in");
    }

    public void close() {
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Вы уверены, что хотите завершить работу программы?\n");
        if (option.isPresent())
            if (option.get() == ButtonType.OK)
                System.exit(0);
    }

    public void inboxCount() {
        if (signIn)
            newAlert(Alert.AlertType.INFORMATION, "Информация", "У вас " + yandex.inboxMessagesCount() + " входящих сообщений.\n" +
                    "Из них " + yandex.notSeenMessagesCount() + " непрочитанных.");
        else
            changeUser();
    }

    public void inboxNotSeenCount() {
        if (signIn)
            newAlert(Alert.AlertType.INFORMATION, "Информация", "У вас " + yandex.notSeenMessagesCount() + " непрочитанных сообщений");
        else
            changeUser();
    }

    public void showNotSeenMessages() {
        if (signIn) {
            if (yandex.notSeenMessagesCount() > 0) {
                ArrayList<Message> messages = yandex.getNotSeenInboxMessages();
                textArea.setText("");
                for (Message value : messages) {
                    textArea.setText(textArea.getText() + EmailMessageReader.getAllMessage(value));
                    textArea.setText(textArea.getText() + "\n");
                }
            } else
                newAlert(Alert.AlertType.ERROR, "Ошибка", "У вас нет непрочитанных сообщений\n" +
                        "или нестабильное интернет соединение");
        } else {
            changeUser();
        }
    }

    public void loadNotSeenMessages() {
        if (signIn) {
            if (yandex.notSeenMessagesCount() > 0) {
                yandex.loadNotSeenInboxMessage(dataBase, rootFolder);
            } else
                newAlert(Alert.AlertType.ERROR, "Ошибка", "У вас нет непрочитанных сообщений\n" +
                        "или нестабильное интернет соединение");
            showAllStudents();
        } else {
            changeUser();
        }
    }

    public void showAllStudents() {
        //studentsTable.getItems().clear();
        students = dataBase.getStudents();
        if (students == null)
            newAlert(Alert.AlertType.INFORMATION, "Инфортация", "В базе данных нет студентов.");
        else
            studentsTable.setItems(students);
    }

    public void yandexQuit() {
        if (signIn) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Вы уверены, что хотите выйти из почтового аккаунта?");
            if (option.isPresent())
                if (option.get() == ButtonType.OK) {
                    signIn = dataBase.isLogged();
                    changeUser();
                }
        } else {
            newAlert(Alert.AlertType.INFORMATION, "Инфортация", "Вы не вошли в почтовый аккаунт");
        }
    }

    public void sendEmail() {
        if (message.getText().isEmpty()) {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не ввели сообщение");
        } else if (toEmail.getText().isEmpty() || themeStage.getText().isEmpty()) {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не выбрали студента из таблицы");
        } else if (!signIn) {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не вошли в почтовый аккаунт (Яндекс почта -> Войти в почту)");
        } else {
            try {
                yandex.sendMessage(toEmail.getText(), themeStage.getText(), message.getText());
                newAlert(Alert.AlertType.INFORMATION, "Информация", "Сообщение успешно отправлено");
            } catch (MessagingException e) {
                newAlert(Alert.AlertType.INFORMATION, "Ошибка", "Сообщение не было отправлено");
            }
        }
    }

    public void deleteStudent() {
        Student student = studentsTable.getSelectionModel().getSelectedItem();
        if (student != null) {
            File file = new File(student.getFolderPath());
            if (file.exists()) {
                Optional<ButtonType> option1 = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Удалить папку студента?");
                if (option1.isPresent())
                    if (option1.get() == ButtonType.OK)
                        System.out.println(deleteDirectory(file));
            }
            Optional<ButtonType> option2 = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Удалить переписку со студентом?");
            if (option2.isPresent())
                if (option2.get() == ButtonType.OK)
                    yandex.deleteInboxDialogMessages(student.getEmailAddress());
            if (new File(student.getFolderPath()).exists() && yandex.getInboxDialogMessages(student.getEmailAddress()).isEmpty()) {
                dataBase.deleteStudent(student.getId());
                showAllStudents();
            }
        } else {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не выбрали студента");
        }
    }

    public void changeDir() {
        Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Вы уверены, что хотите изменить директорию для загрузки файлов?\n" +
                "Данная директория изменится для всех существующих студентов");
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
                        dataBase.changeDir(rootFolder);
                        showAllStudents();
                    }
                }
            }

    }

    public void openDir() {
        Student student = studentsTable.getSelectionModel().getSelectedItem();
        if (student != null) {
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(new File(student.getFolderPath()));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не выбрали студента");
        }
    }

    public void changeStatus() {
        if (signIn) {
            Student student = studentsTable.getSelectionModel().getSelectedItem();
            if (student != null) {
                Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Вы действительно хотите изменить статус выбранного студента на " + student.getNextStatus() + "?");
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
                                    option = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Вы хотите удалить все данные данного студента? (почту, файлы)");
                                    if (option.isPresent())
                                        if (option.get() == ButtonType.OK) {
                                            File file = new File(student.getFolderPath());
                                            System.out.println(file.delete());

                                        }
                                }
                            }
                            dataBase.updateStudent(student);
                            showAllStudents();
                        }
                    }
                }
            } else {
                newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не выбрали студента");
            }
        } else
            changeUser();
    }

    public void changePersonal() {
        Student student = studentsTable.getSelectionModel().getSelectedItem();
        if (student != null) {
            Optional<ButtonType> option = newAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", "Вы действительно хотите изменить персональные данные студента?");
            if (option.isPresent())
                if (option.get() == ButtonType.OK) {
                    newPersonalWindow(student);
                }
        } else {
            newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не выбрали студента");
        }
    }

    public void getDialogWithStudent() {
        if (signIn) {
            Student student = studentsTable.getSelectionModel().getSelectedItem();
            if (student != null) {
                chooseStageForDialog(student);
            } else
                newAlert(Alert.AlertType.ERROR, "Ошибка", "Вы не выбрали студента");
        } else
            changeUser();
    }

    public void changeAutoMessages() {
        showScene("../view/AutoMessagesView.fxml", "Auto-messages");
    }
}