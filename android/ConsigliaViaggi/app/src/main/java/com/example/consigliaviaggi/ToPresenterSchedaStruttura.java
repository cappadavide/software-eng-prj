package com.example.consigliaviaggi;

import android.content.Context;
import com.example.consigliaviaggi.model.Foto;
import com.example.consigliaviaggi.model.Recensione;
import java.util.ArrayList;

public interface ToPresenterSchedaStruttura {

    void obtainReviewsById(int id, int rating);
    void obtainPhotosById(int id);
    void switchReviewsListToView(ArrayList<Recensione> arrayList);
    void switchPhotosArrayToView(ArrayList<Foto> arrayList);
    Context getContext();
    void showError();
    String enableButtonBySharedPreferences(Context context);
    void setSharedPreferences(int flag, Context context, int id);
}
