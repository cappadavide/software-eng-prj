package DAO;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginDAO {
    private static final String url = "consigliaviaggidb.cb6rvhwngqbh.eu-west-3.rds.amazonaws.com";
    private static final String port = "5432";
    private static final String nameDatabase = "postgres";

    public Connection connect(String user, String password) throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" + nameDatabase, user, password);
    }
}
