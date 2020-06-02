package consigliaviaggi;

import DAO.StrutturaDAO;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/login.fxml"));
        Parent content = loader.load();
        primaryStage.setResizable(false);
        Scene loginView = new Scene(content);
        primaryStage.setTitle("ConsigliaViaggi");
        primaryStage.setScene(loginView);
        primaryStage.getIcons().add(new Image("/consigliaviaggi/immagini/icon.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
        StrutturaDAO.closeThreads();
        System.out.println("Chiusura avvenuta con successo");
    }

}
