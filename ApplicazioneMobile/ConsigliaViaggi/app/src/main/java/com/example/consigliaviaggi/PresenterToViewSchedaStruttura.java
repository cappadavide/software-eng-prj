package com.example.consigliaviaggi;

import android.content.Context;
import com.example.consigliaviaggi.model.Foto;
import com.example.consigliaviaggi.model.Recensione;
import java.util.ArrayList;

public interface PresenterToViewSchedaStruttura {

    void setReviewsList(ArrayList<Recensione> arrayList);
    void setPhotosList(ArrayList<Foto> arrayList);
    Context getContext();
}
