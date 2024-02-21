package com.example.consigliaviaggi;

import com.example.consigliaviaggi.model.Struttura;
import com.mapbox.geojson.Point;
import java.util.ArrayList;

public interface ToPresenterListaStrutture {

    boolean createStructureList(String[] nome, float[] rating, String[] indirizzo, String[] informazioni, int[] id, String[] copertinaUrl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine, ArrayList<Struttura> struttureArrayList);
    boolean createStructureList(String[] nome, float[] rating, String[] indirizzo, String[] informazioni, int[] id, String[] copertinaUrl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine, ArrayList<Struttura> struttureArrayList, int range, Point coords);
}
