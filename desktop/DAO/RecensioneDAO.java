package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecensioneDAO {
    private PreparedStatement preparedStatement;
    
    public void approvaRecensione(Connection conn, String idRecensione) throws SQLException{
        String sql = "UPDATE RECENSIONE SET VALUTAZIONE=1 WHERE ID=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, Integer.parseInt(idRecensione));
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    
    public ResultSet ricavaIdDellaStruttura(Connection conn, String nomeStruttura) throws SQLException{
        String sql = "SELECT ID FROM STRUTTURA WHERE NOME=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, nomeStruttura);
        return preparedStatement.executeQuery();
    }
    
    public void aggiornaRatingStruttura(Connection conn, String strutturaID) throws SQLException{
        String sql = "UPDATE STRUTTURA SET RATING=(SELECT AVG(R.RATING) FROM RECENSIONE AS R WHERE R.VALUTAZIONE=1 AND R.STRUTTURAID=?) WHERE STRUTTURA.ID=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, Integer.parseInt(strutturaID));
        preparedStatement.setInt(2, Integer.parseInt(strutturaID));
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    
    public void declinaRecensione(Connection conn, String idRecensione) throws SQLException{
        String sql = "UPDATE RECENSIONE SET VALUTAZIONE=0 WHERE ID=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, Integer.parseInt(idRecensione));
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    
    public ResultSet visualizzaListaRecensioni(Connection conn) throws SQLException {
            String sql = "SELECT R.USERNAMEUTENTE, S.NOME, R.TITOLO, R.RATING, R.CORPO, R.ID FROM RECENSIONE AS R JOIN STRUTTURA AS S on R.VALUTAZIONE=? AND S.ID=R.strutturaid;";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, -1);
            return preparedStatement.executeQuery();
           
    }
}