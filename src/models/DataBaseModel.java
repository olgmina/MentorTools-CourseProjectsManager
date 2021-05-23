package models;

import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sqlite.JDBC;

import java.io.File;
import java.net.URL;
import java.sql.*;

public class DataBaseModel {

    private static final String CON_STR = "jdbc:sqlite::resource:resources/DataBase";
    private static DataBaseModel instance = null;
    private Connection connection;

    public static DataBaseModel getInstance() {
        if (instance == null)
            instance = new DataBaseModel();
        return instance;
    }

    private DataBaseModel() {
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

    public StudentEntity getStudent(String emailAddress) {
        return getStudentFromResultSet(
                executeQuery(
                        "SELECT id, personal, emailAddress, folderPath, id_stage, id_status, fileCount " +
                                "FROM Student " +
                                "WHERE emailAddress = '" + emailAddress + "'"
                )
        );
    }

    public ObservableList<StudentEntity> getStudents() {
        return getStudentsFromResultSet(
                executeQuery(
                        "SELECT id, personal, emailAddress, folderPath, id_stage, id_status, fileCount " +
                                "FROM Student"
                )
        );
    }

    public void addStudent(StudentEntity student) {
        executeUpdate(
                "INSERT INTO Student(" +
                        "personal, " +
                        "emailAddress, " +
                        "folderPath, " +
                        "id_stage, " +
                        "id_status" +
                        ") " +
                        "VALUES(" +
                        "'" + student.getPersonal() + "'," +
                        "'" + student.getEmailAddress() + "'," +
                        "'" + student.getFolderPath() + "'," +
                        student.getStage().getId() + "," +
                        student.getStatus().getId() +
                        ")"
        );
    }

    public void deleteStudent(int id) {
        executeUpdate(
                "DELETE FROM Student " +
                        "WHERE id = " + id
        );
    }

    public void updateStudent(StudentEntity student) {
        executeUpdate(
                "UPDATE Student " +
                        "SET " +
                        "personal = '" + student.getPersonal() + "', " +
                        "folderPath = '" + student.getFolderPath() + "', " +
                        "id_stage = " + student.getStage().getId() + ", " +
                        "id_status = " + student.getStatus().getId() + ", " +
                        "fileCount = " + student.getFileCount() + " " +
                        "WHERE id = " + student.getId()
        );
    }

    public void changeDir(File file) {
        this.getStudents().forEach(student -> executeUpdate(
                "UPDATE Student " +
                        "SET " +
                        "folderPath = '" + file.getPath() + "\\" + student.getEmailAddress() + "' " +
                        "WHERE id = " + student.getId()
                )
        );
    }

    private StudentEntity getStudentFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return new StudentEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("personal"),
                            resultSet.getString("emailAddress"),
                            resultSet.getString("folderPath"),
                            getStage(resultSet.getInt("id_stage")),
                            getStatus(resultSet.getInt("id_status")),
                            resultSet.getInt("fileCount")
                    );
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    private ObservableList<StudentEntity> getStudentsFromResultSet(ResultSet resultSet) {
        ObservableList<StudentEntity> students = FXCollections.observableArrayList();
        if (resultSet != null) {
            try {
                while (resultSet.next()) students.add(
                        new StudentEntity(
                                resultSet.getInt("id"),
                                resultSet.getString("personal"),
                                resultSet.getString("emailAddress"),
                                resultSet.getString("folderPath"),
                                getStage(resultSet.getInt("id_stage")),
                                getStatus(resultSet.getInt("id_status")),
                                resultSet.getInt("fileCount")
                        )
                );
                if (!students.isEmpty()) return students;
            } catch (SQLException ignored) {}
        }
        return null;
    }

    /*---------------------------------------------------------
    --------------Stage----------------------------------------
    ---------------------------------------------------------*/

