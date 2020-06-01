package com.example.consigliaviaggi.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.consigliaviaggi.R;

public class HomeView extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_homeview, container, false);

        Button alberghiButton = rootView.findViewById(R.id.alberghi);
        alberghiButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RicercaAlberghi.class);
            startActivity(intent);
        });

        Button ristorantiButton = rootView.findViewById(R.id.ristoranti);
        ristorantiButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RicercaRistoranti.class);
            startActivity(intent);
        });

        Button attrazioniButton = rootView.findViewById(R.id.attrazioni);
        attrazioniButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), RicercaAttrazioni.class);
            startActivity(intent);
        });

        return rootView;
    }
}