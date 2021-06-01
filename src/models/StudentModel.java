package models;

import entities.StudentEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentModel extends BaseModel {

    private static StudentModel instance = null;

    public static synchronized StudentModel getInstance() {
        if (instance == null)
            instance = new StudentModel();
        return instance;
    }

    private StudentModel() {
    }

    public StudentEntity getStudent(String emailAddress) {
        return getStudentFromResultSet(
                super.dataBaseHandler.executeQuery(
                        "SELECT id, personal, emailAddress, folderPath, id_stage, id_status, fileCount " +
                                "FROM Student " +
                                "WHERE emailAddress = '" + emailAddress + "'"
                )
        );
    }

    public ObservableList<StudentEntity> getStudents() {
        return getStudentsFromResultSet(
                super.dataBaseHandler.executeQuery(
                        "SELECT id, personal, emailAddress, folderPath, id_stage, id_status, fileCount " +
                                "FROM Student"
                )
        );
    }

    public void addStudent(StudentEntity student) {
        super.dataBaseHandler.executeUpdate(
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
        super.dataBaseHandler.executeUpdate(
                "DELETE FROM Student " +
                        "WHERE id = " + id
        );
    }

    public void updateStudent(StudentEntity student) {
        super.dataBaseHandler.executeUpdate(
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
        getStudents().forEach(student -> super.dataBaseHandler.executeUpdate(
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
                            StageModel.getInstance().getStage(resultSet.getInt("id_stage")),
                            StatusModel.getInstance().getStatus(resultSet.getInt("id_status")),
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
                                StageModel.getInstance().getStage(resultSet.getInt("id_stage")),
                                StatusModel.getInstance().getStatus(resultSet.getInt("id_status")),
                                resultSet.getInt("fileCount")
                        )
                );
                if (!students.isEmpty()) return students;
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

}
