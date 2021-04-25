package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sqlite.JDBC;

import javax.mail.PasswordAuthentication;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;

public class DbHandler {

    private static final String CON_STR = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\src\\model\\DataBase";
    private static DbHandler instance = null;
    private Connection connection;

    public static synchronized DbHandler getInstance() {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }

    private DbHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private ResultSet executeQuery(String sql) {
        try {
            Statement statement = this.connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException ignored) {}
        return null;
    }

    private void executeUpdate(String sql) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ignored) {}
    }

    /*---------------------------------------------------------
    --------------Students-------------------------------------
    ---------------------------------------------------------*/

    public Student getStudent(String emailAddress) {
        return getStudentFromResultSet(
                executeQuery(
                        "SELECT id, personal, emailAddress, folderPath, id_stage, id_status, fileCount " +
                            "FROM Student " +
                            "WHERE emailAddress = '" + emailAddress + "'"
                )
        );
    }

    public ObservableList<Student> getStudents() {
        return getStudentsFromResultSet(
                executeQuery(
                        "SELECT id, personal, emailAddress, folderPath, id_stage, id_status, fileCount " +
                            "FROM Student"
                )
        );
    }

    public void addStudent(Student student) {
        executeUpdate(
                "INSERT INTO Student(" +
                        "personal, " +
                        "emailAddress, " +
                        "folderPath, " +
                        "id_stage, " +
                        "id_status" +
                        ") " +
                    "VALUES(" +
                        student.getPersonal() + "," +
                        student.getEmailAddress() + "," +
                        student.getFolderPath() + "," +
                        student.getStageInt() + "," +
                        student.getStatusInt() +
                        ")"
        );
    }

    public void deleteStudent(int id) {
        executeUpdate(
                "DELETE FROM Student " +
                    "WHERE id = " + id
        );
    }

    public void updateStudent(Student student) {
        executeUpdate(
                "UPDATE Student " +
                    "SET " +
                        "personal = '" + student.getPersonal() + "', " +
                        "folderPath = '" + student.getFolderPath() + "', " +
                        "id_stage = " + student.getStageInt() + ", " +
                        "id_status = " + student.getStatusInt() + ", " +
                        "fileCount = " + student.getFileCount() + " " +
                    "WHERE `id` = " + student.getId()
        );
    }

    public void changeDir(File file) {
        getStudents().forEach(student -> executeUpdate(
                "UPDATE Student " +
                        "SET " +
                        "folderPath = '" + file.getPath() + "\\" + student.getEmailAddress() + "' " +
                        "WHERE id = " + student.getId()
                )
        );
    }

    private Student getStudentFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                return new Student(
                        resultSet.getInt("id"),
                        resultSet.getString("personal"),
                        resultSet.getString("emailAddress"),
                        resultSet.getString("folderPath"),
                        resultSet.getInt("id_stage"),
                        resultSet.getInt("id_status"),
                        resultSet.getInt("fileCount")
                );
            } catch (SQLException ignored) {}
        }
        return null;
    }

    private ObservableList<Student> getStudentsFromResultSet(ResultSet resultSet) {
        ObservableList<Student> students = FXCollections.observableArrayList();
        if (resultSet != null) {
            try {
                while (resultSet.next()) students.add(getStudentFromResultSet(resultSet));
                if (!students.isEmpty()) return students;
            } catch (SQLException ignored) {}
        }
        return null;
    }

    /*---------------------------------------------------------
    --------------SignIn---------------------------------------
    ---------------------------------------------------------*/

    public void addSignIn(SignInModel user) {
        clearSignIn();
        executeUpdate(
                "INSERT INTO SignIn(" +
                        "username, " +
                        "password, " +
                        "personal " +
                        ") " +
                        "VALUES(" +
                        user.getEmail() + "," +
                        user.getPassword() + "," +
                        user.getPersonal() +
                        ")"
        );
    }

    public void clearSignIn() {
        executeUpdate(
                "DELETE " +
                    "FROM SignIn"
        );
    }

    public boolean isLogged() {
        return getSignIn() != null;
    }

    public SignInModel getSignIn() {
        return getSignInFromResultSet(
                executeQuery(
                        "SELECT id, username, password, personal " +
                                "FROM SignIn"
                )
        );
    }

    private SignInModel getSignInFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                return new SignInModel(
                        resultSet.getString("username"),
                        resultSet.getString("passwords"),
                        resultSet.getString("personal")
                );
            } catch (SQLException ignored) {}
        }
        return null;
    }

    /*---------------------------------------------------------
    --------------Auto_Messages--------------------------------
    ---------------------------------------------------------*/

    public AutoMessagesModel getAutoMessage(String name) {
        return getAutoMessageFromResultSet(
                executeQuery(
                        "SELECT message_name, message_text " +
                            "FROM Auto_message " +
                            "WHERE message_name = '" + name + "'"
                )
        );
    }

    public void updateAutoMessage(AutoMessagesModel autoMessage) {
        executeUpdate(
                "UPDATE Auto_messages " +
                    "SET " +
                    "message_text = " + autoMessage.getText() + " " +
                    "WHERE message_name = " + autoMessage.getName()
        );
    }

    private AutoMessagesModel getAutoMessageFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                return new AutoMessagesModel(
                        resultSet.getString("message_name"),
                        resultSet.getString("message_text")
                );
            } catch (SQLException ignored) {}
        }
        return null;
    }

}