package DAO;

import java.io.IOException;
import java.io.Reader;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import org.json.*;
import org.asynchttpclient.*;
import presenter.AggiungiStrutturaPresenter;
import io.netty.util.TimerTask;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StrutturaDAO {

    private static final String urlDB = "consigliaviaggidb.cb6rvhwngqbh.eu-west-3.rds.amazonaws.com";
    private static final String port = "5432";
    private static final String nameDatabase = "postgres";
    PreparedStatement preparedStatement;
    Connection connection;

    String apikey = "pk.eyJ1IjoiY2FwcGFkYXZpZGUiLCJhIjoiY2s4dnN3Z2tuMGp1ejNvczc1OWpoNGxidSJ9.Xfan0iqgJviP24q-ThnTUA";
    String types = "types=country,region,locality,place,address";
    String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/";

    public void search(String termine) throws IOException, InterruptedException, ExecutionException {
        AsyncHttpClient client = Dsl.asyncHttpClient();
        BoundRequestBuilder boundGetRequest = client.prepareGet(url + URLEncoder.encode(termine, "UTF-8") + ".json?access_token=" + apikey + "&" + types);
        boundGetRequest.execute(new AsyncCompletionHandler<Integer>() {
            @Override
            public Integer onCompleted(Response response) {
                int resposeStatusCode = response.getStatusCode();
                System.out.println("completed");
                AggiungiStrutturaPresenter controller = new AggiungiStrutturaPresenter();
                controller.rispostaRicevuta(response.toString());
                return resposeStatusCode;
            }
        });
        client.close();

        // Response response = f.get();
    }

    public ResultSet visualizzaListaStrutture(String nome, String categoria, String sottocategoria, String indirizzo, double rating, String ordinaPerNome, String ordinaPerCategoria, String ordinaPerSottocategoria, String ordinaPerIndirizzo) throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://" + urlDB + ":" + port + "/" + nameDatabase, "root", "ingsw192016");
        String sql = "SELECT * FROM STRUTTURA WHERE TRUE ";
        int indiceColonna = 0;

        //costruisco il comando
        if (!nome.equals("")) {
            sql = sql + "AND NOME=? ";
            System.out.println(nome);

        }
        if (!categoria.equals("")) {
            sql = sql + "AND CATEGORIA=?::enumcategoria ";
            System.out.println("qui " + sottocategoria);

        }
        if (!sottocategoria.equals("")) {
            sql = sql + "AND SOTTOCATEGORIA=?::enumsottocategoria ";
            System.out.println("qui " + sottocategoria);

        }
        if (!indirizzo.equals("")) {
            sql = sql + "AND INDIRIZZO=? ";
            System.out.println(indirizzo);

        }
        if (rating != 0.0) {
            sql = sql + "AND RATING=? ";
            System.out.println(rating);
        }

        
        //ORDER BY ..
        sql = sql + "ORDER BY TRUE ";
        
        if (!ordinaPerNome.equals("")) {
            sql = sql + ",?";
            System.out.println(ordinaPerNome);
        }
        if (!ordinaPerCategoria.equals("")) {
            sql = sql + ",?";
            System.out.println(ordinaPerCategoria);
        }
        if (!ordinaPerSottocategoria.equals("")) {
            sql = sql + ",?";
            System.out.println(ordinaPerSottocategoria);
        }
        if (!ordinaPerIndirizzo.equals("")) {
            sql = sql + ",?";
            System.out.println(ordinaPerIndirizzo);
        }
        
        preparedStatement = connection.prepareStatement(sql);
        System.out.println(sql);
        
        //ora che il comando è costruito, setto i campi
        if (!nome.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, nome);
        }
        if (!categoria.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, categoria);
        }
        if (!sottocategoria.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, sottocategoria);
        }
        if (!indirizzo.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, indirizzo);
        }
        if (rating != 0.0) {
            indiceColonna++;
            preparedStatement.setInt(indiceColonna, (int) rating);
        }
        if (!ordinaPerNome.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, ordinaPerNome);
        }
        if (!ordinaPerCategoria.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, ordinaPerCategoria);
        }
        if (!ordinaPerSottocategoria.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, ordinaPerSottocategoria);
        }
        if (!ordinaPerIndirizzo.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, ordinaPerIndirizzo);
        }
        System.out.println(sql);
        return preparedStatement.executeQuery();
    }
}
