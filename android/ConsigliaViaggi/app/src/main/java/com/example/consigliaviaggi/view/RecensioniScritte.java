package com.example.consigliaviaggi.view;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.consigliaviaggi.PresenterToViewRecensioniScritte;
import com.example.consigliaviaggi.R;


public class RecensioniScritte extends Fragment implements PresenterToViewRecensioniScritte {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_recensioni_scritte, container, false);
    }
    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(rootView, savedInstanceState);

    }


    @Override
    public void switchReviewsListFromDaoToView(String[] arraytitoli, String[] arraycorpo, int[] arrayrating, String[] arraystruttura) {

    }
}