package model;

import javafx.scene.control.ListCell;
import javafx.beans.property.SimpleStringProperty;

public class StrutturaModel extends ListCell<String> {
    private SimpleStringProperty nome;
    private SimpleStringProperty informazioni;
    private SimpleStringProperty sottocategoria;
    private SimpleStringProperty categoria;
    private SimpleStringProperty indirizzo;
    private SimpleStringProperty rating;

    public StrutturaModel(String nome, String sottocategoria, String indirizzo, String rating, String informazioni) {
        this.nome = new SimpleStringProperty(nome);
        this.sottocategoria = new SimpleStringProperty(sottocategoria);
        this.indirizzo = new SimpleStringProperty(indirizzo);
        this.rating = new SimpleStringProperty(rating);
        this.informazioni = new SimpleStringProperty(informazioni);
    }

    public String getNome() {
        return nome.get();
    }

    public SimpleStringProperty nomeProperty() {
        return nome;
    }

    public void setNome(String overview) {
        this.nome.set(overview);
    }
    
    public String getCategoria() {
        return sottocategoria.get();
    }

    public SimpleStringProperty categoriaProperty() {
        return sottocategoria;
    }

    public void setCategoria(String overview) {
        this.sottocategoria.set(overview);
    }
    
    public String getSottocategoria() {
        return sottocategoria.get();
    }

    public SimpleStringProperty sottocategoriaProperty() {
        return sottocategoria;
    }

    public void setSottocategoria(String overview) {
        this.sottocategoria.set(overview);
    }
    
    public String getIndirizzo() {
        return indirizzo.get();
    }

    public SimpleStringProperty indirizzoProperty() {
        return indirizzo;
    }

    public void setIndirizzo(String overview) {
        this.indirizzo.set(overview);
    }
    
    public String getRating() {
        return rating.get();
    }

    public SimpleStringProperty ratingProperty() {
        return rating;
    }

    public void setRating(String overview) {
        this.rating.set(overview);
    }
    
    public String getInformazioni() {
        return informazioni.get();
    }

    public SimpleStringProperty informazioniProperty() {
        return informazioni;
    }

    public void setInformazioni(String overview) {
        this.informazioni.set(overview);
    }
}
