package models;

import entities.AutoMessageEntity;

public class AutoMessageModel extends BaseModel{

    public static final String THEME_WRONG_STAGE               = "THEME_WRONG_STAGE";
    public static final String THEME_WRONG_FORMAT              = "THEME_WRONG_FORMAT";
    public static final String TEXT_STAGE_IS_ALREADY_COMPLETED = "TEXT_STAGE_IS_ALREADY_COMPLETED";
    public static final String TEXT_YOU_HAVE_NOT_DONE_STAGE    = "TEXT_YOU_HAVE_NOT_DONE_STAGE";
    public static final String TEXT_WRONG_FORMAT               = "TEXT_WRONG_FORMAT";
    public static final String TEXT_ANSWERED_ON_DATE           = "TEXT_ANSWERED_ON_DATE";
    private static AutoMessageModel instance = null;

    public static synchronized AutoMessageModel getInstance() {
        if (instance == null)
            instance = new AutoMessageModel();
        return instance;
    }

    private AutoMessageModel() { }

    public AutoMessageEntity getAutoMessage(String name) {
        return dbHandler.getAutoMessage(name);
    }

    public void saveAutoMessage(AutoMessageEntity autoMessage) {
        dbHandler.updateAutoMessage(autoMessage);
    }

}
