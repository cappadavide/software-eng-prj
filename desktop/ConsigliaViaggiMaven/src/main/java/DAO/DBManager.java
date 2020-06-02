package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {
    private static final String urlDB = "consigliaviaggidb.cb6rvhwngqbh.eu-west-3.rds.amazonaws.com";
    private static final String port = "5432";
    private static final String nameDatabase = "postgres";

    public static Connection getConnection(boolean autoCommit) throws SQLException {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://" + urlDB + ":" + port + "/" + nameDatabase, "root", "ingsw192016");
            if (!autoCommit) {
                connection.setAutoCommit(false);
            }
            return connection;   
    }
}
