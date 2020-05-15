package presenter;

import org.controlsfx.control.Rating;
import model.RecensioneModel;
import DAO.RecensioneDAO;
import com.jfoenix.controls.JFXListView;
import consigliaviaggi.RecensioneCell;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Giusi
 */
public class RecensioniPresenter implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private MenuBar menuBar;

    @FXML
    private JFXListView listSearch;

    @FXML
    private Label message;

    @FXML
    private Label noRecensioniPendenti;

    @FXML
    private Button arrowButton;

    @FXML
    private Button expandButton;
    
    @FXML
    private Rating ratingButton;

    private int reviews = 0;

    ArrayList<RecensioneModel> list = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            RecensioneDAO recensioneDao = new RecensioneDAO();
            ResultSet rs = recensioneDao.visualizzaListaRecensioni();
            if (!rs.isBeforeFirst()) {
                noRecensioniPendenti.setText("Non ci sono recensioni pendenti");
                listSearch.setVisible(false);
            } else {
                while (rs.next()) {
                    list.add(new RecensioneModel(
                            rs.getString("usernameutente"),
                            rs.getString("nome"),
                            rs.getString("titolo"),
                            rs.getString("rating"),
                            rs.getString("corpo"),
                            rs.getString("id")
                    ));
                    reviews++;
                }
               /* ObservableList<RecensioneModel> obl = FXCollections.observableArrayList(list);
                listSearch.setItems(obl);
                listSearch.setCellFactory(recensioneCell -> new RecensioneCell());
                message.setText(reviews + " recensioni pendenti");
               */
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecensioniPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void clickBack(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Indietro");
        Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        Scene gestisciStruttureView = new Scene(gestisciStrutture);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciStruttureView);
        window.show();
    }

    @FXML
    private void logout(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Esci");
        Parent menuPrincipale = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
        Scene menuPrincipaleView = new Scene(menuPrincipale);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(menuPrincipaleView);
        window.show();
    }

    @FXML
    private void visualizzaInformazioni(final ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Informazioni");
        Parent informazioni = FXMLLoader.load(getClass().getResource("/view/informazioni.fxml"));
        Scene informazioniView = new Scene(informazioni);

        Stage window = new Stage();
        window.setScene(informazioniView);
        window.setTitle("Informazioni");
        window.show();
    }

    public void visualizzaListaRecensioni() throws IOException {
        System.out.println("Torno alla lista");
        Parent gestisciRecensioni = FXMLLoader.load(getClass().getResource("/view/listaRecensioni.fxml"));
        Scene gestisciRecensioniView = new Scene(gestisciRecensioni);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciRecensioniView);
        window.show();
    }
}
