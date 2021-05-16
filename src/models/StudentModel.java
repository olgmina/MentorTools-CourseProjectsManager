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
        return dataBaseModel.getStudent(emailAddress);
    }

    public ObservableList<StudentEntity> getStudents() {
        return dataBaseModel.getStudents();
    }

    public void addStudent(StudentEntity student) {
        dataBaseModel.addStudent(student);
    }

    public void deleteStudent(int id) {
        dataBaseModel.deleteStudent(id);
    }

    public void updateStudent(StudentEntity student) {
        dataBaseModel.updateStudent(student);
    }

    public void changeDir(File file) {
        dataBaseModel.changeDir(file);
    }

}
