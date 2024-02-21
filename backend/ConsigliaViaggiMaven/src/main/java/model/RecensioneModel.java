package model;

import javafx.beans.property.SimpleStringProperty;

public class RecensioneModel {
    private SimpleStringProperty titolo;
    private SimpleStringProperty corpo;
    private SimpleStringProperty rating;
    private SimpleStringProperty usernamecheck;
    private SimpleStringProperty usernameutente;
    private SimpleStringProperty strutturaid;
    private SimpleStringProperty id;
    private SimpleStringProperty valutazione;

    public RecensioneModel(String utente, String struttura, String titolo, String rating, String corpo, String id){
        this.usernameutente=new SimpleStringProperty(utente);
        this.strutturaid=new SimpleStringProperty(struttura);
        this.titolo=new SimpleStringProperty(titolo);
        this.rating=new SimpleStringProperty(rating);
        this.corpo=new SimpleStringProperty(corpo);
        this.id=new SimpleStringProperty(id);
    }
    
    public String getUsernameUtente() {
        return usernameutente.get();
    }

    public SimpleStringProperty usernameUtenteProperty() {
        return usernameutente;
    }

    public void setUsernameUtente(String usernameUtente) {
        this.usernameutente.set(usernameUtente);
    }

    public String getStrutturaId() {
        return strutturaid.get();
    }

    public SimpleStringProperty strutturaIdProperty() {
        return strutturaid;
    }

    public void setStrutturaId(String strutturaid) {
        this.strutturaid.set(strutturaid);
    }

    public String getTitolo() {
        return titolo.get();
    }

    public SimpleStringProperty titoloProperty() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo.set(titolo);
    }

    public String getRating() {
        return rating.get();
    }

    public SimpleStringProperty ratingProperty() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating.set(rating);
    }

     public String getCorpo() {
        return corpo.get();
    }

    public SimpleStringProperty corpoProperty() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo.set(corpo);
    }
    
    public String getID() {
        return id.get();
    }

    public SimpleStringProperty IDProperty() {
        return id;
    }

    public void setID(String id) {
        this.id.set(id);
    }
}