package models;

import models.database.DataBaseDao;
import models.database.DataBaseHandler;

public abstract class BaseModel {
    protected static DataBaseDao dataBaseHandler = DataBaseHandler.getInstance();
}
