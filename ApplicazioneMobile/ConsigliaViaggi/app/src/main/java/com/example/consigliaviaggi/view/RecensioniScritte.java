package com.example.consigliaviaggi.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.consigliaviaggi.PresenterToViewRecensioniScritte;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.model.Recensione;
import com.example.consigliaviaggi.ToPresenterRecensioniScritte;
import com.example.consigliaviaggi.presenter.RecensioniScrittePresenter;
import com.example.consigliaviaggi.presenter.utils.CardAdapter;
import java.util.ArrayList;

public class RecensioniScritte extends Fragment implements PresenterToViewRecensioniScritte {

    private TextView effettuaIlLogin, nessunaRecensioneScritta;
    private ToPresenterRecensioniScritte presenter;
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private ArrayList<Recensione> recensioneArrayList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_recensioni_scritte, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View rootView, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(rootView, savedInstanceState);

        effettuaIlLogin = rootView.findViewById(R.id.listavuota);
        nessunaRecensioneScritta =rootView.findViewById(R.id.norecensioni);

        recyclerView = rootView.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recensioneArrayList = new ArrayList<>();
        adapter = new CardAdapter(getActivity(), recensioneArrayList,4);

        presenter = new RecensioniScrittePresenter(this);

        if(presenter.obtainReviewsBySharedPreferences(getActivity(),1).equals("login")) {

            effettuaIlLogin.setVisibility(View.INVISIBLE);
            presenter.obtainReviewsByUsername(presenter.obtainReviewsBySharedPreferences(getActivity(),2));
        }
        else{
            effettuaIlLogin.setVisibility(View.VISIBLE);
        }
    }

    public void switchReviewsListFromDaoToView(String[] titolo, String[] corpo, int[] rating, String[] nomeStruttura){

        recyclerView.setAdapter(adapter);

        if(presenter.createArrayListOfReviews(titolo,corpo,rating,nomeStruttura, recensioneArrayList))
            effettuaIlLogin.setVisibility(View.INVISIBLE);
        else{
            effettuaIlLogin.setVisibility(View.INVISIBLE);
            nessunaRecensioneScritta.setVisibility(View.VISIBLE);
        }
    }

    public Context getContext(){ return getActivity(); }
}