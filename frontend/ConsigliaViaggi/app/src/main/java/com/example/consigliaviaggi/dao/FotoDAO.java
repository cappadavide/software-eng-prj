package com.example.consigliaviaggi.dao;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.consigliaviaggi.PresenterToFotoDAO;
import com.example.consigliaviaggi.ToPresenterSchedaStruttura;
import com.example.consigliaviaggi.model.Foto;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class FotoDAO implements PresenterToFotoDAO {

    private ToPresenterSchedaStruttura presenter;

    public FotoDAO(ToPresenterSchedaStruttura presenter){ this.presenter=presenter; }

    public void obtainPhotosByStructureId(int id){

        RequestQueue queue  = Volley.newRequestQueue(presenter.getContext());
        String url="https://q8rvy0t9c8.execute-api.eu-west-3.amazonaws.com/test/database?op=visualizza_foto_struttura&strutturaid="+id;
        JsonArrayRequest jsr = new JsonArrayRequest(Request.Method.GET, url,null,
                response -> {

                    JSONObject result= null;
                    String[] url1 = new String[response.length()];
                    int[] strutturaId = new int[response.length()];
                    ArrayList<Foto> fotoArrayList = new ArrayList<>();

                    try {

                        for (int i = 0; i < response.length(); i++) {

                            result = response.getJSONObject(i);
                            url1[i] = result.getString("url");
                            strutturaId[i] = Integer.parseInt(result.getString(("strutturaid")));

                            Foto foto = new Foto();
                            foto.setUrl(url1[i]);
                            foto.setStrutturaid(strutturaId[i]);

                            fotoArrayList.add(foto);
                        }
                        presenter.switchPhotosArrayToView(fotoArrayList);
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
