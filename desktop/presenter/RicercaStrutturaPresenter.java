package presenter;

import DAO.RecensioneDAO;
import DAO.StrutturaDAO;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import consigliaviaggi.RecensioneCell;
import consigliaviaggi.StrutturaCell;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.RecensioneModel;
import model.StrutturaModel;
import org.controlsfx.control.Rating;

public class RicercaStrutturaPresenter implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label message;

    @FXML
    private JFXListView listSearch;

    @FXML
    private ComboBox categoriaComboBox = null;

    @FXML
    private TextField nomeField;

    @FXML
    private ComboBox sottocategoriaComboBox;

    @FXML
    private TextField indirizzoField;

    @FXML
    private Rating ratingButton;

    @FXML
    private JFXCheckBox orderByName;

    @FXML
    private JFXCheckBox orderByCategoria;

    @FXML
    private JFXCheckBox orderBySottocategoria;

    @FXML
    private JFXCheckBox orderByIndirizzo;

    private String nomeStruttura = "";

    private String sottocategoriaStruttura = "";

    private String categoriaStruttura = "";

    private String indirizzoStruttura = "";

    private double ratingStruttura = 0.0;

    private int countStrutture = 0;

    private String ordinaPerNome = "";

    private String ordinaPerCategoria = "";

    private String ordinaPerSottocategoria = "";

    private String ordinaPerIndirizzo = "";

    ArrayList<StrutturaModel> list = new ArrayList<>();

    ObservableList<String> tipoCategoria = FXCollections.observableArrayList("alberghi", "ristoranti", "attrazioni");
    ObservableList<String> tipoSottocategoriaAlberghi = FXCollections.observableArrayList("hotel", "b&b", "appartamento", "altro");
    ObservableList<String> tipoSottocategoriaRistoranti = FXCollections.observableArrayList("pizzeria", "fastfood", "ristorante", "altro");
    ObservableList<String> tipoSottocategoriaAttrazioni = FXCollections.observableArrayList("museo", "zoo", "Parco giochi", "altro");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sottocategoriaComboBox.setDisable(true);
        categoriaComboBox.setItems(tipoCategoria);
        orderByName.setSelected(false);
        orderBySottocategoria.setSelected(false);
        orderByIndirizzo.setSelected(false);
        orderByCategoria.setSelected(false);
        listSearch.setVisible(false);
    }

    @FXML
    private void clickCerca(final ActionEvent event) throws IOException {
        try {
            nomeStruttura = nomeField.getText();
            categoriaStruttura = categoriaComboBox.getSelectionModel().getSelectedItem().toString();

            if (sottocategoriaComboBox.getSelectionModel() != null || !sottocategoriaComboBox.isDisabled()) {
                sottocategoriaStruttura = sottocategoriaComboBox.getSelectionModel().getSelectedItem().toString();
            }

            if (orderByName.isSelected()) {
                ordinaPerNome = "nome";
            }
            
            if (orderByCategoria.isSelected()) {
                ordinaPerCategoria = "categoria";
            }
            
            if (orderBySottocategoria.isSelected()) {
                ordinaPerSottocategoria = "sottocategoria";
            }
            
            if (orderByIndirizzo.isSelected()) {
                ordinaPerIndirizzo = "Indirizzo";
            }

            indirizzoStruttura = indirizzoField.getText();
            ratingStruttura = ratingButton.getRating();
            // System.out.println(nomeStruttura + " " + sottocategoriaStruttura + " "+ indirizzoStruttura + " "+ ratingStruttura);
            StrutturaDAO model = new StrutturaDAO();
            ResultSet rs = model.visualizzaListaStrutture(nomeStruttura, categoriaStruttura, sottocategoriaStruttura, indirizzoStruttura, ratingStruttura, ordinaPerNome, ordinaPerCategoria, ordinaPerSottocategoria, ordinaPerIndirizzo);
            if (!rs.isBeforeFirst()) {
                message.setText("Nessuna struttura individuata");
                listSearch.setVisible(false);
            } else {
                while (rs.next()) {
                    list.add(new StrutturaModel(
                            rs.getString("nome"),
                            rs.getString("sottocategoria"),
                            rs.getString("indirizzo"),
                            rs.getString("rating"),
                            rs.getString("informazioni")
                    ));
                    countStrutture++;
                }
                ObservableList<StrutturaModel> obl = FXCollections.observableArrayList(list);
                listSearch.setItems(obl);
                listSearch.setCellFactory(strutturaCell -> new StrutturaCell());
                message.setText("Sono state trovate " + countStrutture + " strutture secondo i termini di ricerca.");
                //    expandList();
                listSearch.setVisible(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecensioniPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void scegliCategoria(ActionEvent e) {
        String scelta = categoriaComboBox.getSelectionModel().getSelectedItem().toString();
        System.out.println(scelta);
        if (scelta.equals("alberghi") || scelta.equals("ristoranti") || scelta.equals("attrazioni")) {
            sottocategoriaComboBox.setDisable(false);
            if (scelta.equals("alberghi")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAlberghi);
            } else if (scelta.equals("ristoranti")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaRistoranti);
            } else {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAttrazioni);
            }
        }
    }

    @FXML
    private void clickBack(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Indietro");
        Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/gestisciStrutture.fxml"));
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
}
