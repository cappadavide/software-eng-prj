package presenter;

import DAO.DBManager;
import DAO.StrutturaDAO;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.StrutturaModel;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.Rating;
import java.util.TimerTask;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import interfaces.MenuUtils;
import java.sql.Connection;

public class RicercaStrutturaPresenter implements Initializable, MenuUtils {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label message, risultatiMessage, ratingString;

    @FXML
    private JFXListView listaDeiRisultati;

    @FXML
    private Pane pane;

    @FXML
    private ComboBox categoriaComboBox, sottocategoriaComboBox;

    @FXML
    private TextField nomeField, indirizzoField;

    @FXML
    private Rating ratingButton;

    @FXML
    private JFXCheckBox checkRatingDisable, orderByName, orderByCategoria, orderBySottocategoria, orderByIndirizzo;

    @FXML
    private ComboBox filtroRatingComboBox, filtroPrezzoComboBox;

    private String nomeStruttura = "", sottocategoriaStruttura = "", categoriaStruttura = "", indirizzoStruttura = "";

    private String ordinaPerNome = "", ordinaPerCategoria = "", ordinaPerSottocategoria = "", ordinaPerIndirizzo = "", ordinaPerRating = "", ordinaPerPrezzo = "";

    private double ratingStruttura = 0.0;

    private int countStrutture = 0;

    private ArrayList<StrutturaModel> listaStrutture = new ArrayList<>();

    private StrutturaDAO strutturaDAO;

    ObservableList<String> tipoCategoria = FXCollections.observableArrayList("", "alberghi", "ristoranti", "attrazioni");
    ObservableList<String> tipoSottocategoriaAlberghi = FXCollections.observableArrayList("", "hotel", "bb", "appartamento", "altro");
    ObservableList<String> tipoSottocategoriaRistoranti = FXCollections.observableArrayList("", "pizzeria", "fastfood", "ristorante", "altro");
    ObservableList<String> tipoSottocategoriaAttrazioni = FXCollections.observableArrayList("", "museo", "zoo", "Parco giochi", "altro");
    ObservableList<String> filtroRating = FXCollections.observableArrayList("", "crescente", "decrescente");
    ObservableList<String> filtroPrezzo = FXCollections.observableArrayList("", "crescente", "decrescente");

