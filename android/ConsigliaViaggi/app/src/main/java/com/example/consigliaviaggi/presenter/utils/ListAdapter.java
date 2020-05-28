package com.example.consigliaviaggi.presenter.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.consigliaviaggi.R;
import java.util.ArrayList;

/**
 * Classe custom per gestione di ListView.
 * */
public class ListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> list;

    public ListAdapter(Activity context, ArrayList<String> list) {
        super(context, R.layout.mylist, list);
        this.context=context;
        this.list=list;

    }
    @Override
    public int getCount () {
        return list.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;
        String text;

        if(view==null) {

            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.mylist, null, true);
        }
        if(list.size()>0) {

            text = list.get(position);
            if (text != null) {
                TextView titleText = (TextView) view.findViewById(R.id.title1);
                titleText.setText(text);
            }
        }
        return view;
    }
}
