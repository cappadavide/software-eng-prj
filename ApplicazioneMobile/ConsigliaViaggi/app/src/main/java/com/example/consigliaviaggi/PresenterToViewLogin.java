package com.example.consigliaviaggi;

import android.content.Context;

public interface PresenterToViewLogin {

    void loginResponse(int loginSuccess);
    Context getContext();
}
