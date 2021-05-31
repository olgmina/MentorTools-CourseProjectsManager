package models;

import entities.AutoMessageEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AutoMessageModel extends BaseModel{

    public static final String THEME                           = "THEME";
    public static final String TEXT_STAGE_IS_ALREADY_COMPLETED = "TEXT_STAGE_IS_ALREADY_COMPLETED";
    public static final String TEXT_YOU_HAVE_NOT_DONE_STAGE    = "TEXT_YOU_HAVE_NOT_DONE_STAGE";
    public static final String TEXT_WRONG_FORMAT               = "TEXT_WRONG_FORMAT";
    public static final String TEXT_NOT_NEXT_STAGE             = "TEXT_NOT_NEXT_STAGE";
    public static final String TEXT_COURSE_PROJECT_COMPLETED   = "TEXT_COURSE_PROJECT_COMPLETED";
    public static final String TEXT_ANSWERED_ON_DATE           = "TEXT_ANSWERED_ON_DATE";
    public static final String TEXT_STATUS_CHANGED             = "TEXT_STATUS_CHANGED";
    private static AutoMessageModel instance = null;

    public static synchronized AutoMessageModel getInstance() {
        if (instance == null)
            instance = new AutoMessageModel();
        return instance;
    }

    private AutoMessageModel() { }

    public AutoMessageEntity getAutoMessage(String name) {
        return getAutoMessageFromResultSet(
                dataBaseHandler.executeQuery(
                        "SELECT id, message_name, message_text " +
                                "FROM Auto_message " +
                                "WHERE message_name = '" + name + "'"
                )
        );
    }

    public void updateAutoMessage(AutoMessageEntity autoMessage) {
        dataBaseHandler.executeUpdate(
                "UPDATE Auto_message " +
                        "SET " +
                        "message_text = '" + autoMessage.getText() + "' " +
                        "WHERE id = " + autoMessage.getId() + ""
        );
    }

    private AutoMessageEntity getAutoMessageFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return new AutoMessageEntity(
                            resultSet.getInt("id"),
                            resultSet.getString("message_name"),
                            resultSet.getString("message_text")
                    );
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

}
