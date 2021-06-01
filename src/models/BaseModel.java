package models;

import models.database.DataBaseDao;
import models.database.SqliteDataBaseHandler;

public abstract class BaseModel {
    protected static DataBaseDao dataBaseHandler = SqliteDataBaseHandler.getInstance();
}
