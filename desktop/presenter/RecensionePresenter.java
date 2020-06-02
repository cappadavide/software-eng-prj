package presenter;

import DAO.DBManager;
import org.controlsfx.control.Rating;
import model.RecensioneModel;
import DAO.RecensioneDAO;
import com.jfoenix.controls.JFXListView;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import interfaces.MenuUtils;

public class RecensionePresenter implements Initializable, MenuUtils {

    @FXML
    private Button backButton, expandButton;

    @FXML
    private MenuBar menuBar;

    @FXML
    private JFXListView listaRecensioni;

    @FXML
    private Label message, noRecensioniPendenti;

    @FXML
    private Rating ratingButton;

    private int reviews = 0;

    private RecensioneDAO recensioneDAO;

    ArrayList<RecensioneModel> recensioniPendenti = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection conn = DBManager.getConnection(true);
            this.recensioneDAO = new RecensioneDAO();
            ResultSet rs = recensioneDAO.visualizzaListaRecensioni(conn);
            if (!rs.isBeforeFirst()) {
                noRecensioniPendenti.setText("Non ci sono recensioni pendenti");
                listaRecensioni.setVisible(false);
            } else {
                while (rs.next()) {
                    recensioniPendenti.add(new RecensioneModel(
                            rs.getString("usernameutente"),
                            rs.getString("nome"),
                            rs.getString("titolo"),
                            rs.getString("rating"),
                            rs.getString("corpo"),
                            rs.getString("id")
                    ));
                    reviews++;
                }
                ObservableList<RecensioneModel> obl = FXCollections.observableArrayList(recensioniPendenti);
                listaRecensioni.setItems(obl);
                listaRecensioni.setCellFactory(recensioneCell -> new RecensioneCell());
                message.setText(reviews + " recensioni pendenti");
                expandList();
               
            }
            conn.close();
        } catch (SQLException ex) {
            mostraEccezione("Errore", ex.getMessage());
        }
    }

   public void mostraEccezione(String titolo, String messaggio) {
        Alert dialogEccezione = creaDialog(titolo, messaggio);
        ButtonType buttonOk = new ButtonType("Ok");
        dialogEccezione.getButtonTypes().setAll(buttonOk);
        Optional<ButtonType> result = dialogEccezione.showAndWait();
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

    public void expandList() {
        expandButton.setOnAction((ActionEvent e) -> {
            expandButton.setVisible(false);
        });
    }

    @FXML
    private void tornaAlMenu(ActionEvent event) throws IOException {
        Parent menuPrincipale = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        Scene menuPrincipaleView = new Scene(menuPrincipale);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(menuPrincipaleView);
        window.show();
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
    public void visualizzaInformazioni(final ActionEvent event) {
        try {
            Parent informazioni = FXMLLoader.load(getClass().getResource("/view/informazioni.fxml"));
            Scene informazioniView = new Scene(informazioni);
            
            Stage window = new Stage();
            window.setScene(informazioniView);
            window.setTitle("Informazioni");
            window.show();
        } catch (IOException ex) {
            mostraEccezione("Errore", ex.getMessage());
        }
    }
    
    private void mostraEccezioneDialog(String titolo, String corpo) {
        Alert dialogEccezione = creaDialog(titolo, corpo);

        ButtonType buttonOk = new ButtonType("Ok");
        dialogEccezione.getButtonTypes().setAll(buttonOk);
        Optional<ButtonType> result = dialogEccezione.showAndWait();
    }

    public void visualizzaListaRecensioni() throws IOException {
        Parent listaDelleRecensioni = FXMLLoader.load(getClass().getResource("/view/listaRecensioni.fxml"));
        Scene listaRecensioniView = new Scene(listaDelleRecensioni);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(listaRecensioniView);
        window.show();
    }
}
