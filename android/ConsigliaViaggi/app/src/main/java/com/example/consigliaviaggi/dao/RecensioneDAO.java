package com.example.consigliaviaggi.dao;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.consigliaviaggi.PresenterToRecensioneDAO;
import com.example.consigliaviaggi.ToPresenterSchedaStruttura;
import com.example.consigliaviaggi.model.Recensione;
import com.example.consigliaviaggi.ToPresenterRecensioniScritte;
import com.example.consigliaviaggi.ToPresenterScriveRecensione;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class RecensioneDAO implements PresenterToRecensioneDAO {

    private ToPresenterScriveRecensione scriveRecensionePresenter;
    private ToPresenterRecensioniScritte recensioniScrittePresenter;
    private ToPresenterSchedaStruttura schedaStrutturaPresenter;

    public RecensioneDAO(ToPresenterSchedaStruttura schedaStrutturaPresenter) {
        this.schedaStrutturaPresenter = schedaStrutturaPresenter;
    }

    public RecensioneDAO(ToPresenterScriveRecensione scriveRecensionePresenter) {
        this.scriveRecensionePresenter = scriveRecensionePresenter;
    }

    public RecensioneDAO(ToPresenterRecensioniScritte recensioniScrittePresenter) {
        this.recensioniScrittePresenter = recensioniScrittePresenter;
    }

    public void writeReview(String titolo, String corpo, int usernamecheck, int rating, String usernameutente) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(scriveRecensionePresenter.getContext());
        final int strutturaid = sharedPreferences.getInt("id", 0);

        RequestQueue queue = Volley.newRequestQueue(scriveRecensionePresenter.getContext());
        String url="https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=inserisci_recensione&titolo="+titolo+"&corpo="+corpo+"&rating="+rating+"&usernamecheck="+usernamecheck+"&usernameutente="+usernameutente+"&strutturaid="+strutturaid;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url,null,
                response -> {

                    JSONObject result= null;

                    try {
                        result = response.getJSONObject(0);
                        scriveRecensionePresenter.reviewDone();
                    }
                    catch (JSONException e) {
                        scriveRecensionePresenter.showError();
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            scriveRecensionePresenter.showError();
        });
        queue.add(jsr);
    }

    public void obtainReviewsByUsername(String username){

        RequestQueue queue = Volley.newRequestQueue(recensioniScrittePresenter.getContext());
        String url = "https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=visualizza_recensioni_utente&usernameutente=" + username;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {

                    JSONObject result = null;
                    String[] titolo = new String[response.length()];
                    String[] corpo = new String[response.length()];
                    String[] nomeStruttura = new String[response.length()];
                    int[] rating = new int[response.length()];

                    try {

                        for (int i = 0; i < response.length(); i++) {

                            result = response.getJSONObject(i);
                            titolo[i] = result.getString("titolo");
                            corpo[i] = result.getString("corpo");
                            rating[i] = Integer.parseInt(result.getString("rating"));
                            nomeStruttura[i] = result.getString("nome");
                        }
                        recensioniScrittePresenter.switchReviewsListFromDaoToView(titolo, corpo, rating, nomeStruttura);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            recensioniScrittePresenter.showError();
        });
        queue.add(jsr);
    }

    public void obtainReviewsById(int id, int rating) {

        RequestQueue queue = Volley.newRequestQueue(schedaStrutturaPresenter.getContext());
        String url = "https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=visualizza_recensioni_struttura&strutturaid="+id+"&rating="+rating;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {

                    JSONObject result = null;
                    int[] usernameCheck = new int[response.length()];
                    String[] username = new String[response.length()];
                    String[] nomeUtente = new String[response.length()];
                    String[] cognomeUtente = new String[response.length()];
                    String[] titolo = new String[response.length()];
                    String[] corpo = new String[response.length()];
                    int[] rating1 = new int[response.length()];
                    ArrayList<Recensione> recensioniArrayList = new ArrayList<>();

                    try {
                        for (int i = 0; i < response.length(); i++) {

                            result = response.getJSONObject(i);

                            //variabili relative all'utente
                            usernameCheck[i] = Integer.parseInt(result.getString("usernamecheck"));
                            cognomeUtente[i] = result.getString("cognome");
                            nomeUtente[i] = result.getString("nome");
                            username[i] = result.getString("username");

                            //variabili relative alla recensione
                            titolo[i] = result.getString("titolo");
                            corpo[i] = result.getString("corpo");
                            rating1[i] = Integer.parseInt(result.getString("rating"));

                            Recensione recensione = new Recensione();

                            recensione.setUsenameCheck(usernameCheck[i]);
                            recensione.setCorpo(corpo[i]);
                            recensione.setTitolo(titolo[i]);
                            recensione.setCognomeUtente(cognomeUtente[i]);
                            recensione.setNomeUtente(nomeUtente[i]);
                            recensione.setRating(rating1[i]);
                            recensione.setUsernameUtente(username[i]);
                            recensioniArrayList.add(recensione);
                        }
                        schedaStrutturaPresenter.switchReviewsListToView(recensioniArrayList);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            schedaStrutturaPresenter.showError();
        });
        queue.add(jsr);
    }
}
