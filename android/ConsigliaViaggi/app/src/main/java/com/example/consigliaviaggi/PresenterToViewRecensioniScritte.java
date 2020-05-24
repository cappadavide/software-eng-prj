package com.example.consigliaviaggi;

import android.content.Context;

public interface PresenterToViewRecensioniScritte{

    Context getContext();
    void switchReviewsListFromDaoToView(String[] arraytitoli, String[] arraycorpo, int[] arrayrating, String[] arraystruttura);
}
