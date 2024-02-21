package presenter;

import DAO.DBManager;
import DAO.FotoDAO;
import DAO.S3API;
import DAO.StrutturaDAO;
import com.jfoenix.controls.JFXListView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import model.FotoModel;
import model.StrutturaModel;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.Rating;
import interfaces.MenuUtils;
import java.sql.Connection;

public class VisualizzaStrutturaPresenter implements Initializable {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label nomeField, sottocategoriaField, indirizzoField, idStruttura, ratingPrezzo;

    @FXML
    private Rating ratingButton;

    @FXML
    private Text informazioniField;

    @FXML
    private ImageView copertina;

    @FXML
    private JFXListView listaFoto;

    private StrutturaModel strutturaDaVisualizzare;
    private FotoDAO fotoDAO;

    private RicercaStrutturaPresenter riferimentoARicercaStrutturaPresenter;
    private ModificaStrutturaPresenter riferimentoAModificaStrutturaPresenter;

    private ArrayList<FotoModel> listaFotoDellaStruttura = new ArrayList<>();

    public void initData(StrutturaModel struttura,RicercaStrutturaPresenter riferimentoARicercaStrutturaPresenter, ModificaStrutturaPresenter riferimentoAModificaStrutturaPresenter) {
        this.riferimentoARicercaStrutturaPresenter=riferimentoARicercaStrutturaPresenter;
        this.riferimentoAModificaStrutturaPresenter=riferimentoAModificaStrutturaPresenter;
        this.fotoDAO=new FotoDAO();
        strutturaDaVisualizzare = struttura;
        nomeField.setText(strutturaDaVisualizzare.getNome());
        ratingButton.setRating(Double.parseDouble(strutturaDaVisualizzare.getRating()));
        ratingButton.setDisable(true);
        sottocategoriaField.setText(strutturaDaVisualizzare.getSottocategoria());
        indirizzoField.setText(strutturaDaVisualizzare.getIndirizzo());
        informazioniField.setText(strutturaDaVisualizzare.getInformazioni());
        idStruttura.setText(strutturaDaVisualizzare.getID());
        ratingPrezzo.setText("€" + Double.parseDouble(strutturaDaVisualizzare.getPrezzoDa()) + "-" + "€" + Double.parseDouble(strutturaDaVisualizzare.getPrezzoA()));
        copertina.setImage(new Image(strutturaDaVisualizzare.getCopertinaURL()));
        setListaFoto();
    }

    public void setListaFoto() {
        try {
            Connection conn = DBManager.getConnection(true);
            ResultSet rs = fotoDAO.caricaFoto(conn,idStruttura.getText());
            rs.next();
            while (rs.next()) {
                listaFotoDellaStruttura.add(new FotoModel(rs.getString("url").substring(53), rs.getString("url")));
                ObservableList<FotoModel> obl = FXCollections.observableArrayList(listaFotoDellaStruttura);
                listaFoto.setItems(obl);
                listaFoto.setCellFactory(foto -> new FotoDaVisualizzareCell());
                listaFoto.setVisible(true);
            }
            conn.close();
        } catch (SQLException ex) {
             mostraEccezioneDialog("Errore", ex.getMessage());
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idStruttura.setVisible(false);
    }

    @FXML
    private void modificaStruttura(ActionEvent event) throws SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/modificaStruttura.fxml"));
        try {
            Parent a = loader.load();
            Scene ab = new Scene(a);
            ModificaStrutturaPresenter controller = loader.getController();
            controller.initData(strutturaDaVisualizzare,riferimentoARicercaStrutturaPresenter,this);

            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(ab);
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
    private void eliminaStruttura(ActionEvent event) throws IOException, SQLException {
        Alert eliminaStrutturaDialog = creaDialog();
        Connection conn = DBManager.getConnection(true);
        ButtonType buttonSi = new ButtonType("Si");
        ButtonType buttonNo = new ButtonType("No");
        eliminaStrutturaDialog.getButtonTypes().setAll(buttonSi, buttonNo);
        Optional<ButtonType> result = eliminaStrutturaDialog.showAndWait();
        if (result.get() == buttonSi) {
            eliminaFotoDaS3();
            StrutturaDAO strutturaDAO = new StrutturaDAO();
            strutturaDAO.eliminaStruttura(conn,idStruttura.getText());
            tornaAllaRicerca();
        }
    }
    
    private Alert creaDialog() {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setHeaderText("Eliminazione struttura");
        dialog.setContentText("Vuoi confermare l'operazione?");
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        dialog.setX((bounds.getMaxX() / 2) - 150);
        dialog.setY((bounds.getMaxY() / 2) - 100);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/alertStruttura.css").toExternalForm());
        dialog.initStyle(StageStyle.UNDECORATED);

        return dialog;
    }
    
    public void eliminaFotoDaS3(){
        try {
            Connection conn = DBManager.getConnection(true);
            fotoDAO = new FotoDAO();
            S3API s3API = new S3API();
            ResultSet rs = fotoDAO.caricaFoto(conn,idStruttura.getText());
            while(rs.next()){
                File pathFile = new File(rs.getString("url").substring(6));
                s3API.eliminaFoto(pathFile);
            }
            conn.close();
        } catch (SQLException ex) {
            mostraEccezioneDialog("Errore", ex.getMessage());
        }
    }
    
    @FXML
    private void zoomFoto(MouseEvent e) {
        FotoModel indice = (FotoModel) listaFoto.getSelectionModel().getSelectedItem();
        ImageView image = new ImageView(indice.getPathFile().toString());
        Alert dialog = new Alert(Alert.AlertType.NONE);
        DialogPane dialogPane = dialog.getDialogPane();
        dialog.initStyle(StageStyle.DECORATED);

        image.fitWidthProperty().bind(dialogPane.widthProperty());
        image.fitHeightProperty().bind(dialogPane.heightProperty());
        dialogPane.setContent(image);
        image.setPreserveRatio(true);

        Window window = dialog.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                dialog.hide();
            }
        });
        Optional<ButtonType> result = dialog.showAndWait();
    }

    private void tornaAllaRicerca() throws IOException {
        Stage stage = (Stage) menuBar.getScene().getWindow(); 
        stage.close();
        riferimentoARicercaStrutturaPresenter.aggiornaListaStrutture();
    }

}
