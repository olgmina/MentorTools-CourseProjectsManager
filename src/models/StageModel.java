package models;

import entities.StageEntity;
import javafx.collections.ObservableList;

public class StageModel extends BaseModel {

    private static StageModel instance = null;

    public static synchronized StageModel getInstance() {
        if (instance == null)
            instance = new StageModel();
        return instance;
    }

    private StageModel() {
    }

    public StageEntity getStage(int id) {
        return dataBaseModel.getStage(id);
    }

    public StageEntity getNextStage(StageEntity currentStage) {
        return dataBaseModel.getNextStage(currentStage);
    }

    public StageEntity getFirstStage() {
        return dataBaseModel.getFirstStage();
    }

    public StageEntity getLastStage() {
        return dataBaseModel.getLastStage();
    }

    public ObservableList<StageEntity> getStages() {
        return dataBaseModel.getStages();
    }

    public void addStage(StageEntity stage) {
        dataBaseModel.addStage(stage);
    }

    public void deleteStage(int id) {
        dataBaseModel.deleteStage(id);
    }

    public void updateStage(StageEntity stage) {
        dataBaseModel.updateStage(stage);
    }

    public boolean isStageCorrect(String name) {
        return dataBaseModel.isStageCorrect(name);
    }
}
