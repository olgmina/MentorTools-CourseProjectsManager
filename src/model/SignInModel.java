package model;

public class SignInModel {

    private String email;
    private String password;
    private String personal;

    public SignInModel(String email, String password, String personal) {
        this.email = email;
        this.password = password;
        this.personal = personal;
    }

    SignInModel() {
        email = "unknown@yandex.ru";
        password = "password";
        personal = "unknown";
    }

    public String getEmail() {
        return email;
    }

    void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    public String getPersonal() {
        return personal;
    }

    void setPersonal(String personal) {
        this.personal = personal;
    }
}
