package com.example.consigliaviaggi.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.example.consigliaviaggi.PresenterToViewLogin;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.ToPresenterLogin;
import com.example.consigliaviaggi.presenter.LoginPresenter;

import java.util.Objects;

public class Login extends AppCompatActivity implements View.OnClickListener, PresenterToViewLogin {

    private Button accediButton, iscrivitButton;
    private EditText password, username;
    private TextView utenteGiaLoggato, credenzialiNonValide;
    private Toolbar toolbar;
    private ToPresenterLogin presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar= findViewById(R.id.toolbar_orange);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        credenzialiNonValide = (TextView)findViewById(R.id.invalid);
        utenteGiaLoggato = (TextView)findViewById(R.id.loggato);

        accediButton = (Button) findViewById(R.id.accedibtn);
        iscrivitButton = (Button) findViewById(R.id.iscrivitibtn);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        username.addTextChangedListener(loginTextWatcher);
        password.addTextChangedListener(loginTextWatcher);

        accediButton.setOnClickListener(this);
        iscrivitButton.setOnClickListener(this);

        presenter = new LoginPresenter(this);

        toolbar.setNavigationOnClickListener(v -> finish());

    }

    public Context getContext(){
        return getApplicationContext();
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String usernameInput = username.getText().toString();
            String passwordInput = password.getText().toString();
            accediButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void loginResponse(int loginSuccess) {


        if (loginSuccess==1) {

            presenter.setUserAsLogged(true,this);

            if(presenter.obtainSharedPreferencesToFilterActivity(this).equals("utente")) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
            }
            else if(presenter.obtainSharedPreferencesToFilterActivity(this).equals("struttura")){
                Intent intent = new Intent(this, SchedaStruttura.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                this.finish();
            }
        }
        else if (loginSuccess==0){

            presenter.setUserAsLogged(false,this);
            credenzialiNonValide.setVisibility(View.INVISIBLE);
            utenteGiaLoggato.setVisibility(View.VISIBLE);
        }
        else{

            presenter.setUserAsLogged(false,this);
            utenteGiaLoggato.setVisibility(View.INVISIBLE);
            credenzialiNonValide.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view){

        switch(view.getId()) {

            case R.id.accedibtn:
                presenter.login(username.getText().toString().trim(), password.getText().toString().trim());
                break;

            case R.id.iscrivitibtn:
                Intent registrazione = new Intent(this, Registrazione.class);
                startActivity(registrazione);
                this.finish();
                break;
        }
    }
}