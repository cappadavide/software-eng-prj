package model;

import javafx.beans.property.SimpleStringProperty;

public class StrutturaModel{
    private SimpleStringProperty nome;
    private SimpleStringProperty informazioni;
    private SimpleStringProperty sottocategoria;
    private SimpleStringProperty categoria;
    private SimpleStringProperty indirizzo;
    private SimpleStringProperty rating;
    private SimpleStringProperty idStruttura;
    private SimpleStringProperty prezzoDa;
    private SimpleStringProperty prezzoA;
    private SimpleStringProperty copertinaURL;

    public StrutturaModel(){
        
    }
    
    public StrutturaModel(String nome, String categoria, String sottocategoria, String indirizzo, String rating, String informazioni, String identificativo, String prezzoDa, String prezzoA, String copertinaURL) {
        this.nome = new SimpleStringProperty(nome);
        this.categoria = new SimpleStringProperty(categoria);
        this.sottocategoria = new SimpleStringProperty(sottocategoria);
        this.indirizzo = new SimpleStringProperty(indirizzo);
        this.rating = new SimpleStringProperty(rating);
        this.informazioni = new SimpleStringProperty(informazioni);
        this.idStruttura = new SimpleStringProperty(identificativo);
        this.prezzoDa = new SimpleStringProperty(prezzoDa);
        this.prezzoA = new SimpleStringProperty(prezzoA);
        this.copertinaURL = new SimpleStringProperty(copertinaURL);
    }

    public String getNome() {
        return nome.get();
    }

    public SimpleStringProperty nomeProperty() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome.set(nome);
    }

    public String getCategoria() {
        return categoria.get();
    }

    public SimpleStringProperty categoriaProperty() {
        return categoria;
    }

    public void setCategoria(String cetegoria) {
        this.categoria.set(cetegoria);
    }

    public String getSottocategoria() {
        return sottocategoria.get();
    }

    public SimpleStringProperty sottocategoriaProperty() {
        return sottocategoria;
    }

    public void setSottocategoria(String sottocategoria) {
        this.sottocategoria.set(sottocategoria);
    }

    public String getIndirizzo() {
        return indirizzo.get();
    }

    public SimpleStringProperty indirizzoProperty() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo.set(indirizzo);
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

    public String getInformazioni() {
        return informazioni.get();
    }

    public SimpleStringProperty informazioniProperty() {
        return informazioni;
    }

    public void setInformazioni(String informazioni) {
        this.informazioni.set(informazioni);
    }

    public String getID() {
        return idStruttura.get();
    }

    public SimpleStringProperty IDProperty() {
        return idStruttura;
    }

    public void setID(String idstruttura) {
        this.idStruttura.set(idstruttura);
    }

    public String getPrezzoDa() {
        return prezzoDa.get();
    }

    public SimpleStringProperty prezzoDaProperty() {
        return prezzoDa;
    }

    public void setPrezzoDa(String prezzoDa) {
        this.prezzoDa.set(prezzoDa);
    }

    public String getPrezzoA() {
        return prezzoA.get();
    }

    public SimpleStringProperty prezzoAProperty() {
        return prezzoA;
    }

    public void setPrezzoA(String prezzoA) {
        this.prezzoA.set(prezzoA);
    }

    public String getCopertinaURL() {
        return copertinaURL.get();
    }

    public SimpleStringProperty copertinaURLProperty() {
        return copertinaURL;
    }

    public void setCopertinaURL(String copertinaURL) {
        this.copertinaURL.set(copertinaURL);
    }

}