    private static final String prefix = "https://consigliaviaggi.s3.eu-west-3.amazonaws.com/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.strutturaDAO = new StrutturaDAO();
        sottocategoriaComboBox.setDisable(true);
        categoriaComboBox.setItems(tipoCategoria);
        orderByName.setSelected(false);
        orderBySottocategoria.setSelected(false);
        orderByIndirizzo.setSelected(false);
        orderByCategoria.setSelected(false);
        listaDeiRisultati.setVisible(false);
        risultatiMessage.setVisible(false);
        filtroRatingComboBox.setItems(filtroRating);
        filtroPrezzoComboBox.setItems(filtroPrezzo);
        categoriaComboBox.setValue("");
        filtroRatingComboBox.setValue("");
        filtroPrezzoComboBox.setValue("");
        ratingString.setText("");
        ratingButton.setDisable(true);
        ratingString.setVisible(false);
        setRatingButtonListener();
    }

    private void setRatingButtonListener() {
        ratingButton.ratingProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((double) newValue >= 1.0 || (double) newValue <= 5.0) {
                    ratingString.setVisible(true);
                    ratingString.setText((approssimaRating((double) newValue).toString()));
                }
            }
        });
    }
    
    public static Number approssimaRating(double rating) {
        return (float) round(rating, 2);
    }

    public static double round(double ratingDaApprossimare, int numeroDiCifreSignificative) {
        if (numeroDiCifreSignificative < 0) {
            throw new IllegalArgumentException();
        }
        long fattoreDaMoltiplicare = (long) Math.pow(10, numeroDiCifreSignificative);
        ratingDaApprossimare = ratingDaApprossimare * fattoreDaMoltiplicare;
        long ratingApprossimato = Math.round(ratingDaApprossimare);
        return (double) ratingApprossimato / fattoreDaMoltiplicare;
    }

    @FXML
    public void abilitaRatingButton(MouseEvent event) {
        if (checkRatingDisable.isSelected()) {
            ratingButton.setDisable(false);
            ratingString.setVisible(true);
        } else {
            ratingButton.setDisable(true);
            ratingString.setVisible(false);
        }
    }

    @FXML
    public void cerca(final ActionEvent event) throws IOException {
        listaStrutture.clear();
        try {
            Connection conn = DBManager.getConnection(true);
            nomeStruttura = nomeField.getText();
            categoriaStruttura = categoriaComboBox.getSelectionModel().getSelectedItem().toString();

            if (!sottocategoriaComboBox.isDisabled()) {
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
                ordinaPerIndirizzo = "indirizzo";
            }
            indirizzoStruttura = indirizzoField.getText();

            if (ratingButton.isDisable()) {
                ratingStruttura = -1;
            } else {
                ratingStruttura = ratingButton.getRating();
            }

            ordinaPerRating = filtroRatingComboBox.getSelectionModel().getSelectedItem().toString();
            ordinaPerPrezzo = filtroPrezzoComboBox.getSelectionModel().getSelectedItem().toString();

            ResultSet rs = strutturaDAO.visualizzaListaStrutture(conn,nomeStruttura, categoriaStruttura, sottocategoriaStruttura, indirizzoStruttura, ratingStruttura, ordinaPerNome, ordinaPerCategoria, ordinaPerSottocategoria, ordinaPerIndirizzo, ordinaPerRating, ordinaPerPrezzo);
            if (!rs.isBeforeFirst()) {
                propertyMessage(0);
                propertyList(false);
            } else {
                countStrutture = 0;
                while (rs.next()) {
                    listaStrutture.add(new StrutturaModel(
                            rs.getString("nome"),
                            rs.getString("categoria"),
                            rs.getString("sottocategoria"),
                            rs.getString("indirizzo"),
                            rs.getString("rating"),
                            rs.getString("informazioni"),
                            rs.getString("id"),
                            rs.getString("prezzoda"),
                            rs.getString("prezzoa"),
                            rs.getString("copertinaurl")
                    ));
                    countStrutture++;
                }
                ObservableList<StrutturaModel> obl = FXCollections.observableArrayList(listaStrutture);
                listaDeiRisultati.setItems(obl);
                listaDeiRisultati.setCellFactory(strutturaCell -> new StrutturaCell());
                propertyMessage(countStrutture);
                propertyList(true);
                listaDeiRisultati.setVisible(true);
                pulisciParametri();
                conn.close();
            }
        } catch (SQLException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
            propertyMessage(-1);
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

    private void pulisciParametri() {
        nomeStruttura = "";
        sottocategoriaStruttura = "";
        categoriaStruttura = "";
        indirizzoStruttura = "";
        ordinaPerNome = "";
        ordinaPerCategoria = "";
        ordinaPerSottocategoria = "";
        ordinaPerIndirizzo = "";
        ordinaPerRating = "";
        ordinaPerPrezzo = "";
    }

    private void propertyList(boolean listaConAlmenoUnRisultato) {
        if (!listaConAlmenoUnRisultato) {
            listaStrutture.clear();
            listaDeiRisultati.setVisible(false);
        } else {
            pane.setStyle("-fx-background-color: white");
        }
    }

    private void propertyMessage(int numeroStrutture) {
        if (numeroStrutture <= 0) {
            if (numeroStrutture==0){
                message.setText("Nessuna struttura individuata.");
            }
            else {
              message.setText("Errore. Si è verificato un problema.");  
            }
            risultatiMessage.setVisible(false);
            message.setStyle("-fx-font-size: 18px");
            message.setFont(Font.font("Segoe UI Bold"));
            risultatiMessage.setVisible(false);
            pane.setStyle("-fx-background-color: #F4F4F4");
        } else {
            risultatiMessage.setVisible(true);
            message.setLayoutX(14);
            message.setStyle("-fx-font-size: 18px");
            message.setFont(Font.font("Segoe UI Regular"));
            if (numeroStrutture == 1) {
                message.setText("E' stata trovata " + numeroStrutture + " struttura secondo i termini di ricerca.");
            } else {
                message.setText("Sono state trovate " + numeroStrutture + " strutture secondo i termini di ricerca.");
            }
        }
    }

    public void scegliCategoria(ActionEvent e) {
        String scelta = categoriaComboBox.getSelectionModel().getSelectedItem().toString();
        if (scelta.equals("alberghi") || scelta.equals("ristoranti") || scelta.equals("attrazioni")) {
            sottocategoriaComboBox.setDisable(false);
            sottocategoriaComboBox.setValue("");
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
    private void selezionaStruttura(MouseEvent event) throws IOException {
        StrutturaModel strutturaScelta = (StrutturaModel) listaDeiRisultati.getSelectionModel().getSelectedItem();
        try {
            visualizzaStruttura(strutturaScelta);
        } catch (IOException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
    }

    public void visualizzaStruttura(StrutturaModel struttura) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/visualizzaStruttura.fxml"));
        try {
            Parent a = loader.load();
            Scene ab = new Scene(a);
            VisualizzaStrutturaPresenter controller = loader.getController();
            controller.initData(struttura, this, null);

            Stage window = new Stage();
            window.setTitle(struttura.getNome());
            window.setScene(ab);
            window.show();

        } catch (IOException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
    }

    public void aggiornaListaStrutture() throws IOException {
        cerca(new ActionEvent());
        Notifications notifica = Notifications.create()
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT);
        notifica.title("");
        notifica.text("La struttura è stata eliminata");
        notifica.show();
    }

    @FXML
    private void tornaAllaGestioneDelleStrutture(ActionEvent event) throws IOException {
        Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/gestisciStrutture.fxml"));
        Scene gestisciStruttureView = new Scene(gestisciStrutture);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciStruttureView);
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
