package models;

import entities.StatusEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusModel extends BaseModel {

    private static StatusModel instance = null;

    public static synchronized StatusModel getInstance() {
        if (instance == null)
            instance = new StatusModel();
        return instance;
    }

    private StatusModel() { }

    public StatusEntity getStatus(int id) {
        return getStatusFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "WHERE id = " + id
                )
        );
    }

    public StatusEntity getNextStatus(StatusEntity currentStatus) {
        return getStatusFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "WHERE id > " + currentStatus.getId() + " " +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StatusEntity getPreviousStatus(StatusEntity currentStatus) {
        return getStatusFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "WHERE id < " + currentStatus.getId() + " " +
                                "ORDER BY id DESC " +
                                "LIMIT 1"
                )
        );
    }

    public StatusEntity getFirstStatus() {
        return getStatusFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "ORDER BY id " +
                                "LIMIT 1"
                )
        );
    }

    public StatusEntity getLastStatus() {
        return getStatusFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Status " +
                                "ORDER BY id DESC " +
                                "LIMIT 1"
                )
        );
    }

    public ObservableList<StatusEntity> getStatuses() {
        return getStatusesFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, name " +
                                "FROM Status"
                )
        );
    }

    public void addStatus(StatusEntity status) {
        dataBaseHandler.executeUpdate(
                "INSERT INTO Status(name) " +
                        "VALUES('" + status.getName() + "')"
        );
    }

    public void deleteStatus(int id) {
        dataBaseHandler.executeUpdate(
                "DELETE FROM Status " +
                        "WHERE id = " + id
        );
    }

    public void updateStatus(StatusEntity status) {
        dataBaseHandler.executeUpdate(
                "UPDATE Status " +
                        "SET " +
                        "name = '" + status.getName() + "' " +
                        "WHERE id = " + status.getId()
        );
    }

    private StatusEntity getStatusFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next()) {
                    return new StatusEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("name")
                    );
                }
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

    private ObservableList<StatusEntity> getStatusesFromResultSet(ResultSet resultSet) {
        ObservableList<StatusEntity> statuses = FXCollections.observableArrayList();
        if (resultSet != null) {
            try {
                while (resultSet.next()) statuses.add(
                        new StatusEntity(
                                resultSet.getInt("id"),
                                resultSet.getString("name")
                        )
                );
                if (!statuses.isEmpty()) return statuses;
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

}
