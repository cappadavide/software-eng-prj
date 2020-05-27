package presenter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

public class MenuPresenter implements Initializable {

    @FXML
    MenuBar menuBar;

    private RecensioniPresenter recensioni;

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
    private void clickGestisciStrutture(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Gestisci Strutture");
        Parent gestisciStrutture = FXMLLoader.load(getClass().getResource("/view/gestisciStrutture.fxml"));
        Scene gestisciStruttureView = new Scene(gestisciStrutture);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciStruttureView);
        window.show();

    }

    @FXML
    private void clickGestisciRecensioni(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Gestisci Recensioni");
        Parent gestisciRecensioni = FXMLLoader.load(getClass().getResource("/view/listaRecensioni.fxml"));
        Scene gestisciRecensioniView = new Scene(gestisciRecensioni);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(gestisciRecensioniView);
        window.show();

    }

    @FXML
    private void clickAggiungiStruttura(final ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Aggiungi Struttura");
        Parent aggiungiStruttura = FXMLLoader.load(getClass().getResource("/view/aggiungiStruttura.fxml"));
        Scene aggiungiStrutturaView = new Scene(aggiungiStruttura);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(aggiungiStrutturaView);
        window.show();
    }

    @FXML
    private void clickRicercaStruttura(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Ricerca struttura");
        Parent ricercaStruttura = FXMLLoader.load(getClass().getResource("/view/ricercaStruttura.fxml"));
        Scene ricercaStrutturaView = new Scene(ricercaStruttura);

        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(ricercaStrutturaView);
        window.show();
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

}
