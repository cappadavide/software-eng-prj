package com.example.consigliaviaggi.dao;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.consigliaviaggi.PresenterToUtenteDAO;
import com.example.consigliaviaggi.ToPresenterImpostazioni;
import com.example.consigliaviaggi.ToPresenterLogin;
import com.example.consigliaviaggi.ToPresenterRegistrazione;
import org.json.JSONException;
import org.json.JSONObject;

public class UtenteDAO implements PresenterToUtenteDAO {

    private ToPresenterLogin loginPresenter;
    private ToPresenterImpostazioni impostazioniPresenter;
    private ToPresenterRegistrazione registrazionePresenter;

    public UtenteDAO(ToPresenterLogin loginPresenter) { this.loginPresenter = loginPresenter; }

    public UtenteDAO(ToPresenterImpostazioni impostazioniPresenter){ this.impostazioniPresenter = impostazioniPresenter; }

    public UtenteDAO(ToPresenterRegistrazione registrazionePresenter){ this.registrazionePresenter = registrazionePresenter; }

    public void login(final String username, final String password) {

        RequestQueue queue = Volley.newRequestQueue(loginPresenter.getContext());
        String url="https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=verifica_utente&username="+username+"&password="+password;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url,null,
                response -> {

                    JSONObject result= null;

                    try {

                        result = response.getJSONObject(0);

                        if(result.getString("0").equals("Utente valido per il login.")) {
                            loginPresenter.loginResponse(1,username,password);
                            updateUserAsLogged(username,password);
                        }
                        else if(result.getString("0").equals("Utente gia' loggato."))
                            loginPresenter.loginResponse(0,username,password);
                        else
                            loginPresenter.loginResponse(-1,username,password);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            loginPresenter.showError();
        });
        queue.add(jsr);
    }

    public void updateUserAsLogged(String username, String password){

        RequestQueue queue = Volley.newRequestQueue(loginPresenter.getContext());
        String url="https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=log_in&username="+username+"&password="+password;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url,null,
                response -> {

                    JSONObject result= null;

                    try {
                        result = response.getJSONObject(0);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            loginPresenter.showError();
        });
        queue.add(jsr);
    }

    public void logout(String username, String password){

        RequestQueue queue = Volley.newRequestQueue(impostazioniPresenter.getContext());
        String url="https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=log_out&username="+username+"&password="+password;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url,null,
                response -> {

                    JSONObject result= null;

                    try {
                        result = response.getJSONObject(0);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            impostazioniPresenter.showError();
        });
        queue.add(jsr);
    }

    public void registration(final String nome, final String cognome, final String email, final String username, final String password){

        RequestQueue queue = Volley.newRequestQueue(registrazionePresenter.getContext());
        String url="https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=inserisci_utente&nome="+nome+"&cognome="+cognome+"&email="+email+"&username="+username+"&password="+password;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url,null,
                response -> {

                    JSONObject result= null;

                    try {

                        result = response.getJSONObject(0);

                        if(result.getString("0").equals("Utente gia' presente."))
                            registrazionePresenter.registrationResponse(0,nome,cognome,email,username,password);
                        else if(result.getString("0").equals("Formato email non valido."))
                            registrazionePresenter.registrationResponse(1,nome,cognome,email,username,password);
                        else if(result.getString("0").equals("Formato username non valido."))
                            registrazionePresenter.registrationResponse(2,nome,cognome,email,username,password);
                        else
                            registrazionePresenter.registrationResponse(3,nome,cognome,email,username,password);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            registrazionePresenter.showError();
        });
        queue.add(jsr);
    }
}