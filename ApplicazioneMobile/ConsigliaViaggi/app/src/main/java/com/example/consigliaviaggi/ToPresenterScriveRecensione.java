package com.example.consigliaviaggi;

import android.content.Context;

public interface ToPresenterScriveRecensione {

    void writeReview(String titolo, String corpo, int checked, int rating);
    Context getContext();
    void showError();
    void reviewDone();
}
