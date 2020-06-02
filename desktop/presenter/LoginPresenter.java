package presenter;

import DAO.LoginDAO;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.ErrorManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginPresenter implements Initializable {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorMessage;
    
    private LoginDAO loginDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
      this.loginDAO=new LoginDAO();
    }
    
    @FXML
    private void login(ActionEvent event) throws IOException, SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            loginDAO.connect(username,password);
            Parent menuPrincipale = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
            Scene menuPrincipaleView = new Scene(menuPrincipale);
        
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(menuPrincipaleView);
            window.show();
        }
        catch (SQLException e){
            mostraEccezione(e.getMessage());
        }
    }
    
    public void mostraEccezione(String messaggio){
        if(messaggio.contains("tentativo di connessione")){
            Alert dialogEccezione = creaDialog(messaggio);
            ButtonType buttonOk = new ButtonType("Ok");
            dialogEccezione.getButtonTypes().setAll(buttonOk);
            Optional<ButtonType> result = dialogEccezione.showAndWait();
        }
        else {
            errorMessage.setText("Nome utente o credenziali non valide");
            errorMessage.setFill(Color.FIREBRICK);
        } 
    }
    
    private Alert creaDialog(String messaggio){
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setHeaderText("Errore");
        dialog.setContentText(messaggio);
        
        javafx.geometry.Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        dialog.setX((bounds.getMaxX() / 2) - 150);
        dialog.setY((bounds.getMaxY() / 2) - 100);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/consigliaviaggi/css/alertStruttura.css").toExternalForm());
        dialog.initStyle(StageStyle.UNDECORATED);
        
        return dialog;
    }
    
}