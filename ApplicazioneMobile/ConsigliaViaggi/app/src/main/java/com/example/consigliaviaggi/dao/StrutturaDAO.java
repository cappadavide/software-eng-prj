package com.example.consigliaviaggi.dao;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.consigliaviaggi.PresenterToStrutturaDAO;
import com.example.consigliaviaggi.SearchPresenter;
import org.json.JSONException;
import org.json.JSONObject;

public class StrutturaDAO implements PresenterToStrutturaDAO {

    private SearchPresenter.ToPresenterRicercaStruttura presenter;

    public StrutturaDAO(SearchPresenter.ToPresenterRicercaStruttura presenter){ this.presenter=presenter; }

    public void research(String urlString){

        RequestQueue queue = Volley.newRequestQueue(presenter.getContext());
        String url="https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=ricerca_standard&"+urlString;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url,null,
                response -> {

                    JSONObject result= null;
                    String[] nome = new String[response.length()];
                    float[] rating1 = new float[response.length()];
                    String[] indirizzo = new String[response.length()];
                    int[] id = new int[response.length()];
                    String[] informazioni = new String[response.length()];
                    String[] copertinaUrl = new String[response.length()];
                    double[] prezzoda = new double[response.length()];
                    double[] prezzoa = new double[response.length()];
                    double[] longitudine = new double[response.length()];
                    double[] latitudine = new double[response.length()];

                    try {

                        for (int i = 0; i < response.length(); i++) {

                            result = response.getJSONObject(i);
                            nome[i] = result.getString("nome");
                            rating1[i] = Float.parseFloat(result.getString("rating"));
                            indirizzo[i] = result.getString("indirizzo");
                            informazioni[i] = result.getString("informazioni");
                            id[i] = Integer.parseInt(result.getString("id"));
                            copertinaUrl[i] = result.getString("copertinaurl");
                            prezzoda[i] = Double.parseDouble(result.getString("prezzoda"));
                            prezzoa[i] = Double.parseDouble(result.getString("prezzoa"));
                            longitudine[i] = Double.parseDouble(result.getString("longitudine"));
                            latitudine[i] = Double.parseDouble(result.getString("latitudine"));
                        }
                        presenter.switchStructureListToView(nome, rating1,indirizzo,informazioni,id,copertinaUrl,prezzoda,prezzoa,longitudine,latitudine);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            Log.e("Volley","Error");
            presenter.showError();
        });
        queue.add(jsr);
    }
}