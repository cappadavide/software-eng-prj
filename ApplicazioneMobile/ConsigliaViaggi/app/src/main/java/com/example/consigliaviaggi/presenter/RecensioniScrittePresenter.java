package com.example.consigliaviaggi.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.example.consigliaviaggi.PresenterToRecensioneDAO;
import com.example.consigliaviaggi.PresenterToViewRecensioniScritte;
import com.example.consigliaviaggi.model.Recensione;
import com.example.consigliaviaggi.ToPresenterRecensioniScritte;
import com.example.consigliaviaggi.dao.RecensioneDAO;
import java.util.ArrayList;

public class RecensioniScrittePresenter implements ToPresenterRecensioniScritte {

    private PresenterToViewRecensioniScritte view;
    private PresenterToRecensioneDAO dao;

    public RecensioniScrittePresenter(PresenterToViewRecensioniScritte view){

        this.view=view;
        dao = new RecensioneDAO(this);
    }

    public Context getContext(){ return view.getContext(); }

    public void obtainReviewsByUsername(String username){ dao.obtainReviewsByUsername(username); }

    public void switchReviewsListFromDaoToView(String[] titolo, String[] corpo, int[] rating, String[] nomeStruttura){

        view.switchReviewsListFromDaoToView(titolo,corpo,rating,nomeStruttura);
    }

    @Override
    public void showError() {
        Toast.makeText(view.getContext(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();
    }

    public boolean createArrayListOfReviews(String[] titolo, String[] corpo, int[] rating, String[] nomeStruttura, ArrayList<Recensione> recensioneArrayList){

        if(titolo.length==0) return false;
        else {

            for (int i = 0; i < titolo.length; i++) {

                Recensione recensione = new Recensione();
                recensione.setTitolo(titolo[i]);
                recensione.setCorpo(corpo[i]);
                recensione.setRating(rating[i]);
                recensione.setNomeStruttura(nomeStruttura[i]);
                recensioneArrayList.add(recensione);
            }
            return true;
        }
    }

    public String obtainReviewsBySharedPreferences(Context context, int flag){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String username = sharedPreferences.getString("username", "default_value");
        final String key = sharedPreferences.getString("key", "default_value");

        if(flag==1) return key;
        if(flag==2) return username;

        return key;
    }
}