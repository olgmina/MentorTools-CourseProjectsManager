package controllers;

import entities.StageEntity;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import models.StageModel;

import java.net.URL;
import java.util.ResourceBundle;

public class ChooseStageController extends BaseController implements Initializable {

    public ListView<StageEntity> stageList;
    public static StageEntity stage = null;
    public StageModel stageModel = StageModel.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stageList.setItems(stageModel.getStages());
    }

    public void saveAction() {
        stage = stageList.getSelectionModel().getSelectedItem();
    }
}
