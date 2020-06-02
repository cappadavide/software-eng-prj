package com.example.consigliaviaggi.view;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.consigliaviaggi.PresenterToViewImpostazioni;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.ToPresenterImpostazioni;
import com.example.consigliaviaggi.presenter.ImpostazioniPresenter;
import java.util.Objects;

public class Impostazioni extends Fragment implements PresenterToViewImpostazioni {

    private TextView informazioniApplicazione;
    private TextView loginButton, logoutButton;
    private ToPresenterImpostazioni presenter;
    private SharedPreferences sharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_impostazioni, container, false);

        logoutButton = rootView.findViewById(R.id.logout);
        loginButton = rootView.findViewById(R.id.accedi);
        informazioniApplicazione = rootView.findViewById(R.id.info);

        presenter = new ImpostazioniPresenter(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (presenter.obtainUserBySharedPreferences(sharedPreferences,1).equals("login")) {

            loginButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        }
        else {

            logoutButton.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
        }

        informazioniApplicazione.setOnClickListener(view -> startActivity(new Intent(getActivity(), Informazioni.class)));

        loginButton.setOnClickListener(view -> {
            presenter.setSharedPreferencesLogin(getActivity());
            startActivity(new Intent(getActivity(), Login.class));
        });

        logoutButton.setOnClickListener(view -> {

            presenter.setSharedPreferencesLogout(sharedPreferences,1);

            presenter.logout(presenter.obtainUserBySharedPreferences(sharedPreferences,2),presenter.obtainUserBySharedPreferences(sharedPreferences,3));

            startActivity(new Intent(getActivity(), MainActivity.class));
            Objects.requireNonNull(getActivity()).finish();
        });

        return rootView;
    }

    public void notMakeLogout(){
        presenter.setSharedPreferencesLogout(sharedPreferences,2);
    }

    public Context getContext(){ return getActivity(); }
}