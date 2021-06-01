package models;

import entities.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel extends BaseModel {

    private static UserModel instance = null;

    public static synchronized UserModel getInstance() {
        if (instance == null)
            instance = new UserModel();
        return instance;
    }

    public UserEntity getUser() {
        return getUserFromResultSet(
                super.dataBaseHandler.executeQuery(
                        "SELECT id, username, password, personal " +
                                "FROM User"
                )
        );
    }

    public void insertUser(UserEntity user) {
        clearUser();
        super.dataBaseHandler.executeUpdate(
                "INSERT INTO User(username, password, personal) " +
                        "VALUES('" + user.getUsername() + "','" + user.getPassword() + "','" + user.getPersonal() + "')"
        );
    }

    public void updateUser(UserEntity user) {
        super.dataBaseHandler.executeUpdate(
                "UPDATE User " +
                        "SET " +
                        "personal = '" + user.getPersonal() + "' " +
                        "WHERE id = " + user.getId()
        );
    }

    public void clearUser() {
        super.dataBaseHandler.executeUpdate(
                "DELETE " +
                        "FROM User"
        );
    }

    public boolean isLogged() {
        return getUser() != null;
    }

    private UserEntity getUserFromResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.next())
                    return new UserEntity(resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("password"),
                            resultSet.getString("personal")
                    );
            } catch (SQLException ignored) {
            }
        }
        return null;
    }

}
