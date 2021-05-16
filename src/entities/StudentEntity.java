package entities;

public class StudentEntity {

    private final int id;
    private String personal;
    private final String emailAddress;
    private String folderPath;
    private int stageId;
    private int statusId;
    private int fileCount;

    public StudentEntity(int id, String personal, String emailAddress, String folderPath, int stageId, int statusId, int fileCount) {
        this.id = id;
        this.personal = personal;
        this.emailAddress = emailAddress;
        this.folderPath = folderPath;
        this.stageId = stageId;
        this.statusId = statusId;
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

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stage) {
        this.stageId = stage;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int status) {
        this.statusId = status;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

}
