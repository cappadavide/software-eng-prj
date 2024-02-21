package presenter;

import DAO.DBManager;
import DAO.RecensioneDAO;
import model.RecensioneModel;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.Rating;
import interfaces.MenuUtils;
import java.sql.Connection;

public class VisualizzaRecensionePresenter implements Initializable, MenuUtils {

    private RecensioneModel recensioneDaVisualizzare;
    private RecensioneDAO recensioneDAO;

    @FXML
    private Label labelUsernameUtente, labelStrutturaId, labelTitolo, labelID;

    @FXML
    private Rating ratingField;

    @FXML
    private Text labelCorpo;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button arrowButton;

    @FXML
    private FXMLLoader mLLoader;

    @FXML
    private MenuBar menuBar;
    
    private ResultSet idStruttura;

    public void initData(RecensioneModel recensione) throws IOException {
        this.recensioneDaVisualizzare = recensione;
        recensioneDAO = new RecensioneDAO();
        labelUsernameUtente.setText(recensione.getUsernameUtente());
        labelStrutturaId.setText(recensione.getStrutturaId());
        labelTitolo.setText(recensione.getTitolo());
        ratingField.setRating(Integer.parseInt(recensione.getRating()));
        labelCorpo.setText(recensione.getCorpo());
        labelID.setText(recensione.getID());
        labelID.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    @FXML
    public void logout(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene loginView = new Scene(login);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(loginView);
        window.show();
    }

    @Override
    @FXML
    public void visualizzaInformazioni(final ActionEvent event){
        try {
            Parent informazioni = FXMLLoader.load(getClass().getResource("/view/informazioni.fxml"));
            Scene informazioniView = new Scene(informazioni);
            
            Stage window = new Stage();
            window.setScene(informazioniView);
            window.setTitle("Informazioni");
            window.show();
        } catch (IOException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
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

    @FXML
    private void tornaAllaListaDelleRecensioni(ActionEvent event) throws IOException {
        Parent listaRecensioni = FXMLLoader.load(getClass().getResource("/view/listaRecensioni.fxml"));
        Scene listaRecensioniView = new Scene(listaRecensioni);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(listaRecensioniView);
        window.show();
    }

    @FXML
    private void mostraApprovaDialog(ActionEvent event) throws IOException, SQLException {
        Alert approvaDialog = creaDialog("Valutazione recensione","Vuoi confermare l'operazione?");
        Connection conn = DBManager.getConnection(true);
        ButtonType buttonSi = new ButtonType("Si");
        ButtonType buttonNo = new ButtonType("No");
        approvaDialog.getButtonTypes().setAll(buttonSi, buttonNo);
        Optional<ButtonType> result = approvaDialog.showAndWait();
        if (result.get() == buttonSi) {
            recensioneDAO.approvaRecensione(conn,labelID.getText());
            idStruttura = recensioneDAO.ricavaIdDellaStruttura(conn,recensioneDaVisualizzare.getStrutturaId());
            while (idStruttura.next()) {
                    recensioneDAO.aggiornaRatingStruttura(conn,idStruttura.getString("id"));
                }  
            visualizzaListaRecensioni();
        }
        conn.close();
    }

    @FXML
    private void mostraDeclinaDialog(ActionEvent event) throws IOException, SQLException {
        Alert declinaDialog = creaDialog("Valutazione recensione","Vuoi confermare l'operazione?");
        Connection conn = DBManager.getConnection(true);
        ButtonType buttonSi = new ButtonType("Si");
        ButtonType buttonNo = new ButtonType("No");
        declinaDialog.getButtonTypes().setAll(buttonSi, buttonNo);
        Optional<ButtonType> result = declinaDialog.showAndWait();

        if (result.get() == buttonSi) {
            recensioneDAO.declinaRecensione(conn, labelID.getText());
            visualizzaListaRecensioni();
        }
    }

    private void visualizzaListaRecensioni() throws IOException {
        Parent listaRecensioni = FXMLLoader.load(getClass().getResource("/view/listaRecensioni.fxml"));
        Scene listaRecensioniView = new Scene(listaRecensioni);
        listaRecensioniView.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/style.css").toExternalForm());

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(listaRecensioniView);
        window.show();
        Notifications notifica = Notifications.create()
                .hideAfter(Duration.seconds(3))
                .position(Pos.BOTTOM_LEFT);
        notifica.title("");
        notifica.text("La lista è stata aggiornata");
        notifica.show();
    }

}
