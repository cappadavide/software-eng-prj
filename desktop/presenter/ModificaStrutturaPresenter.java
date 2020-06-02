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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
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
import javafx.util.Duration;
import model.CoordinateModel;
import model.FotoModel;
import model.StrutturaModel;
import org.controlsfx.control.Notifications;
import org.json.JSONArray;
import org.json.JSONObject;
import interfaces.MenuUtils;

public class ModificaStrutturaPresenter implements Initializable, RicercaIndirizzo, MenuUtils {

    @FXML
    private MenuBar menuBar;

    @FXML
    private ComboBox categoriaComboBox, sottocategoriaComboBox;

    @FXML
    private TextField nomeField, indirizzoField, prezzoDa, prezzoA;

    @FXML
    private TextArea informazioniField;

    @FXML
    private Text campiObbligatoriMessage;

    @FXML
    private Label idStruttura;

    @FXML
    private JFXListView listaSuggerimenti, listaFotoDellaStruttura;

    private String nomeStruttura, categoriaStruttura, sottocategoriaStruttura, informazioniStruttura;

    private String indirizzoStruttura;

    private String prezzoDaStruttura;

    private String prezzoAStruttura;

    private String copertinaURL;

    private StrutturaDAO strutturaDAO;

    ObservableList<String> tipoCategoria = FXCollections.observableArrayList("alberghi", "ristoranti", "attrazioni");
    ObservableList<String> tipoSottocategoriaAlberghi = FXCollections.observableArrayList("hotel", "bb", "apartamento", "altro");
    ObservableList<String> tipoSottocategoriaRistoranti = FXCollections.observableArrayList("pizzeria", "fastfood", "ristorante", "altro");
    ObservableList<String> tipoSottocategoriaAttrazioni = FXCollections.observableArrayList("museo", "zoo", "parco giochi", "altro");

    ArrayList<String> lista = new ArrayList<>();
    ArrayList<FotoModel> listaFotoAggiunte = new ArrayList<>();
    ArrayList<FotoModel> listaFotoDaEliminare = new ArrayList<>();
    ArrayList<FotoModel> listaFotoDaAggiungere = new ArrayList<>();

    private StrutturaModel strutturaDaModificare;
    private FotoDAO fotoDAO;
    private int numeroDelleFotoDellaStruttura = 0;
    private int numeroDelleFotoAggiunte = 0;

    private double latitudine, longitudine;
    private ArrayList<String> suggerimenti = new ArrayList<>();

