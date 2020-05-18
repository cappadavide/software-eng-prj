package consigliaviaggi;

import model.RecensioneModel;
import presenter.VisualizzaRecensionePresenter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
    private Label labelCorpo;

    @FXML
    private GridPane gridPane;

    @FXML
    private ImageView poster;

    @FXML
    private Button arrowButton;

    @FXML
    private FXMLLoader mLLoader;

    @FXML
    private MenuBar menubar;

    @Override
    protected void updateItem(RecensioneModel fs, boolean empty) {
        super.updateItem(fs, empty);
        if (empty || fs == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/view/recensioneCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    System.out.println("RECENSIONE CELL");
                }
            }
            labelUsernameUtente.setText(fs.getUsernameUtente());
            labelStrutturaId.setText(fs.getStrutturaId());
            labelTitolo.setText(fs.getTitolo());
            ratingField.setRating(Integer.parseInt(fs.getRating()));
            labelCorpo.setText(fs.getCorpo());
            arrowButton.setVisible(true);
            arrowButton.setId(fs.getID());
            arrowButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/view/visualizzaRecension.fxml"));
                    try {
                        Parent a = loader.load();
                        Scene ab = new Scene(a);
                        VisualizzaRecensionePresenter controller = loader.getController();
                        controller.initData(fs);

                        Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        window.setScene(ab);
                        window.show();
                    } catch (IOException ex) {
                        Logger.getLogger(RecensioneCell.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Errore");
                    }
                }
            });
            setText(null);
            setGraphic(gridPane);
        }
    }
}
