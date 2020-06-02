package DAO;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import interfaces.RicercaIndirizzo;

public class StrutturaDAO {
    private static final String prefix = "https://consigliaviaggi.s3.eu-west-3.amazonaws.com/";
    private String apikey = "pk.eyJ1IjoiY2FwcGFkYXZpZGUiLCJhIjoiY2s4dnN3Z2tuMGp1ejNvczc1OWpoNGxidSJ9.Xfan0iqgJviP24q-ThnTUA";
    private String types = "types=address,place";
    private String language = "&language=it";
    private String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/";
    private static AsyncHttpClient client = Dsl.asyncHttpClient();
    private PreparedStatement preparedStatement;
    private Connection connection;

    public void search(String termine, RicercaIndirizzo presenter) throws IOException, InterruptedException, ExecutionException {
        client.prepareGet(url + URLEncoder.encode(termine, "UTF-8") + ".json?access_token=" + apikey + "&" + types + language).execute(
                new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) {
                int resposeStatusCode = response.getStatusCode();
                presenter.rispostaRicevuta(response.getResponseBody());
                return response;
            }
        }
        );
    }

    public static void closeThreads() {
        try {
            client.close();
        } catch (IOException ex) {
            System.out.println("Eccezione thread");
        }
    }

    public ResultSet visualizzaListaStrutture(Connection conn, String nome, String categoria, String sottocategoria, String indirizzo, double rating, String ordinaPerNome, String ordinaPerCategoria, String ordinaPerSottocategoria, String ordinaPerIndirizzo, String ordinaPerRating, String ordinaPerPrezzo) throws SQLException {
        String sql = null;
        sql = "SELECT * FROM STRUTTURA WHERE TRUE ";
        int indiceColonna = 0;

        //COMMAND
        if (!nome.equals("")) {
            sql = sql + "AND NOME LIKE ? ";

        }
        if (!categoria.equals("")) {
            sql = sql + "AND CATEGORIA=?::enumcategoria ";

        }
        if (!sottocategoria.equals("")) {
            sql = sql + "AND SOTTOCATEGORIA=?::enumsottocategoria ";

        }
        if (!indirizzo.equals("")) {
            sql = sql + "AND INDIRIZZO LIKE ? ";

        }
        if (rating >= 0.0) {
            sql = sql + "AND ROUND(CAST (RATING AS NUMERIC))=ROUND(?)  ";

        }

        //ORDER BY ..
        sql = sql + "ORDER BY TRUE ";

        if (!ordinaPerNome.equals("")) {
            sql = sql + ",nome";

        }
        if (!ordinaPerCategoria.equals("")) {
            sql = sql + ",categoria";

        }
        if (!ordinaPerSottocategoria.equals("")) {
            sql = sql + ",sottocategoria";

        }
        if (!ordinaPerIndirizzo.equals("")) {
            sql = sql + ",indirizzo";

        }
        if (!ordinaPerRating.equals("")) {
            if (ordinaPerRating.equals("crescente")) {
                sql = sql + ",rating " + "asc";
            } else {
                sql = sql + ",rating " + "desc";
            }
        }
        if (!ordinaPerPrezzo.equals("")) {
            if (ordinaPerPrezzo.equals("crescente")) {
                sql = sql + ",prezzoda " + "asc";
            } else {
                sql = sql + ",prezzoda " + "desc";
            }
        }

        preparedStatement = conn.prepareStatement(sql);

        //ora che il comando è costruito, setto i campi
        if (!nome.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, "%" + nome + "%");
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
            preparedStatement.setString(indiceColonna, "%" + indirizzo + "%");
        }
        if (rating >= 0.0) {
            indiceColonna++;
            preparedStatement.setDouble(indiceColonna, rating);
        }

        return preparedStatement.executeQuery();
    }

    public float approssimaRating(double rating) {
        return (float) round(rating, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void inserisciStruttura(Connection conn, String nome, String categoria, String sottocategoria, String informazioni, String indirizzo, double latitudine, double longitudine, String prezzoDa, String prezzoA, String copertinaurl) throws SQLException {
        String sql = "INSERT INTO STRUTTURA(NOME,INDIRIZZO,INFORMAZIONI,CATEGORIA,SOTTOCATEGORIA,PREZZOA,PREZZODA,COPERTINAURL,LATITUDINE,LONGITUDINE) VALUES(?,?,?,?::enumcategoria,?::enumsottocategoria,?,?,?,?,?)";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, nome);
        preparedStatement.setString(2, indirizzo);
        preparedStatement.setString(3, informazioni);
        preparedStatement.setString(4, categoria);
        preparedStatement.setString(5, sottocategoria);
        preparedStatement.setDouble(6, Double.parseDouble(prezzoA));
        preparedStatement.setDouble(7, Double.parseDouble(prezzoDa));
        preparedStatement.setString(8, prefix + copertinaurl);
        preparedStatement.setDouble(9, latitudine);
        preparedStatement.setDouble(10, longitudine);
        preparedStatement.executeUpdate();
        preparedStatement.close();

    }

    public ResultSet trovaIdStrutturaMassimo(Connection conn) throws SQLException {
        String sql = "SELECT MAX(ID) AS id FROM STRUTTURA";
        preparedStatement = conn.prepareStatement(sql);
        return preparedStatement.executeQuery();
    }

    public int modificaStruttura(Connection conn, String nome, String categoria, String sottocategoria, String informazioni, String indirizzo, String prezzoDa, String prezzoA, String id, String copertinaURL, double latitudine, double longitudine) throws SQLException {
        String sql = "UPDATE STRUTTURA SET ";
        int indiceColonna = 0;

        if (!nome.equals("")) {
            sql = sql + "NOME=? ";

        }
        if (!categoria.equals("")) {
            sql = sql + ",CATEGORIA=?::enumcategoria ";

        }
        if (!sottocategoria.equals("")) {
            sql = sql + ",SOTTOCATEGORIA=?::enumsottocategoria ";

        }
        if (!indirizzo.equals("")) {
            sql = sql + ",INDIRIZZO=? ";

        }
        if (!informazioni.equals("")) {
            sql = sql + ",INFORMAZIONI=? ";

        }
        if (!prezzoDa.equals("")) {
            sql = sql + ",PREZZODA=? ";
        }
        if (!prezzoA.equals("")) {
            sql = sql + ",PREZZOA=? ";
        }
        if (!copertinaURL.equals("")) {
            sql = sql + ",COPERTINAURL=? ";
        }
        if (latitudine != 0.0) {
            sql = sql + ",LATITUDINE=?";
        }
        if (longitudine != 0.0) {
            sql = sql + ",LONGITUDINE=?";
        }

        sql = sql + "WHERE ID=?";

        preparedStatement = conn.prepareStatement(sql);

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
        if (!informazioni.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, informazioni);
        }
        if (!prezzoDa.equals("")) {
            indiceColonna++;
            preparedStatement.setDouble(indiceColonna, Double.parseDouble(prezzoDa));
        }
        if (!prezzoA.equals("")) {
            indiceColonna++;
            preparedStatement.setDouble(indiceColonna, Double.parseDouble(prezzoA));
        }
        if (!copertinaURL.equals("")) {
            indiceColonna++;
            preparedStatement.setString(indiceColonna, prefix + copertinaURL);
        }
        if (latitudine != 0.0) {
            indiceColonna++;
            preparedStatement.setDouble(indiceColonna, latitudine);
        }
        if (longitudine != 0.0) {
            indiceColonna++;
            preparedStatement.setDouble(indiceColonna, longitudine);
        }

        preparedStatement.setInt(++indiceColonna, Integer.parseInt(id));
        
        return preparedStatement.executeUpdate();

    }

    public void eliminaStruttura(Connection conn, String idStruttura) throws SQLException {
        String sql = "DELETE FROM STRUTTURA WHERE ID=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, Integer.parseInt(idStruttura));
        preparedStatement.executeUpdate();
        connection.close();
        preparedStatement.close();

    }

    public ResultSet recuperaFotoAnteprima(Connection conn, String identificativoStruttura) throws SQLException {
        String sql = "SELECT COPERTINAURL FROM STRUTTURA WHERE ID=?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, Integer.parseInt(identificativoStruttura));
        return preparedStatement.executeQuery();
    }
}
