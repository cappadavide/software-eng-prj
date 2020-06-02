package presenter;

import DAO.DBManager;
import DAO.FotoDAO;
import DAO.S3API;
import DAO.StrutturaDAO;
import com.jfoenix.controls.JFXListView;
import interfaces.RicercaIndirizzo;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.FotoModel;
import model.StrutturaModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Connection;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import model.CoordinateModel;
import org.controlsfx.control.Notifications;
import org.postgresql.util.PSQLException;
import interfaces.MenuUtils;

public class AggiungiStrutturaPresenter implements Initializable, RicercaIndirizzo, MenuUtils {

    @FXML
    private MenuBar menuBar;

    @FXML
    private ComboBox categoriaComboBox, sottocategoriaComboBox;

    @FXML
    private TextField nomeField, indirizzoField, prezzoDa, prezzoA;

    @FXML
    private TextArea informazioniField;

    @FXML
    private Text campiObbligatoriMessage, caricamentoMessage;

    @FXML
    private JFXListView listaSuggerimenti, listaFoto;

    private String nomeStruttura, categoriaStruttura, sottocategoriaStruttura, informazioniStruttura, indirizzoStruttura, prezzoDaStruttura, prezzoAStruttura;

    private StrutturaDAO strutturaDAO;

    private ArrayList<FotoModel> listaFotoAggiunte = new ArrayList<>();

    private ArrayList<String> suggerimenti = new ArrayList<>();

    private ArrayList<CoordinateModel> listaCoordinate = new ArrayList<>();

    private StrutturaModel struttura;
    private S3API s3API;

    private double latitudine, longitudine;

