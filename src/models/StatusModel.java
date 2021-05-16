package models;

import entities.StatusEntity;
import javafx.collections.ObservableList;

public class StatusModel extends BaseModel {

    private static StatusModel instance = null;

    private static synchronized StatusModel getInstance() {
        if (instance == null)
            instance = new StatusModel();
        return instance;
    }

    private StatusModel() {
    }

    public StatusEntity getStatus(String id) {
        return dataBaseModel.getStatus(id);
    }

    public ObservableList<StatusEntity> getStatuses() {
        return dataBaseModel.getStatuses();
    }

    public void addStatus(StatusEntity status) {
        dataBaseModel.addStatus(status);
    }

    public void deleteStatus(int id) {
        dataBaseModel.deleteStatus(id);
    }

    public void updateStatus(StatusEntity status) {
        dataBaseModel.updateStatus(status);
    }
}
