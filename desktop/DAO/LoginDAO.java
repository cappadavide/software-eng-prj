package DAO;

import java.sql.*;
import consigliaviaggi.*;

public class LoginDAO {
    private static final String url = "consigliaviaggidb.cb6rvhwngqbh.eu-west-3.rds.amazonaws.com";
    private static final String port = "5432";
    private static final String nameDatabase = "postgres";
            
    public Connection connect(String user, String password) throws SQLException {
        System.out.println("Provo a connettermi con "+user+" e "+password);
        Connection connection = DriverManager.getConnection("jdbc:postgresql://"+url+":"+port+"/"+nameDatabase,user,password);
        
        System.out.println("Connessione avvenuta con successo!");
        return connection;
    }
    
}
