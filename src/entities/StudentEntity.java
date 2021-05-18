package entities;

import models.StageModel;
import models.StatusModel;

public class StudentEntity {

    private final int id;
    private String personal;
    private final String emailAddress;
    private String folderPath;
    private StageEntity stage;
    private StatusEntity status;
    private int fileCount;

    public StudentEntity(int id, String personal, String emailAddress, String folderPath, StageEntity stage, StatusEntity status, int fileCount) {
        this.id = id;
        this.personal = personal;
        this.emailAddress = emailAddress;
        this.folderPath = folderPath;
        this.stage = stage;
        this.status = status;
        this.fileCount = fileCount;
    }

    public int getId() {
        return id;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public StageEntity getStage() {
        return stage;
    }

    public void setStage(StageEntity stage) {
        this.stage = stage;
    }

    public StatusEntity getStatus() {
        return status;
    }

    public void setStatus(StatusEntity status) {
        this.status = status;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

}
