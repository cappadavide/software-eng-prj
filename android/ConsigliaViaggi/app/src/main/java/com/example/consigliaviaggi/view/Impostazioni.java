package com.example.consigliaviaggi.view;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.consigliaviaggi.PresenterToViewImpostazioni;
import com.example.consigliaviaggi.R;


public class Impostazioni extends Fragment implements PresenterToViewImpostazioni {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_impostazioni, container, false);



        return rootView;
    }
    @Override
    public Context getContext(){ return getActivity(); }
}
