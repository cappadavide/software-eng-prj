package com.example.consigliaviaggi.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.consigliaviaggi.PresenterToViewRegistrazione;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.ToPresenterRegistrazione;
import com.example.consigliaviaggi.presenter.RegistrazionePresenter;
import java.util.Objects;

public class Registrazione extends AppCompatActivity implements View.OnClickListener, PresenterToViewRegistrazione {

    private Button iscrivitiButton;
    private EditText password, username, email, nome, cognome;
    private Toolbar toolbar;
    private TextView utenteGiaPresente, formatoEmailInvalido, formatoUsernameNonValido;
    private CheckBox privacyCheckBox;
    private ToPresenterRegistrazione presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        toolbar = findViewById(R.id.toolbar_orange);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registrazione");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        utenteGiaPresente = (TextView) findViewById(R.id.utenteinvalid);
        formatoEmailInvalido = (TextView) findViewById(R.id.emailinvalid);
        formatoUsernameNonValido = (TextView) findViewById(R.id.usernameinvalid);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        nome = (EditText) findViewById(R.id.nome);
        cognome = (EditText) findViewById(R.id.cognome);
        email = (EditText) findViewById(R.id.email);

        iscrivitiButton = (Button) findViewById(R.id.button_iscriviti);
        iscrivitiButton.setOnClickListener(this);

        username.addTextChangedListener(regTextWatcher);
        password.addTextChangedListener(regTextWatcher);
        nome.addTextChangedListener(regTextWatcher);
        cognome.addTextChangedListener(regTextWatcher);
        email.addTextChangedListener(regTextWatcher);

        privacyCheckBox = (CheckBox) findViewById(R.id.checkBox);

        presenter = new RegistrazionePresenter(this);

        privacyCheckBox.setOnClickListener(v -> {

            String usernameInput = username.getText().toString();
            String passwordInput = password.getText().toString();
            String nomeInput = nome.getText().toString();
            String cognomeInput = cognome.getText().toString();
            String emailInput = email.getText().toString();
            iscrivitiButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() &&
                    !nomeInput.isEmpty() && !cognomeInput.isEmpty() && !emailInput.isEmpty() && privacyCheckBox.isChecked());
        });

    }

    private TextWatcher regTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String usernameInput = username.getText().toString();
            String passwordInput = password.getText().toString();
            String nomeInput = nome.getText().toString();
            String cognomeInput = cognome.getText().toString();
            String emailInput = email.getText().toString();
            iscrivitiButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() &&
                    !nomeInput.isEmpty() && !cognomeInput.isEmpty() && !emailInput.isEmpty() && privacyCheckBox.isChecked());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public Context getContext(){
        return getApplicationContext();
    }

    public void registrationResponse(int response){

        if (response==0) {

            formatoUsernameNonValido.setVisibility(View.INVISIBLE);
            formatoEmailInvalido.setVisibility(View.INVISIBLE);
            utenteGiaPresente.setVisibility(View.VISIBLE);
        }
        else if (response==1) {

            formatoUsernameNonValido.setVisibility(View.INVISIBLE);
            utenteGiaPresente.setVisibility(View.INVISIBLE);
            formatoEmailInvalido.setVisibility(View.VISIBLE);
        }
        else if (response==2) {

            utenteGiaPresente.setVisibility(View.INVISIBLE);
            formatoEmailInvalido.setVisibility(View.INVISIBLE);
            formatoUsernameNonValido.setVisibility(View.VISIBLE);
        }
        else{

            Intent form = new Intent(this, Login.class);
            startActivity(form);
            this.finish();
        }
    }

    @Override
    public void onClick(View view){

        presenter.registration(nome.getText().toString().trim(), cognome.getText().toString().trim(),
                email.getText().toString().trim(), username.getText().toString().trim(), password.getText().toString().trim());
    }
}