    ObservableList<String> tipoCategoria = FXCollections.observableArrayList("", "alberghi", "ristoranti", "attrazioni");
    ObservableList<String> tipoSottocategoriaAlberghi = FXCollections.observableArrayList("", "hotel", "bb", "appartamento", "altro");
    ObservableList<String> tipoSottocategoriaRistoranti = FXCollections.observableArrayList("", "pizzeria", "fastfood", "ristorante", "altro");
    ObservableList<String> tipoSottocategoriaAttrazioni = FXCollections.observableArrayList("", "museo", "zoo", "parco giochi", "altro");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.strutturaDAO = new StrutturaDAO();
        s3API = new S3API();
        sottocategoriaComboBox.setDisable(true);
        categoriaComboBox.setValue("");
        sottocategoriaComboBox.setValue("");
        nomeField.setText("");
        indirizzoField.setText("");
        prezzoDa.setText("");
        prezzoA.setText("");
        informazioniField.setText("");
        categoriaComboBox.setItems(tipoCategoria);
        listaSuggerimenti.setVisible(false);
        caricamentoMessage.setVisible(false);
        setIndirizzoField();
    }

    private void setIndirizzoField() {
        indirizzoField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue != null && newValue.length() > 2 && indirizzoField.isFocused()) {
                try {
                    suggerimenti.clear();
                    listaCoordinate.clear();
                    strutturaDAO.search(newValue, this);
                } catch (IOException ex) {
                    mostraEccezioneDialog("Errore", ex.getMessage());
                } catch (InterruptedException | ExecutionException ex) {
                    mostraEccezioneDialog("Errore", ex.getMessage());
                }
            }
        });
    }

    @Override
    public void rispostaRicevuta(String result) {
        final int height = 24;
        Platform.runLater(() -> {
            JSONObject response = new JSONObject(result);
            JSONArray features = response.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                suggerimenti.add(feature.get("place_name").toString());
                listaCoordinate.add(prelevaCoordinate(feature));
            }
            ObservableList<String> obl = FXCollections.observableArrayList(suggerimenti);
            listaSuggerimenti.setItems(obl);
            listaSuggerimenti.setPrefHeight(suggerimenti.size() * height + 2);
            listaSuggerimenti.setVisible(true);
        });
    }

    private CoordinateModel prelevaCoordinate(JSONObject feature) {
        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coords = geometry.getJSONArray("coordinates");
        return new CoordinateModel(coords.getDouble(1), coords.getDouble(0));
    }

    @FXML
    private void scegliIndirizzo(MouseEvent event) {
        int indice = listaSuggerimenti.getSelectionModel().getSelectedIndex();
        indirizzoField.setText(listaSuggerimenti.getSelectionModel().getSelectedItem().toString());
        latitudine = listaCoordinate.get(indice).getLatitudine();
        longitudine = listaCoordinate.get(indice).getLongitudine();
        listaSuggerimenti.setVisible(false);
    }

    public void scegliCategoria(ActionEvent e) {
        String scelta = categoriaComboBox.getSelectionModel().getSelectedItem().toString();
        if (scelta.equals("alberghi") || scelta.equals("ristoranti") || scelta.equals("attrazioni")) {
            sottocategoriaComboBox.setDisable(false);
            if (scelta.equals("alberghi")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAlberghi);

            } else if (scelta.equals("ristoranti")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaRistoranti);
            } else if (scelta.equals("attrazioni")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAttrazioni);
            }
        }
    }

    @FXML
    private void clickInserisci(ActionEvent event) {
        Alert inserisciStrutturaDialog = creaDialog("Aggiungi struttura", "Sei sicuro di voler aggiungere tale struttura?");
        ButtonType buttonSi = new ButtonType("Si");
        ButtonType buttonNo = new ButtonType("No");
        inserisciStrutturaDialog.getButtonTypes().setAll(buttonSi, buttonNo);
        Optional<ButtonType> result = inserisciStrutturaDialog.showAndWait();
        if (result.get() == buttonSi) {
            try {
                inserisciStruttura();
            } catch (InterruptedException ex) {
                mostraEccezioneDialog("Errore", ex.getMessage());
            }
        }
    }

    public void inserisciStruttura() throws InterruptedException {
        propertyMessage(1);
        campiObbligatoriMessage.setVisible(false);
        caricamentoMessage.setVisible(false);
        String nomeFile, copertinaURL;
        nomeStruttura = nomeField.getText();
        categoriaStruttura = categoriaComboBox.getSelectionModel().getSelectedItem().toString();
        if (sottocategoriaComboBox.isDisable()) {
            sottocategoriaStruttura = "";
        }
        sottocategoriaStruttura = sottocategoriaComboBox.getSelectionModel().getSelectedItem().toString();
        indirizzoStruttura = indirizzoField.getText();
        informazioniStruttura = informazioniField.getText();
        prezzoDaStruttura = prezzoDa.getText();
        prezzoAStruttura = prezzoA.getText();
        if (controlloParametri()) {
            propertyMessage(0); 
        } else {
            copertinaURL = listaFotoAggiunte.get(0).getNomeFile();
            try {
                Connection conn = DBManager.getConnection(true);
                conn.createStatement().execute("SELECT setval('struttura_id_seq', MAX(struttura.id)) FROM STRUTTURA");
                conn.setAutoCommit(false);
                conn.createStatement().execute("SET CONSTRAINTS foto_strutturaid_fkey DEFERRED");
                strutturaDAO.inserisciStruttura(conn, nomeStruttura, categoriaStruttura, sottocategoriaStruttura, informazioniStruttura, indirizzoStruttura, latitudine, longitudine, prezzoDaStruttura, prezzoAStruttura, copertinaURL);
                ResultSet idStruttura = strutturaDAO.trovaIdStrutturaMassimo(conn);
                while (idStruttura.next()) {
                    int id = idStruttura.getInt("id");
                    aggiungiFoto(conn, listaFotoAggiunte, id);
                }
                conn.commit();
                conn.close();
                s3API.inserisciFoto(listaFotoAggiunte);
                ritornaAlMenu();
                } catch (NumberFormatException exception) {
                propertyMessage(2);
                mostraEccezioneDialog("Errore riempimento campi", "I campi 'Prezzo da' e 'Prezzo a' devono essere numeri.");
            } catch (SQLException ex) {
                propertyMessage(2);
                mostraEccezioneDialog("Errore", ex.getMessage());
            }
        }
    }

    public boolean controlloParametri() {
        return (nomeStruttura.equals("")
                || categoriaStruttura.equals("")
                || sottocategoriaStruttura.equals("")
                || informazioniStruttura.equals("")
                || indirizzoStruttura.equals("")
                || prezzoDaStruttura.equals("")
                || prezzoAStruttura.equals("")
                || listaFotoAggiunte.isEmpty());
    }

    public void propertyMessage(int flag){
        if (flag == 0) {
            campiObbligatoriMessage.setVisible(true);
            campiObbligatoriMessage.setText("Tutti i campi sono obbligatori");
            campiObbligatoriMessage.setFill(Color.FIREBRICK);
        } else if (flag == 1) {
            caricamentoMessage.setVisible(true);
        }
        else {
            campiObbligatoriMessage.setText("Errore. Si è verificato un problema.");
            campiObbligatoriMessage.setFill(Color.FIREBRICK);
        }
    }

    private boolean aggiungiFoto(Connection conn, ArrayList<FotoModel> listaFoto, int idStruttura) throws SQLException {
        Iterator<FotoModel> iteratore = listaFoto.iterator();
        FotoDAO fotoDAO = new FotoDAO();
        while (iteratore.hasNext()) {
            String nomeFile = iteratore.next().getNomeFile();
            fotoDAO.inserisciFoto(conn, nomeFile, idStruttura);
        }
        return true;
    }

    private Alert creaDialog(String titolo, String corpo) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setHeaderText(titolo);
        if (corpo.contains("struttura_longitudine_key") || corpo.contains("struttura_latitidune_key")) {
            dialog.setContentText("A questo indirizzo è stata già assegnata una struttura.");
        } else if (corpo.contains("foto_pkey")) {
            dialog.setContentText("Una o più foto sono state già utilizzate per un'altra struttura. Riprovare.");
        } else {
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
        Alert dialogEccezione = creaDialog(titolo, corpo);

        ButtonType buttonOk = new ButtonType("Ok");
        dialogEccezione.getButtonTypes().setAll(buttonOk);
        Optional<ButtonType> result = dialogEccezione.showAndWait();
    }

    private void ritornaAlMenu(){
        try {
            Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/gestisciStrutture.fxml"));
            Scene gestisciStruttureView = new Scene(gestisciStrutture);
            gestisciStruttureView.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/style.css").toExternalForm());
            
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(gestisciStruttureView);
            window.show();
            Notifications notifica = Notifications.create()
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.BOTTOM_LEFT);
            notifica.title("");
            notifica.text("La struttura stata aggiunta.");
            notifica.show();
        } catch (IOException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
    }

    @FXML
    private void tornaAllaGestioneDelleStrutture(ActionEvent event){
        try {
            Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/gestisciStrutture.fxml"));
            Scene gestisciStruttureView = new Scene(gestisciStrutture);
            
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(gestisciStruttureView);
            window.show();
        } catch (IOException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
    }

    @Override
    @FXML
    public void logout(ActionEvent event) {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene loginView = new Scene(login);
            
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(loginView);
            window.show();
        } catch (IOException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
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
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
    }

    @FXML
    private void aggiungiFotoAllaStruttura(final ActionEvent event){
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser aggiungiFoto = new FileChooser();
        aggiungiFoto.getExtensionFilters().add(imageFilter);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        aggiungiFoto.setTitle("Esplora risorse");
        File pathFile = aggiungiFoto.showOpenDialog(stage);
        if (pathFile != null) {
            listaFotoAggiunte.add(new FotoModel(pathFile.getName(), "file:"+pathFile.toString()));
            ObservableList<FotoModel> obl = FXCollections.observableArrayList(listaFotoAggiunte);
            listaFoto.setItems(obl);
            listaFoto.setCellFactory(foto -> new FotoCell());
            listaFoto.setVisible(true);
        }

    }

    @FXML
    private void deletePhoto(MouseEvent e) {
        FotoModel indice = (FotoModel) listaFoto.getSelectionModel().getSelectedItem();
        listaFotoAggiunte.remove(indice);
        ObservableList<FotoModel> obl = FXCollections.observableArrayList(listaFotoAggiunte);
        listaFoto.setItems(obl);
    }

}
