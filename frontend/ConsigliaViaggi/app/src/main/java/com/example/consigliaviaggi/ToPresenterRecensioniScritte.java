package com.example.consigliaviaggi;

import android.content.Context;
import com.example.consigliaviaggi.model.Recensione;
import java.util.ArrayList;

public interface ToPresenterRecensioniScritte {

    void obtainReviewsByUsername(String username);
    Context getContext();
    void showError();
    String obtainReviewsBySharedPreferences(Context context, int flag);
    void switchReviewsListFromDaoToView(String[] arraytitoli, String[] arraycorpo, int[] arrayrating, String[] arraystruttura);
    boolean createArrayListOfReviews(String[] arraytitoli, String[] arraycorpo, int[] arrayrating, String[] arraystruttura, ArrayList<Recensione> reviewArrayList);
}
