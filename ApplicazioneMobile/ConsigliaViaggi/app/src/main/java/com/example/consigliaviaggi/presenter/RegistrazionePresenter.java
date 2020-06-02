package com.example.consigliaviaggi.presenter;

import android.content.Context;
import android.widget.Toast;
import com.example.consigliaviaggi.PresenterToUtenteDAO;
import com.example.consigliaviaggi.PresenterToViewRegistrazione;
import com.example.consigliaviaggi.ToPresenterRegistrazione;
import com.example.consigliaviaggi.model.Utente;
import com.example.consigliaviaggi.dao.UtenteDAO;

public class RegistrazionePresenter implements ToPresenterRegistrazione {

    private PresenterToViewRegistrazione view;
    private PresenterToUtenteDAO dao;

    public RegistrazionePresenter(PresenterToViewRegistrazione view) {

        this.view = view;
        dao = new UtenteDAO(this);
    }

    public void registration(String nome, String cognome, String email, String username, String password){

        dao.registration(nome,cognome,email,username,password);
    }

    public Context getContext() { return view.getContext(); }

    public void registrationResponse(int response, String nome, String cognome, String email, String username, String password){

        if(response==3){

            Utente.setNome(nome);
            Utente.setCognome(cognome);
            Utente.setEmail(email);
            Utente.setUsername(username);
            Utente.setPassword(password);
        }
        view.registrationResponse(response);
    }

    @Override
    public void showError() {
        Toast.makeText(view.getContext(), "Controllare lo stato della connessione.", Toast.LENGTH_LONG).show();
    }
}