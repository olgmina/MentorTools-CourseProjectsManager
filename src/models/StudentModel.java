package models;

import entities.StudentEntity;
import javafx.collections.ObservableList;

import java.io.File;

public class StudentModel extends BaseModel{

    private static StudentModel instance = null;

    private static synchronized StudentModel getInstance() {
        if (instance == null)
            instance = new StudentModel();
        return instance;
    }

    private StudentModel() { }

    public StudentEntity getStudent(String emailAddress) {
        return dbHandler.getStudent(emailAddress);
    }

    public ObservableList<StudentEntity> getStudents() {
        return dbHandler.getStudents();
    }

    public void addStudent(StudentEntity student) {
        dbHandler.addStudent(student);
    }

    public void deleteStudent(int id) {
        dbHandler.deleteStudent(id);
    }

    public void updateStudent(StudentEntity student) {
        dbHandler.updateStudent(student);
    }

    public void changeDir(File file) {
        dbHandler.changeDir(file);
    }

}
