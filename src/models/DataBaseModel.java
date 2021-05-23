package models;

import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sqlite.JDBC;

import java.io.File;
import java.net.URL;
import java.sql.*;

public class DataBaseModel {

    private final File rootFolder = new java.io.File(System.getenv("APPDATA") + File.separator + "CourseProjectsManager" + File.separator + "data");
    private static String CON_STR = "jdbc:sqlite:";
    private static DataBaseModel instance = null;
    private Connection connection;

    public static DataBaseModel getInstance() {
        if (instance == null)
            instance = new DataBaseModel();
        return instance;
    }

    private DataBaseModel() {
        if (!rootFolder.exists()) rootFolder.mkdirs();
        File db = new java.io.File(rootFolder.getAbsolutePath() + File.separator + "Database.sqlite");
        boolean migrationNeeded = false;
        if (!db.exists()) migrationNeeded = true;
        CON_STR += db.getAbsolutePath();
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (migrationNeeded) migrate();
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

    private void migrate() {
        createTableUser();
        createTableAutoMessage();
        createTableStage();
        createTableStatus();
        createTableStudent();
    }



    /*---------------------------------------------------------
    --------------Students-------------------------------------
    ---------------------------------------------------------*/

    private void createTableStudent() {
        executeUpdate(
                "create table Student(" +
                        "id integer not null constraint Student_pk primary key autoincrement," +
                        "personal text not null," +
                        "emailAddress text not null," +
                        "folderPath text not null," +
                        "id_stage integer not null references Stage," +
                        "id_status integer not null references Status," +
                        "fileCount integer default 0 not null" +
                        ");"
        );
    }

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

    private void createTableStage() {
        executeUpdate(
                "create table Stage(" +
                        "id integer not null constraint Stage_pk primary key autoincrement,\n" +
                        "name text not null" +
                        ");" +
                        "create unique index Stage_name_uindex on Stage (name);"
        );
    }

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

    private void createTableStatus() {
        executeUpdate(
                "create table Status(" +
                        "id integer not null constraint Status_pk primary key autoincrement, " +
                        "name text not null" +
                        ");" +
                        "create unique index Status_name_uindex on Status (name);"
        );
    }

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

    private void createTableUser() {
        executeUpdate(
                "create table User(" +
                        "id integer not null constraint signIn_pk primary key autoincrement, " +
                        "username text not null, " +
                        "password text not null, " +
                        "personal text " +
                        ");" +
                        "create unique index signIn_password_uindex on User (password);" +
                        "create unique index signIn_username_uindex on User (username);"
        );
    }

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

    private void createTableAutoMessage() {
        executeUpdate(
                "create table Auto_message(" +
                        "id integer constraint Auto_messages_pk primary key autoincrement, " +
                        "message_name text, " +
                        "message_text text" +
                        ");" +
                        "create unique index Auto_messages_message_name_uindex on Auto_message (message_name);" +
                        "insert into Auto_message (message_name, message_text) " +
                        "VALUES" +
                        "('THEME', '#ТЕКУЩИЙ_ЭТАП#'), " +
                        "('TEXT_STAGE_IS_ALREADY_COMPLETED', 'Вы уже завершили этап \"#УКАЗАННЫЙ_ЭТАП#\". Можете переходить к этапу \"#СЛЕДУЮЩИЙ_ЭТАП#\".'), " +
                        "('TEXT_YOU_HAVE_NOT_DONE_STAGE', 'Вы не завершили этап \"#ТЕКУЩИЙ_ЭТАП#\".'), " +
                        "('TEXT_WRONG_FORMAT', 'Файл, который был прикреплен к сообщению имеет недопустимый формат. Допускаются форматы \".pdf, .docx, .doc, .txt, .c\".'), " +
                        "('TEXT_ANSWERED_ON_DATE', 'Отвечено на сообщение, отправленное: #ДАТА_СООБЩЕНИЯ#'), " +
                        "('TEXT_NOT_NEXT_STAGE', 'Вы пропустили один из этапов. Переходите к этапу \"#СЛЕДУЮЩИЙ_ЭТАП#\".'), " +
                        "('TEXT_COURSE_PROJECT_COMPLETED', 'Вы уже завершили курсовой проект.'), " +
                        "('TEXT_STATUS_CHANGED', 'Статус этапа \"#ТЕКУЩИЙ_ЭТАП#\" изменен с \"#ТЕКУЩИЙ_СТАТУС#\" на \"#СЛЕДУЮЩИЙ_СТАТУС#\".');"
        );
    }

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