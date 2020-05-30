package com.example.consigliaviaggi.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.example.consigliaviaggi.PresenterToRecensioneDAO;
import com.example.consigliaviaggi.PresenterToViewScriveRecensione;
import com.example.consigliaviaggi.ToPresenterScriveRecensione;
import com.example.consigliaviaggi.dao.RecensioneDAO;

public class ScriveRecensionePresenter implements ToPresenterScriveRecensione {

    private PresenterToViewScriveRecensione view;
    private PresenterToRecensioneDAO dao;

    public ScriveRecensionePresenter(PresenterToViewScriveRecensione view){

        this.view=view;
        dao = new RecensioneDAO(this);
    }

    public void writeReview(String titolo, String corpo, int usernameCheck, int rating){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final String username = sharedPreferences.getString("username", "default_value");

        dao.writeReview(titolo,corpo,usernameCheck,rating,username);
    }

    public Context getContext(){ return view.getContext(); }

    public void reviewDone(){ view.reviewDone(); }

    @Override
    public void showError() {
        Toast.makeText(view.getContext(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();
    }
}