package com.example.consigliaviaggi;

import android.content.Context;

public interface ToPresenterLogin {

    void login(String username, String password);
    void loginResponse(int loginSuccess, String username, String password);
    Context getContext();
    void setUserAsLogged(Boolean isLogged, Context context);
    String obtainSharedPreferencesToFilterActivity(Context context);
    void showError();
}
