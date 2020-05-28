package com.example.consigliaviaggi;

import android.app.Activity;
import android.widget.ListView;

public interface PresenterToViewRicercaMappa {
    void showSuggestionsList();
    void hideSuggestionsList();
    ListView getListView();
    void goOnMap();
    Activity getFragActivity();
}
