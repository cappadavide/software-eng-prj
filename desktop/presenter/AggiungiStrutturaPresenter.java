package presenter;

import DAO.StrutturaDAO;
import com.jfoenix.controls.JFXListView;
import consigliaviaggi.RecensioneCell;
import consigliaviaggi.StrutturaCell;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.RecensioneModel;
import model.StrutturaModel;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * pk.eyJ1IjoiY2FwcGFkYXZpZGUiLCJhIjoiY2s4dnN3Z2tuMGp1ejNvczc1OWpoNGxidSJ9.Xfan0iqgJviP24q-ThnTUA
 *
 * @author davso
 */
public class AggiungiStrutturaPresenter implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private ComboBox categoriaComboBox;

    @FXML
    private ComboBox sottocategoriaComboBox;

    @FXML
    private TextField nomeField;

    @FXML
    private TextArea informazioniField;

    @FXML
    private TextField ratingField;

    @FXML
    private TextField indirizzoField;

    @FXML
    private TextField prezzoDa;

    @FXML
    private TextField prezzoA;

    @FXML
    private Text campiObbligatoriMessage;

    @FXML
    private JFXListView listSearch;

    private StrutturaDAO strutturaDAO;

    ObservableList<String> tipoCategoria = FXCollections.observableArrayList("Alberghi", "Ristoranti", "Attrazioni");
    ObservableList<String> tipoSottocategoriaAlberghi = FXCollections.observableArrayList("Hotel", "B&B", "Appartamento", "Altro");
    ObservableList<String> tipoSottocategoriaRistoranti = FXCollections.observableArrayList("Pizzeria", "FastFood", "Ristorante", "Altro");
    ObservableList<String> tipoSottocategoriaAttrazioni = FXCollections.observableArrayList("Museo", "Zoo", "Parco giochi", "Altro");

    ArrayList<String> lista = new ArrayList<>();

    private StrutturaModel struttura;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sottocategoriaComboBox.setDisable(true);
        categoriaComboBox.setItems(tipoCategoria);
        listSearch.setVisible(false);
        setIndirizzoField();
    }

    public void setIndirizzoField() {
        indirizzoField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            System.out.println("Text Changed to  " + newValue + "\n");
            if (newValue != null && newValue.length() > 2) {
                try {
                    listSearch.getItems().clear();
                    lista.clear();
                    this.strutturaDAO = new StrutturaDAO();

                    //search void per chiamate asincrone
                    strutturaDAO.search(newValue);
                    //in DAO mettere un riferimento a questo presenter per restituire il JSONObject quando è pronto

                } catch (IOException ex) {
                    Logger.getLogger(AggiungiStrutturaPresenter.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Impossibile ricercare termine di ricerca");
                } catch (InterruptedException ex) {
                    Logger.getLogger(AggiungiStrutturaPresenter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(AggiungiStrutturaPresenter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void rispostaRicevuta(String result) {
        JSONObject response = new JSONObject (result);
        //fare nuovo metodo per visualizzare la lista
        JSONArray list = response.getJSONArray("features");
        System.out.println(response.toString() + "\n");
        for (int i = 0; i < list.length(); i++) {
            System.out.println(list.getJSONObject(i).get("place_name"));
            lista.add(list.getJSONObject(i).get("place_name").toString());
        }
        ObservableList<String> obl = FXCollections.observableArrayList(lista);
        listSearch.setItems(obl);
        //   listSearch.setCellFactory(strutturaCell -> new StrutturaCell());
        listSearch.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                indirizzoField.setText(newValue);
                //     listSearch.setVisible(false);
            }
        });
        listSearch.setVisible(true);
    }

    public void sceltaIndirizzo(StrutturaModel stringa) {
        this.struttura = stringa;
        indirizzoField.setText(struttura.getIndirizzo());
    }

    public void scegliCategoria(ActionEvent e) {
        String scelta = categoriaComboBox.getSelectionModel().getSelectedItem().toString();
        System.out.println(scelta);
        if (scelta.equals("Alberghi") || scelta.equals("Ristoranti") || scelta.equals("Attrazioni")) {
            sottocategoriaComboBox.setDisable(false);
            if (scelta.equals("Alberghi")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAlberghi);
            } else if (scelta.equals("Ristoranti")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaRistoranti);
            } else {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAttrazioni);
            }
        }
    }

    @FXML
    private void clickInserisci(ActionEvent event) {
        System.out.println("Hai cliccato Inserisci");
        try {
            if ((categoriaComboBox.getSelectionModel().getSelectedItem().toString() == null
                    || sottocategoriaComboBox.getSelectionModel().getSelectedItem().toString() == null
                    || nomeField.getText() == null
                    || informazioniField.getText() == null
                    || indirizzoField.getText() == null
                    || ratingField.getText() == null
                    || prezzoDa.getText() == null
                    || prezzoA.getText() == null)
                    || nomeField.getText().trim().isEmpty()
                    || informazioniField.getText().trim().isEmpty()
                    || ratingField.getText().trim().isEmpty()
                    || prezzoDa.getText().trim().isEmpty()
                    || prezzoA.getText().trim().isEmpty()) {
                campiObbligatoriMessage.setText("Tutti i campi sono obbligatori");
                campiObbligatoriMessage.setFill(Color.FIREBRICK);
            } else {
                campiObbligatoriMessage.setText(" ");
                System.out.println("Inserito");
            }
        } catch (NullPointerException e) {
            campiObbligatoriMessage.setText("Tutti i campi sono obbligatori");
            campiObbligatoriMessage.setFill(Color.FIREBRICK);
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
}

//   TextFields.bindAutoCompletion(indirizzoField, lista);
//   ObservableList<String> obl = FXCollections.observableArrayList(lista);
//   listSearch.setItems(obl);
//   listSearch.setVisible(true);
