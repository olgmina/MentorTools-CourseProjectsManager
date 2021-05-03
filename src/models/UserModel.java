package models;

import entities.UserEntity;

public class UserModel extends BaseModel {

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
