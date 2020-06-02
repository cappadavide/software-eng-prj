package com.example.consigliaviaggi.presenter;

import com.example.consigliaviaggi.PresenterToViewListaStrutture;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.ToPresenterListaStrutture;
import com.example.consigliaviaggi.model.Struttura;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaStrutturePresenter implements ToPresenterListaStrutture {

    private PresenterToViewListaStrutture view;

    public ListaStrutturePresenter(PresenterToViewListaStrutture view) {
        this.view = view;
    }

    @Override
    public boolean createStructureList(String[] nome, float[] rating, String[] indirizzo, String[] informazioni, int[] id, String[] copertinaUrl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine, ArrayList<Struttura> struttureArrayList){

        if(nome.length==0) return false;
        else {
            for (int i = 0; i < id.length; i++) {
                Struttura struttura = new Struttura();
                struttura.setUrl(copertinaUrl[i]);
                struttura.setInformazioni(informazioni[i]);
                struttura.setId(id[i]);
                struttura.setRating(rating[i]);
                struttura.setIndirizzo(indirizzo[i]);
                struttura.setTermine(nome[i]);
                struttura.setDa(prezzoda[i]);
                struttura.setA(prezzoa[i]);
                struttura.setLongitudine(longitudine[i]);
                struttura.setLatitudine(latitudine[i]);
                struttureArrayList.add(struttura);
            }
            return true;
        }
    }

    @Override
    public boolean createStructureList(String[] nome, float[] rating, String[] indirizzo, String[] informazioni, int[] id, String[] copertinaUrl, double[] prezzoda, double[] prezzoa, double[] longitudine, double[] latitudine, ArrayList<Struttura> struttureArrayList, int range, Point coords) {

        if(nome.length==0) return false;
        else {
            for (int i = 0; i < id.length; i++) {
                Struttura struttura = new Struttura();
                struttura.setUrl(copertinaUrl[i]);
                struttura.setInformazioni(informazioni[i]);
                struttura.setId(id[i]);
                struttura.setRating(rating[i]);
                struttura.setIndirizzo(indirizzo[i]);
                struttura.setTermine(nome[i]);
                struttura.setDa(prezzoda[i]);
                struttura.setA(prezzoa[i]);
                struttura.setLongitudine(longitudine[i]);
                struttura.setLatitudine(latitudine[i]);
                if(i==id.length-1)
                    getDistance(struttura,Point.fromLngLat(longitudine[i],latitudine[i]),coords,range,struttureArrayList,true);
                else
                    getDistance(struttura,Point.fromLngLat(longitudine[i],latitudine[i]),coords,range,struttureArrayList,false);

            }
            return true;
        }
    }

    private void getDistance(Struttura newStruttura, Point strutturaLocation,Point coords,int range,ArrayList<Struttura> list,boolean isLast){

        MapboxDirections client = MapboxDirections.builder()
                .origin(coords)
                .destination(strutturaLocation)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(view.getContext().getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                if (response.body() == null) return;
                else if (response.body().routes().size() < 1) return;

                // Get the directions route
                DirectionsRoute path=response.body().routes().get(0);
                Double distance = path.distance();
                assert distance != null;
                if(distance <=range){

                    newStruttura.setDistance(distance);
                    list.add(newStruttura);

                }
                if(isLast) view.showResults();
            }


            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {

            }
        });
    }
}