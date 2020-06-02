package presenter;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.MenuBar;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import interfaces.MenuUtils;

public class MenuPresenter implements Initializable, MenuUtils {

    @FXML
    private MenuBar menuBar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void gestisciStrutture(ActionEvent event) throws IOException {
        Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/gestisciStrutture.fxml"));
        Scene gestisciStruttureView = new Scene(gestisciStrutture);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciStruttureView);
        window.show();

    }

    @FXML
    private void gestisciRecensioni(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/listaRecensioni.fxml"));
        Parent gestisciRecensioni = loader.load();
        Scene gestisciRecensioniView = new Scene(gestisciRecensioni);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciRecensioniView);
        window.show();

    }

    @FXML
    private void aggiungiStruttura(final ActionEvent event) throws IOException {
        Parent aggiungiStruttura = FXMLLoader.load(getClass().getResource("/view/aggiungiStruttura.fxml"));
        Scene aggiungiStrutturaView = new Scene(aggiungiStruttura);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(aggiungiStrutturaView);
        window.show();
    }

    @FXML
    private void ricercaStruttura(ActionEvent event) throws IOException {
        Parent ricercaStruttura = FXMLLoader.load(getClass().getResource("/view/ricercaStruttura.fxml"));
        Scene ricercaStrutturaView = new Scene(ricercaStruttura);
        ricercaStrutturaView.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/style.css").toExternalForm());
        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(ricercaStrutturaView);
        window.show();
    }

    @FXML
    private void tornaAlMenu(ActionEvent event) throws IOException {
        Parent menu = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        Scene menuView = new Scene(menu);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(menuView);
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

}
