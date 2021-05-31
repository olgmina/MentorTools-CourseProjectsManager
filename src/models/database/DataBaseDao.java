package models.database;

import java.sql.ResultSet;

public interface DataBaseDao {

    ResultSet executeQuery(String sql);

    void executeUpdate(String sql);

    void migrate();

}
