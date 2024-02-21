package com.example.consigliaviaggi.model;

public class Struttura {

    private String localita, termine, sottocategoria, categoria, indirizzo, url, informazioni;
    private double da, a, longitudine, latitudine;
    private Double distance;
    private int id;
    private float rating;

    public double getLongitudine() { return longitudine; }

    public void setLongitudine(double longitudine) { this.longitudine = longitudine; }

    public double getLatitudine() { return latitudine; }

    public void setLatitudine(double latitudine) { this.latitudine = latitudine; }

    public String getInformazioni() {
        return informazioni;
    }

    public void setInformazioni(String informazioni) {
        this.informazioni = informazioni;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalita() {
        return localita;
    }

    public void setLocalita(String localita) {
        this.localita = localita;
    }

    public String getTermine() {
        return termine;
    }

    public void setTermine(String termine) {
        this.termine = termine;
    }

    public String getSottocategoria() {
        return sottocategoria;
    }

    public void setSottocategoria(String sottocategoria) {
        this.sottocategoria = sottocategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public double getDa() {
        return da;
    }

    public void setDa(double da) {
        this.da = da;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getDistance(){return distance;}

    public void setDistance(Double distance){this.distance=distance;}
}