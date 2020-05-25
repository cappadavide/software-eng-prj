package com.example.consigliaviaggi;

import android.content.Context;
import android.content.SharedPreferences;

public interface ToPresenterImpostazioni {

    void logout(String username, String password);
    Context getContext();
    void setSharedPreferencesLogin(Context context);
    String obtainUserBySharedPreferences(SharedPreferences sharedPreferences, int flag);
    void setSharedPreferencesLogout(SharedPreferences sharedPreferences);
}

