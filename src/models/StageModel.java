package models;

import entities.StageEntity;
import javafx.collections.ObservableList;

public class StageModel extends BaseModel {

    private static StageModel instance = null;

    private static synchronized StageModel getInstance() {
        if (instance == null)
            instance = new StageModel();
        return instance;
    }

    private StageModel() {
    }

    public StageEntity getStage(String id) {
        return dbHandler.getStage(id);
    }

    public ObservableList<StageEntity> getStages() {
        return dbHandler.getStages();
    }

    public void addStage(StageEntity stage) {
        dbHandler.addStage(stage);
    }

    public void deleteStage(int id) {
        dbHandler.deleteStage(id);
    }

    public void updateStage(StageEntity stage) {
        dbHandler.updateStage(stage);
    }
}
