package model;

import javafx.scene.control.ListCell;
import javafx.beans.property.SimpleStringProperty;

public class RecensioneModel extends ListCell<String>{
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

    public void setUsernameUtente(String overview) {
        this.usernameutente.set(overview);
    }

    public String getStrutturaId() {
        return strutturaid.get();
    }

    public SimpleStringProperty strutturaIdProperty() {
        return strutturaid;
    }

    public void setStrutturaId(String title) {
        this.strutturaid.set(title);
    }

    public String getTitolo() {
        return titolo.get();
    }

    public SimpleStringProperty titoloProperty() {
        return titolo;
    }

    public void setTitolo(String posterPath) {
        this.titolo.set(posterPath);
    }

    public String getRating() {
        return rating.get();
    }

    public SimpleStringProperty ratingProperty() {
        return rating;
    }

    public void setRating(String releaseDate) {
        this.rating.set(releaseDate);
    }

     public String getCorpo() {
        return corpo.get();
    }

    public SimpleStringProperty corpoProperty() {
        return corpo;
    }

    public void setCorpo(String releaseDate) {
        this.corpo.set(releaseDate);
    }
    
    public String getID() {
        return id.get();
    }

    public SimpleStringProperty IDProperty() {
        return id;
    }

    public void setID(String releaseDate) {
        this.id.set(releaseDate);
    }
}