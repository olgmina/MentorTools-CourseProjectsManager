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
        return dbHandler.getStatus(id);
    }

    public ObservableList<StatusEntity> getStatuses() {
        return dbHandler.getStatuses();
    }

    public void addStatus(StatusEntity status) {
        dbHandler.addStatus(status);
    }

    public void deleteStatus(int id) {
        dbHandler.deleteStatus(id);
    }

    public void updateStatus(StatusEntity status) {
        dbHandler.updateStatus(status);
    }
}
