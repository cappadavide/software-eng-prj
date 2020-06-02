package com.example.consigliaviaggi.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.example.consigliaviaggi.PresenterToFotoDAO;
import com.example.consigliaviaggi.PresenterToRecensioneDAO;
import com.example.consigliaviaggi.PresenterToViewSchedaStruttura;
import com.example.consigliaviaggi.ToPresenterSchedaStruttura;
import com.example.consigliaviaggi.dao.FotoDAO;
import com.example.consigliaviaggi.dao.RecensioneDAO;
import com.example.consigliaviaggi.model.Foto;
import com.example.consigliaviaggi.model.Recensione;
import java.util.ArrayList;

public class SchedaStrutturaPresenter implements ToPresenterSchedaStruttura {

    PresenterToViewSchedaStruttura view;
    PresenterToRecensioneDAO daoRecensione;
    PresenterToFotoDAO daoFoto;

    public SchedaStrutturaPresenter(PresenterToViewSchedaStruttura view){

        this.view=view;
        daoRecensione = new RecensioneDAO(this);
        daoFoto = new FotoDAO(this);
    }

    public void obtainReviewsById(int id, int rating){ daoRecensione.obtainReviewsById(id,rating); }

    public void obtainPhotosById(int id){ daoFoto.obtainPhotosByStructureId(id); }

    public Context getContext(){ return view.getContext(); }

    public void switchPhotosArrayToView(ArrayList<Foto> fotoArrayList){ view.setPhotosList(fotoArrayList); }

    public void switchReviewsListToView(ArrayList<Recensione> recensioneArrayList){ view.setReviewsList(recensioneArrayList); }

    public String enableButtonBySharedPreferences(Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("key", "default_value");
    }

    public void setSharedPreferences(int flag, Context context, int id){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(flag==1) editor.putString("button", "struttura");
        if(flag==2) editor.putInt("id", id);
        editor.apply();
    }

    @Override
    public void showError() {
        Toast.makeText(view.getContext(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();
    }
}