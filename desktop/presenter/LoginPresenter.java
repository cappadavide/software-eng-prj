package presenter;

import DAO.LoginDAO;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class LoginPresenter implements Initializable {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;

    @FXML
    private Text errorMessage;
    
    private LoginDAO model;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
      
    }
    
    @FXML
    private void login(ActionEvent event) throws IOException {
        System.out.println("Hai cliccato Accedi");
        this.model=new LoginDAO();
        
        String username = usernameField.getText();
        String password = passwordField.getText();
        
       // try {
         //   model.connect(username,password);
            Parent menuPrincipale = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
            Scene menuPrincipaleView = new Scene(menuPrincipale);
        
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(menuPrincipaleView);
            window.show();
      //  }
      //  catch (SQLException e){
          //  System.err.println("SQLState: " +
          //          ((SQLException)e).getSQLState());

            //    System.err.println("Error Code: " +
            //        ((SQLException)e).getErrorCode());

          //  errorMessage.setText("Nome utente o credenziali non valide");
          //  errorMessage.setFill(Color.FIREBRICK);
       // }
    }
    
}