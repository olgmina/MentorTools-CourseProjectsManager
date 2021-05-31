package models.database;

import org.sqlite.JDBC;

import java.io.File;
import java.sql.*;

public class DataBaseHandler implements DataBaseDao {

    private static DataBaseDao instance = null;
    private Connection connection;

    public static DataBaseDao getInstance() {
        if (instance == null)
            instance = new DataBaseHandler();
        return instance;
    }

    private DataBaseHandler() {
        File rootFolder = new File(System.getenv("APPDATA") + File.separator + "CourseProjectsManager" + File.separator + "data");
        if (!rootFolder.exists()) rootFolder.mkdirs();

        File db = new java.io.File(rootFolder.getAbsolutePath() + File.separator + "Database.sqlite");
        boolean migrationNeeded = !db.exists();

        String CON_STR = "jdbc:sqlite:" + db.getAbsolutePath();
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        if (migrationNeeded) migrate();
    }

    @Override
    public ResultSet executeQuery(String sql) {
        try {
            Statement statement = this.connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public void executeUpdate(String sql) {
        try {
            Statement statement = this.connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException ignored) {
        }
    }

    @Override
    public void migrate() {
        createTableUser();
        createTableAutoMessage();
        createTableStage();
        createTableStatus();
        createTableStudent();
    }

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

    private void createTableStage() {
        executeUpdate(
                "create table Stage(" +
                        "id integer not null constraint Stage_pk primary key autoincrement,\n" +
                        "name text not null" +
                        ");" +
                        "create unique index Stage_name_uindex on Stage (name);" +
                        "insert into Stage (name) " +
                        "VALUES" +
                        "('задание'), " +
                        "('разработка программы'), " +
                        "('оформление пояснительной записки');"
        );
    }

    private void createTableStatus() {
        executeUpdate(
                "create table Status(" +
                        "id integer not null constraint Status_pk primary key autoincrement, " +
                        "name text not null" +
                        ");" +
                        "create unique index Status_name_uindex on Status (name);" +
                        "insert into Status (name) " +
                        "VALUES" +
                        "('ожидание'), " +
                        "('рецензирование'), " +
                        "('на исправлении'), " +
                        "('завершено');"
        );
    }

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

}