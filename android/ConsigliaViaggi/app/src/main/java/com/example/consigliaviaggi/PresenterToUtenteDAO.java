package com.example.consigliaviaggi;

public interface PresenterToUtenteDAO {

    void login(String username, String password);
    void logout(String username, String password);
    void registration(String nome, String cognome, String email, String username, String password);
}