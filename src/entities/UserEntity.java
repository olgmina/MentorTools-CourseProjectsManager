package entities;

public class UserEntity {

    private int     id;
    private String  username;
    private String  password;
    private String  personal;

    public UserEntity(int id, String username, String password, String personal) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.personal = personal;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPersonal() {
        return personal;
    }

}
