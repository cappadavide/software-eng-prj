package presenter;

import model.RecensioneModel;
import presenter.VisualizzaRecensionePresenter;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Rating;

public class RecensioneCell extends ListCell<RecensioneModel> {

    @FXML
    private Label labelUsernameUtente;

    @FXML
    private Label labelStrutturaId;

    @FXML
    private Label labelTitolo;

    @FXML
    private Rating ratingField;

    @FXML
    private Text labelCorpo;

    @FXML
    private GridPane gridPane;

    @FXML
    private Button arrowButton;

    @FXML
    private FXMLLoader mLLoader;

    @FXML
    private MenuBar menubar;

    @Override
    protected void updateItem(RecensioneModel recensione, boolean empty) {
        super.updateItem(recensione, empty);
        if (empty || recensione == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/view/recensioneCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    mostraEccezioneDialog("Errore", e.getMessage());
                }
            }
            labelUsernameUtente.setText(recensione.getUsernameUtente());
            labelStrutturaId.setText(recensione.getStrutturaId());
            labelTitolo.setText(recensione.getTitolo());
            ratingField.setRating(Integer.parseInt(recensione.getRating()));
            labelCorpo.setText(recensione.getCorpo());
            arrowButton.setVisible(true);
            arrowButton.setId(recensione.getID());
            arrowButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/view/visualizzaRecension.fxml"));
                    try {
                        Parent a = loader.load();
                        Scene ab = new Scene(a);
                        VisualizzaRecensionePresenter controller = loader.getController();
                        controller.initData(recensione);

                        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        window.setScene(ab);
                        window.show();
                    } catch (IOException ex) {
                        mostraEccezioneDialog("Errore", ex.getMessage());
                    }
                }
            });
            setText(null);
            setGraphic(gridPane);
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

}
