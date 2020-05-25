package com.example.consigliaviaggi.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.example.consigliaviaggi.PresenterToUtenteDAO;
import com.example.consigliaviaggi.PresenterToViewImpostazioni;
import com.example.consigliaviaggi.ToPresenterImpostazioni;
import com.example.consigliaviaggi.dao.UtenteDAO;

public class ImpostazioniPresenter implements ToPresenterImpostazioni {

    private PresenterToViewImpostazioni view;
    private PresenterToUtenteDAO dao;

    public ImpostazioniPresenter(PresenterToViewImpostazioni view) {

        dao = new UtenteDAO(this);
        this.view = view;
    }

    public void logout(String username, String password){

        dao.logout(username,password);
    }

    public Context getContext(){ return view.getContext(); }

    public void setSharedPreferencesLogout(SharedPreferences sharedPreferences){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key", "LOGOUT");
        editor.apply();
    }

    public void setSharedPreferencesLogin(Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("button", "utente");
        editor.apply();
    }

    public String obtainUserBySharedPreferences(SharedPreferences sharedPreferences, int flag){

        final String username = sharedPreferences.getString("username", "default_value");
        final String password = sharedPreferences.getString("password", "default_value");
        final String key = sharedPreferences.getString("key", "default_value");

        if(flag==1) return key;
        if(flag==2) return username;
        if(flag==3) return password;

        return key;
    }
}