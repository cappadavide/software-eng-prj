package com.example.consigliaviaggi.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.example.consigliaviaggi.PresenterToUtenteDAO;
import com.example.consigliaviaggi.PresenterToViewLogin;
import com.example.consigliaviaggi.ToPresenterLogin;
import com.example.consigliaviaggi.model.Utente;
import com.example.consigliaviaggi.dao.UtenteDAO;

public class LoginPresenter implements ToPresenterLogin {

    private PresenterToViewLogin view;
    private PresenterToUtenteDAO dao;

    public LoginPresenter(PresenterToViewLogin view) {

        this.view = view;
        dao = new UtenteDAO(this);
    }

    public void login(String username, String password){

        dao.login(username,password);
    }

    public String obtainSharedPreferencesToFilterActivity(Context context){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString("button", "default_value");
    }

    public void setUserAsLogged(Boolean isLogged, Context context){

        if(isLogged) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", Utente.getUsername());
            editor.putString("password", Utente.getPassword());
            editor.putString("key", "LOGIN");
            editor.apply();
        }

        Utente.setLogged(isLogged);
    }

    public void loginResponse(int loginSuccess, String username, String password) {

        if(loginSuccess==1){

            Utente.setUsername(username);
            Utente.setPassword(password);

        }
        view.loginResponse(loginSuccess);
    }

    @Override
    public void showError() {
        Toast.makeText(view.getContext(), "Controllare lo stato della connessione", Toast.LENGTH_LONG).show();
    }

    public Context getContext(){ return view.getContext(); }
}