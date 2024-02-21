package com.example.consigliaviaggi.model;

import androidx.annotation.NonNull;

public class Recensione {

    private String titolo, corpo, usernameUtente, nomeStruttura, nomeUtente, cognomeUtente;
    private int rating, usenameCheck;

    public String getNomeStruttura() { return nomeStruttura; }

    public void setNomeStruttura(String nomeStruttura) { this.nomeStruttura = nomeStruttura; }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public String getUsernameUtente() {
        return usernameUtente;
    }

    public void setUsernameUtente(String usernameUtente) {
        this.usernameUtente = usernameUtente;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getUsenameCheck() {
        return usenameCheck;
    }

    public void setUsenameCheck(int usenameCheck) {
        this.usenameCheck = usenameCheck;
    }

    @NonNull
    @Override
    public String toString() {
        return "Titolo:"+titolo+"\nCorpo:"+corpo+"\n----\n";
    }
}