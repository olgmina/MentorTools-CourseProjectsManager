package model;

public class AutoMessagesModel {

    public static final String THEME_WRONG_STAGE                 = "THEME_WRONG_STAGE";
    public static final String THEME_WRONG_FORMAT                = "THEME_WRONG_FORMAT";
    public static final String TEXT_STAGE_IS_ALREADY_COMPLETED   = "TEXT_STAGE_IS_ALREADY_COMPLETED";
    public static final String TEXT_YOU_HAVE_NOT_DONE_STAGE      = "TEXT_YOU_HAVE_NOT_DONE_STAGE";
    public static final String TEXT_WRONG_FORMAT                 = "TEXT_WRONG_FORMAT";
    public static final String TEXT_ANSWERED_ON_DATE             = "TEXT_ANSWERED_ON_DATE";

    private String name;
    private String text;

    public AutoMessagesModel(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