    public StageEntity getStage(int id) {
        return getStageFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "WHERE id = " + id + ""
                )
        );
    }

    public StageEntity getStage(String name) {
        return getStageFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "WHERE name = '" + name + "'"
                )
        );
    }

    public StageEntity getNextStage(StageEntity currentStage) {
        return getStageFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "WHERE id > " + currentStage.getId() + "" +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StageEntity getFirstStage() {
        return getStageFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StageEntity getLastStage() {
        return getStageFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "ORDER BY id DESC " +
                                "LIMIT 1"
                )
        );
    }

    public ObservableList<StageEntity> getStages() {
        return getStagesFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Stage"
                )
        );
    }

    public void addStage(StageEntity stage) {
        executeUpdate(
                "INSERT INTO Stage(name) " +
                        "VALUES('" + stage.getName() + "')"
        );
    }

    public void deleteStage(int id) {
        executeUpdate(
                "DELETE FROM Stage " +
                        "WHERE id = " + id
        );
    }

    public void updateStage(StageEntity stage) {
        executeUpdate(
                "UPDATE Stage " +
                        "SET " +
                        "name = '" + stage.getName() + "' " +
                        "WHERE id = " + stage.getId()
        );
    }

    public boolean isStageCorrect(String name) {
        if (getStage(name) != null)
            return true;
        return false;
    }

    private StageEntity getStageFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return new StageEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    private ObservableList<StageEntity> getStagesFromResultSet(ResultSet resultSet) {
        ObservableList<StageEntity> stages = FXCollections.observableArrayList();
        if (resultSet != null) {
            try {
                while (resultSet.next()) stages.add(
                        new StageEntity(
                                resultSet.getInt("id"),
                                resultSet.getString("name")
                        )
                );
                if (!stages.isEmpty()) return stages;
            } catch (SQLException ignored) {}
        }
        return null;
    }

    /*---------------------------------------------------------
    --------------Status---------------------------------------
    ---------------------------------------------------------*/

    public StatusEntity getStatus(int id) {
        return getStatusFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "WHERE id = " + id + ""
                )
        );
    }

    public StatusEntity getNextStatus(StatusEntity currentStatus) {
        return getStatusFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "WHERE id > " + currentStatus.getId() + " " +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StatusEntity getFirstStatus() {
        return getStatusFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StatusEntity getLastStatus() {
        return getStatusFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "ORDER BY id DESC " +
                                "LIMIT 1"
                )
        );
    }

    public ObservableList<StatusEntity> getStatuses() {
        return getStatusesFromResultSet(
                executeQuery(
                        "SELECT id, name " +
                                "FROM Status"
                )
        );
    }

    public void addStatus(StatusEntity status) {
        executeUpdate(
                "INSERT INTO Status(name) " +
                        "VALUES('" + status.getName() + "')"
        );
    }

    public void deleteStatus(int id) {
        executeUpdate(
                "DELETE FROM Status " +
                        "WHERE id = " + id
        );
    }

    public void updateStatus(StatusEntity status) {
        executeUpdate(
                "UPDATE Status " +
                        "SET " +
                        "name = '" + status.getName() + "' " +
                        "WHERE id = " + status.getId()
        );
    }

    private StatusEntity getStatusFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    return new StatusEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                }
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    private ObservableList<StatusEntity> getStatusesFromResultSet(ResultSet resultSet) {
        ObservableList<StatusEntity> statuses = FXCollections.observableArrayList();
        if (resultSet != null) {
            try {
                while (resultSet.next()) statuses.add(
                        new StatusEntity(
                                resultSet.getInt("id"),
                                resultSet.getString("name")
                        )
                );
                if (!statuses.isEmpty()) return statuses;
            } catch (SQLException ignored) {}
        }
        return null;
    }

    /*---------------------------------------------------------
    --------------User-----------------------------------------
    ---------------------------------------------------------*/

    public UserEntity getUser() {
        return getUserFromResultSet(
                executeQuery(
                        "SELECT id, username, password, personal " +
                                "FROM User"
                )
        );
    }

    public void insertUser(UserEntity user) {
        clearUser();
        executeUpdate(
                "INSERT INTO User(username, password, personal) " +
                        "VALUES('" + user.getUsername() + "','" + user.getPassword() + "','" + user.getPersonal() + "')"
        );
    }

    public void updateUser(UserEntity user) {
        executeUpdate(
                "UPDATE User " +
                        "SET " +
                        "personal = '" + user.getPersonal() + "' " +
                        "WHERE id = " + user.getId()
        );
    }

    public void clearUser() {
        executeUpdate(
                "DELETE " +
                        "FROM User"
        );
    }

    public boolean isLogged() {
        return getUser() != null;
    }

    private UserEntity getUserFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return new UserEntity(resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("personal")
                    );
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    /*---------------------------------------------------------
    --------------Auto_Messages--------------------------------
    ---------------------------------------------------------*/

    public AutoMessageEntity getAutoMessage(String name) {
        return getAutoMessageFromResultSet(
                executeQuery(
                        "SELECT id, message_name, message_text " +
                                "FROM Auto_message " +
                                "WHERE message_name = '" + name + "'"
                )
        );
    }

    public void updateAutoMessage(AutoMessageEntity autoMessage) {
        executeUpdate(
                "UPDATE Auto_message " +
                        "SET " +
                        "message_text = '" + autoMessage.getText() + "' " +
                        "WHERE id = " + autoMessage.getId() + ""
        );
    }

    private AutoMessageEntity getAutoMessageFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return new AutoMessageEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("message_name"),
                            resultSet.getString("message_text")
                    );
            } catch (SQLException ignored) {}
        }
        return null;
    }
}