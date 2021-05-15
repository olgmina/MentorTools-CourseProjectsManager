package models;

import entities.UserEntity;

public class UserModel extends BaseModel {

    private static UserModel instance = null;

    public static synchronized UserModel getInstance() {
        if (instance == null)
            instance = new UserModel();
        return instance;
    }

    public UserEntity getUser() {
        return dbHandler.getUser();
    }

    public void insertUser(UserEntity user) {
        dbHandler.insertUser(user);
    }

    public boolean isLogged() {
        return dbHandler.isLogged();
    }

}
