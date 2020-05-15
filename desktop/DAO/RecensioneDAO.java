package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecensioneDAO {
    private static final String url = "consigliaviaggidb.cb6rvhwngqbh.eu-west-3.rds.amazonaws.com";
    private static final String port = "5432";
    private static final String nameDatabase = "postgres";
    PreparedStatement preparedStatement;
    Connection connection;

    public ResultSet visualizzaListaRecensioni() throws SQLException{
        connection = DriverManager.getConnection("jdbc:postgresql://"+url+":"+port+"/"+nameDatabase,"root","ingsw192016");
        String sql = "SELECT * FROM RECENSIONE WHERE VALUTAZIONE=?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, -1);
        return preparedStatement.executeQuery();
    }
}