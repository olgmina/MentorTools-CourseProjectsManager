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
        return dataBaseModel.getUser();
    }

    public void insertUser(UserEntity user) {
        dataBaseModel.insertUser(user);
    }

    public void updateUser(UserEntity user) {
        dataBaseModel.updateUser(user);
    }

    public void clearUser() {
        dataBaseModel.clearUser();
    }

    public boolean isLogged() {
        return dataBaseModel.isLogged();
    }

}
