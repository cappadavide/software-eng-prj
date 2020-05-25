package com.example.consigliaviaggi;

import android.content.Context;

public interface ToPresenterRegistrazione {

    void registration(String nome, String cognome, String email, String username, String password);
    Context getContext();
    void showError();
    void registrationResponse(int response, String nome, String cognome, String email, String username, String password);
}