    private ArrayList<CoordinateModel> listaCoordinate = new ArrayList<>();
    private RicercaStrutturaPresenter riferimentoARicercaStrutturaPresenter;
    private VisualizzaStrutturaPresenter riferimentoAVisualizzaStrutturaPresenter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO
    }

    public void initData(StrutturaModel struttura, RicercaStrutturaPresenter ricercaPresenter, VisualizzaStrutturaPresenter visualizzaPresenter) throws SQLException {
        strutturaDaModificare = struttura;
        this.strutturaDAO = new StrutturaDAO();
        riferimentoARicercaStrutturaPresenter = ricercaPresenter;
        riferimentoAVisualizzaStrutturaPresenter = visualizzaPresenter;
        nomeField.setText(strutturaDaModificare.getNome());
        sottocategoriaComboBox.setValue(strutturaDaModificare.getSottocategoria());
        categoriaComboBox.setValue(strutturaDaModificare.getCategoria());
        indirizzoField.setText(strutturaDaModificare.getIndirizzo());
        informazioniField.setText(strutturaDaModificare.getInformazioni());
        prezzoDa.setText(strutturaDaModificare.getPrezzoDa());
        prezzoA.setText(strutturaDaModificare.getPrezzoA());
        idStruttura.setText(strutturaDaModificare.getID());
        recuperaFotoDellaStruttura(strutturaDaModificare.getID());
        categoriaComboBox.setItems(tipoCategoria);
        setSottocategoria(categoriaComboBox.getValue().toString());
        idStruttura.setVisible(false);
        listaSuggerimenti.setVisible(false);
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

    @FXML
    private void scegliIndirizzo(MouseEvent event) {
        int indice = listaSuggerimenti.getSelectionModel().getSelectedIndex();
        indirizzoField.setText(listaSuggerimenti.getSelectionModel().getSelectedItem().toString());
        latitudine = listaCoordinate.get(indice).getLatitudine();
        longitudine = listaCoordinate.get(indice).getLongitudine();
        listaSuggerimenti.setVisible(false);
    }

    private CoordinateModel prelevaCoordinate(JSONObject feature) {
        JSONObject geometry = feature.getJSONObject("geometry");
        JSONArray coords = geometry.getJSONArray("coordinates");
        return new CoordinateModel(coords.getDouble(1), coords.getDouble(0));
    }

    public void recuperaFotoDellaStruttura(String identificativoStruttura) throws SQLException {
            Connection conn = DBManager.getConnection(true);
            this.fotoDAO = new FotoDAO();
            ResultSet rs = fotoDAO.caricaFoto(conn,identificativoStruttura);
            while (rs.next()) {
                listaFotoAggiunte.add(new FotoModel(rs.getString("url").substring(51), rs.getString("url")));
                numeroDelleFotoDellaStruttura++;
            }
            ObservableList<FotoModel> obl = FXCollections.observableArrayList(listaFotoAggiunte);
            listaFotoDellaStruttura.setItems(obl);
            listaFotoDellaStruttura.setCellFactory(foto -> new FotoCell());
            listaFotoDellaStruttura.setVisible(true);
            conn.close();
    }

    public void scegliCategoria(ActionEvent e) {
        String scelta = categoriaComboBox.getSelectionModel().getSelectedItem().toString();
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

    public void setSottocategoria(String categoria) {
        if (categoria.equals("alberghi") || categoria.equals("ristoranti") || categoria.equals("attrazioni")) {
            sottocategoriaComboBox.setDisable(false);
            if (categoria.equals("alberghi")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAlberghi);
            } else if (categoria.equals("ristoranti")) {
                sottocategoriaComboBox.setItems(tipoSottocategoriaRistoranti);
            } else {
                sottocategoriaComboBox.setItems(tipoSottocategoriaAttrazioni);
            }
        }
    }

    @FXML
    public void aggiungiFotoAllaStruttura(final ActionEvent event)throws IOException{
        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        FileChooser aggiungiFoto = new FileChooser();
        aggiungiFoto.getExtensionFilters().add(imageFilter);
        File pathFile;
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        aggiungiFoto.setTitle("Esplora risorse");
        pathFile = aggiungiFoto.showOpenDialog(stage);
        if (pathFile != null) {
            listaFotoAggiunte.add(new FotoModel(pathFile.getName(), "file:"+pathFile.toString()));
            numeroDelleFotoAggiunte++;
            numeroDelleFotoDellaStruttura++;
            ObservableList<FotoModel> obl = FXCollections.observableArrayList(listaFotoAggiunte);
            listaFotoDellaStruttura.setItems(obl);
            listaFotoDellaStruttura.setCellFactory(foto -> new FotoCell());
            listaFotoDellaStruttura.setVisible(true);
            }
    }

    @FXML
    public void deletePhoto(MouseEvent e) {
        FotoModel indice = (FotoModel) listaFotoDellaStruttura.getSelectionModel().getSelectedItem();
        listaFotoDaEliminare.add(new FotoModel(indice.getNomeFile(),indice.getPathFile()));
        listaFotoAggiunte.remove(indice);
        if (numeroDelleFotoAggiunte != 0) {
            numeroDelleFotoAggiunte--;
        }
        numeroDelleFotoDellaStruttura--;
        ObservableList<FotoModel> obl = FXCollections.observableArrayList(listaFotoAggiunte);
        listaFotoDellaStruttura.setItems(obl);
    }

    @FXML
    private void clickModifica(ActionEvent event) {
        Alert modificaStrutturaDialog = creaDialog("Modifica struttura", "Sei sicuro di voler modificare tale struttura?");

        ButtonType buttonSi = new ButtonType("Si");
        ButtonType buttonNo = new ButtonType("No");
        modificaStrutturaDialog.getButtonTypes().setAll(buttonSi, buttonNo);
        Optional<ButtonType> result = modificaStrutturaDialog.showAndWait();
        if (result.get() == buttonSi) {
            modificaStruttura();
        }
    }
    
    public void modificaStruttura(){
        nomeStruttura = nomeField.getText();
        categoriaStruttura = categoriaComboBox.getSelectionModel().getSelectedItem().toString();
        sottocategoriaStruttura = sottocategoriaComboBox.getSelectionModel().getSelectedItem().toString();
        indirizzoStruttura = indirizzoField.getText();
        informazioniStruttura = informazioniField.getText();
        prezzoDaStruttura = prezzoDa.getText();
        prezzoAStruttura = prezzoA.getText();
        try {
            if (controlloParametri()) {
                propertyMessage(0);
            } else {
                Connection conn = DBManager.getConnection(true);
                propertyMessage(1);
                S3API s3API = new S3API();
                copertinaURL =listaFotoAggiunte.get(0).getNomeFile();
                conn.createStatement().execute("SELECT setval('struttura_id_seq', MAX(struttura.id)) FROM STRUTTURA;");
                conn.setAutoCommit(false);
                conn.createStatement().execute("SET CONSTRAINTS foto_strutturaid_fkey DEFERRED");
                strutturaDAO.modificaStruttura(conn,nomeStruttura, categoriaStruttura, sottocategoriaStruttura, informazioniStruttura, indirizzoStruttura, prezzoDaStruttura, prezzoAStruttura, idStruttura.getText(), copertinaURL, latitudine, longitudine);
                if (!listaFotoDaEliminare.isEmpty()) {
                    eliminaFotoSingole(conn,listaFotoDaEliminare);
                    s3API.eliminaFotoS3(listaFotoDaEliminare);
                }
                if (numeroDelleFotoAggiunte != 0) {
                    aggiungiFoto(conn,estraiLista(), Integer.parseInt(idStruttura.getText()));
                    s3API.inserisciFoto(estraiLista());
                }
                conn.commit();
                conn.close();
                aggiornaDatiStruttura();
                notificaModifica();
                
            }
            } catch (NumberFormatException exception) {
                mostraEccezioneDialog("Errore riempimento campi", "I campi 'Prezzo da' e 'Prezzo a' devono essere numeri.");
            } catch (SQLException ex) {
                mostraEccezioneDialog("Errore", ex.getMessage());
            }
    }
    
    public void propertyMessage(int flag){
        if(flag==0){
            campiObbligatoriMessage.setText("Tutti i campi sono obbligatori");
            campiObbligatoriMessage.setFill(Color.FIREBRICK);
        }
        else {
            campiObbligatoriMessage.setText("Caricamento in corso.. Attendere..");
            campiObbligatoriMessage.setFill(Color.BLACK);
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
    
    private void mostraEccezioneDialog(String titolo, String corpo) {
        Alert dialogEccezione = creaDialog(titolo, corpo);

        ButtonType buttonOk = new ButtonType("Ok");
        dialogEccezione.getButtonTypes().setAll(buttonOk);
        Optional<ButtonType> result = dialogEccezione.showAndWait();
    }
    
    private Alert creaDialog(String titolo, String corpo) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setHeaderText(titolo);
        if (corpo.contains("struttura_longitudine_key") || corpo.contains("struttura_latitidune_key")) {
            dialog.setContentText("A questo indirizzo è stata già assegnata una struttura.");
        } else if (corpo.contains("foto_pkey")){
            dialog.setContentText("Una o più foto sono state già utilizzate per un'altra struttura. Riprovare.");
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

    public ArrayList<FotoModel> estraiLista() {
        Iterator<FotoModel> iteratore = listaFotoAggiunte.iterator();
        int indice = 0;
        while (iteratore.hasNext()) {
            if (indice >= numeroDelleFotoDellaStruttura - numeroDelleFotoAggiunte) {
                FotoModel foto = iteratore.next();
                listaFotoDaAggiungere.add(new FotoModel(foto.getNomeFile(),foto.getPathFile()));
            } else {
                iteratore.next();
            }
            indice++;
        }
        return listaFotoDaAggiungere;
    }

    public void eliminaFotoSingole(Connection conn,ArrayList<FotoModel> listaFotoDaEliminare) {
        Iterator<FotoModel> iteratore = listaFotoDaEliminare.iterator();
        while (iteratore.hasNext()) {
            try {
                fotoDAO.eliminaFoto(conn,iteratore.next().getPathFile());
            } catch (SQLException ex) {
                mostraEccezioneDialog("Errore", ex.getMessage());
            }
        }
    }

    public void aggiungiFoto(Connection conn, List<FotoModel> listaFoto, int idStruttura) throws SQLException{
        Iterator<FotoModel> iteratore = listaFoto.iterator();
        while (iteratore.hasNext()) {
                String nomeFile = iteratore.next().getNomeFile();
                fotoDAO.inserisciFoto(conn, nomeFile, idStruttura);     
        }
    }

    public void aggiornaDatiStruttura() {
        strutturaDaModificare.setNome(nomeStruttura);
        strutturaDaModificare.setCategoria(categoriaStruttura);
        strutturaDaModificare.setSottocategoria(sottocategoriaStruttura);
        strutturaDaModificare.setIndirizzo(indirizzoStruttura);
        strutturaDaModificare.setInformazioni(informazioniStruttura);
        strutturaDaModificare.setPrezzoDa(prezzoDaStruttura);
        strutturaDaModificare.setPrezzoA(prezzoAStruttura);
        strutturaDaModificare.setCopertinaURL(listaFotoAggiunte.get(0).getPathFile());
        ObservableList<FotoModel> obl = FXCollections.observableArrayList(listaFotoAggiunte);
        listaFotoDellaStruttura.setItems(obl);
    }

    public void notificaModifica() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/visualizzaStruttura.fxml"));
        try {
            Parent a = loader.load();
            Scene ab = new Scene(a);
            ab.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/style.css").toExternalForm());
            VisualizzaStrutturaPresenter controller = loader.getController();
            controller.initData(strutturaDaModificare, riferimentoARicercaStrutturaPresenter, this);

            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setTitle(nomeStruttura);
            window.setScene(ab);
            window.show();

            Notifications notifica = Notifications.create()
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_LEFT);
            notifica.title("");
            notifica.text("La struttura è stata modificata.");
            notifica.show();
            riferimentoARicercaStrutturaPresenter.cerca(new ActionEvent());
        } catch (IOException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
            }

    }

    @FXML
    private void clickBack(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/visualizzaStruttura.fxml"));
            Parent a = loader.load();
            Scene ab = new Scene(a);
            VisualizzaStrutturaPresenter controller = loader.getController();
            controller.initData(strutturaDaModificare, riferimentoARicercaStrutturaPresenter, this);

            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(ab);
            window.show();

    }

    @Override
    @FXML
    public void logout(ActionEvent event) throws IOException{
           Parent menuPrincipale = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene menuPrincipaleView = new Scene(menuPrincipale);
            
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(menuPrincipaleView);
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
}
