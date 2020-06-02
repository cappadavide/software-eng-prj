package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Screen;
import javafx.stage.StageStyle;

public class FotoDAO {
    private static final String prefix = "https://consigliaviaggi.s3.eu-west-3.amazonaws.com/";
    private PreparedStatement preparedStatement;

    public void inserisciFoto(Connection conn, String nomeFile, int idStruttura) throws SQLException {
            String sql = "INSERT INTO FOTO(URL,STRUTTURAID) VALUES(?,?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, prefix + nomeFile);
            preparedStatement.setInt(2, idStruttura);
            preparedStatement.executeUpdate();
            preparedStatement.close();   
    }
    
    private Alert creaDialog(String titolo, String corpo) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setHeaderText(titolo);
        if(corpo.contains("foto_pkey")){
            dialog.setContentText("Una o più foto sono state già utilizzate per una struttura presente nel Database. Riprovare.");
        }
        else {
            dialog.setContentText(corpo);
        }
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        dialog.setX((bounds.getMaxX() / 2) - 150);
        dialog.setY((bounds.getMaxY() / 2) - 100);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/alertStruttura.css").toExternalForm());
        dialog.initStyle(StageStyle.UNDECORATED);

        return dialog;
    }

    
    private void mostraEccezioneDialog(String titolo, String corpo) {
        Alert dialogEccezione = creaDialog(titolo,corpo);

        ButtonType buttonOk = new ButtonType("Ok");
        dialogEccezione.getButtonTypes().setAll(buttonOk);
        Optional<ButtonType> result = dialogEccezione.showAndWait();
    }

    public ResultSet caricaFoto(Connection conn, String idStruttura) throws SQLException {
        String sql = "SELECT URL FROM FOTO WHERE STRUTTURAID=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, Integer.parseInt(idStruttura));
        return preparedStatement.executeQuery();
    }

    public void aggiornaFoto(Connection conn, String nomeFile, String idStruttura) throws SQLException {
        String sql = "UPDATE FOTO SET URL=? WHERE STRUTTURAID=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, prefix + nomeFile);
        preparedStatement.setInt(2, Integer.parseInt(idStruttura));
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
 
    public void eliminaFoto(Connection conn, String url) throws SQLException {
        String sql = "DELETE FROM FOTO WHERE URL=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, url);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

}
