package models;

import entities.StageEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

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
        return getStageFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "WHERE id = " + id + ""
                )
        );
    }

    public StageEntity getStage(String name) {
        return getStageFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "WHERE name = '" + name + "'"
                )
        );
    }

    public StageEntity getNextStage(StageEntity currentStage) {
        return getStageFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "WHERE id > " + currentStage.getId() + " " +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StageEntity getFirstStage() {
        return getStageFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StageEntity getLastStage() {
        return getStageFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Stage " +
                                "ORDER BY id DESC " +
                                "LIMIT 1"
                )
        );
    }

    public ObservableList<StageEntity> getStages() {
        return getStagesFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Stage"
                )
        );
    }

    public void addStage(StageEntity stage) {
        dataBaseHandler.executeUpdate(
                "INSERT INTO Stage(name) " +
                        "VALUES('" + stage.getName() + "')"
        );
    }

    public void deleteStage(int id) {
        dataBaseHandler.executeUpdate(
                "DELETE FROM Stage " +
                        "WHERE id = " + id
        );
    }

    public void updateStage(StageEntity stage) {
        dataBaseHandler.executeUpdate(
                "UPDATE Stage " +
                        "SET " +
                        "name = '" + stage.getName() + "' " +
                        "WHERE id = " + stage.getId()
        );
    }

    public boolean isStageCorrect(String name) {
        if (getStage(name) != null)
            return true;
        return false;
    }

    private StageEntity getStageFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return new StageEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    private ObservableList<StageEntity> getStagesFromResultSet(ResultSet resultSet) {
        ObservableList<StageEntity> stages = FXCollections.observableArrayList();
        if (resultSet != null) {
            try {
                while (resultSet.next()) stages.add(
                        new StageEntity(
                                resultSet.getInt("id"),
                                resultSet.getString("name")
                        )
                );
                if (!stages.isEmpty()) return stages;
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

}
