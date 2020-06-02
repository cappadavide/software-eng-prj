package presenter;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import model.FotoModel;

public class FotoDaVisualizzareCell extends ListCell<FotoModel> {

    @FXML
    private FXMLLoader mLLoader;

    @FXML
    private GridPane gridpane;

    @FXML
    private ImageView foto;

    @Override
    protected void updateItem(FotoModel fs, boolean empty) {
        super.updateItem(fs, empty);
        if (empty || fs == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/view/fotoDaVisualizzareCell.fxml"));
                mLLoader.setController(this);
                
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    mostraEccezioneDialog("Errore", e.getMessage());
                }
            }
            foto.setImage(new Image(fs.getPathFile(),500,500,false,false));
            setText(null);
            setGraphic(gridpane);
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
