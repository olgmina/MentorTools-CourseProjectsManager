package entities;

public class StudentEntity {
    private final int id;
    private String personal;
    private final String emailAddress;
    private String folderPath;
    private int stage;
    private int status;
    private int fileCount;

    public StudentEntity(int id, String personal, String emailAddress, String folderPath, int stage, int status, int fileCount) {
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

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

}
