package presenter;

import DAO.RecensioneDAO;
import model.RecensioneModel;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.Rating;

public class VisualizzaRecensionePresenter implements Initializable {

    private RecensioneModel recensione;

    @FXML
    private Label labelUsernameUtente;

    @FXML
    private Label labelStrutturaId;

    @FXML
    private Label labelTitolo;

    @FXML
    private Rating ratingField;

    @FXML
    private Label labelCorpo;

    @FXML
    private Label labelID;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button arrowButton;

    @FXML
    private FXMLLoader mLLoader;

    @FXML
    private MenuBar menuBar;

    public void initData(RecensioneModel rs) throws IOException {
        this.recensione = rs;
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

    @FXML
    private void clickBack(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Indietro");
        Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/listaRecensioni.fxml"));
        Scene gestisciStruttureView = new Scene(gestisciStrutture);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciStruttureView);
        window.show();
    }

    @FXML
    private void mostraApprovaDialog(ActionEvent event) throws IOException, SQLException {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setHeaderText("Valutazione recensione");
        dialog.setContentText("Vuoi confermare l'operazione?");
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        dialog.setX((bounds.getMaxX() / 2) - 150);
        dialog.setY((bounds.getMaxY() / 2) - 100);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/alert.css").toExternalForm());
        dialog.initStyle(StageStyle.UNDECORATED);

        ButtonType buttonSi = new ButtonType("Si");
        ButtonType buttonNo = new ButtonType("No");
        dialog.getButtonTypes().setAll(buttonSi, buttonNo);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == buttonSi) {
            RecensioneDAO model = new RecensioneDAO();
            model.approvaRecensione(labelID.getText());
            visualizzaListaRecensioni();
        } else {
            System.out.println("Hai cliccato no");
        }
    }

    @FXML
    private void mostraDeclinaDialog(ActionEvent event) throws IOException, SQLException {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.setHeaderText("Valutazione recensione");
        dialog.setContentText("Vuoi confermare l'operazione?");
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        dialog.setX((bounds.getMaxX() / 2) - 150);
        dialog.setY((bounds.getMaxY() / 2) - 100);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/alert.css").toExternalForm());
        dialog.initStyle(StageStyle.UNDECORATED);

        ButtonType buttonSi = new ButtonType("Si");
        ButtonType buttonNo = new ButtonType("No");
        dialog.getButtonTypes().setAll(buttonSi, buttonNo);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == buttonSi) {
            RecensioneDAO model = new RecensioneDAO();
            model.declinaRecensione(labelID.getText());
            visualizzaListaRecensioni();
        } else {
            System.out.println("Hai cliccato no");
        }
    }

    private void visualizzaListaRecensioni() throws IOException {
        System.out.println("Hai cliccato Indietro");
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
