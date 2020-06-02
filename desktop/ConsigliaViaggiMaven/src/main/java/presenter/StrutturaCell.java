package presenter;

import DAO.DBManager;
import DAO.StrutturaDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import model.StrutturaModel;
import org.controlsfx.control.Rating;

public class StrutturaCell extends ListCell<StrutturaModel> {

    @FXML
    private FXMLLoader mLLoader;

    @FXML
    private GridPane gridpane;

    @FXML
    private Label nomeField;

    @FXML
    private Label rangePrezzo;

    @FXML
    private Rating ratingButton;

    @FXML
    private Label sottocategoriaField;

    @FXML
    private Label indirizzoField;

    @FXML
    private Text informazioniField;

    @FXML
    private ImageView anteprima;

    @Override
    protected void updateItem(StrutturaModel fs, boolean empty) {
        super.updateItem(fs, empty);
        if (empty || fs == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/view/risultatiStrutture.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException ex) {
                    mostraEccezioneDialog("Errore", ex.getMessage());
                }
            }
            nomeField.setText(fs.getNome());
            ratingButton.setRating(Double.parseDouble(fs.getRating()));
            ratingButton.setDisable(true);
            sottocategoriaField.setText(fs.getSottocategoria());
            indirizzoField.setText(fs.getIndirizzo());
            informazioniField.setText(fs.getInformazioni());
            rangePrezzo.setText("€" + Double.parseDouble(fs.getPrezzoDa()) + "-" + "€" + Double.parseDouble(fs.getPrezzoA()));
            try {
                caricaFotoAnteprima(fs.getID());
            } catch (SQLException | IOException ex) {
                mostraEccezioneDialog("Errore", ex.getMessage());
            }
            setText(null);
            setGraphic(gridpane);
        }
    }

    public void caricaFotoAnteprima(String identificativoStruttura) throws SQLException, IOException {
        Connection conn = DBManager.getConnection(true);
        StrutturaDAO strutturadao = new StrutturaDAO();
        ResultSet rs = strutturadao.recuperaFotoAnteprima(conn,identificativoStruttura);
        rs.next();
        anteprima.setImage(new Image(rs.getString("copertinaurl"),500,500,false,false));
        conn.close();
    }

    private Alert creaDialog(String titolo, String corpo) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setHeaderText(titolo);
        dialog.setContentText(corpo);
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        dialog.setX((bounds.getMaxX() / 2) - 150);
        dialog.setY((bounds.getMaxY() / 2) - 100);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/alertStruttura.css").toExternalForm());
        dialog.initStyle(StageStyle.UNDECORATED);

        return dialog;
    }

    private void mostraEccezioneDialog(String titolo, String corpo) {
        Alert dialogEccezione = creaDialog(titolo, corpo);

        ButtonType buttonOk = new ButtonType("Ok");
        dialogEccezione.getButtonTypes().setAll(buttonOk);
        Optional<ButtonType> result = dialogEccezione.showAndWait();
    }

}
