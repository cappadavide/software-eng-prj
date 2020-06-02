package com.example.consigliaviaggi.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.consigliaviaggi.PresenterToViewScriveRecensione;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.ToPresenterScriveRecensione;
import com.example.consigliaviaggi.presenter.ScriveRecensionePresenter;
import java.util.Objects;

public class ScriveRecensione extends AppCompatActivity implements PresenterToViewScriveRecensione {

    private static boolean attivaBottoneInvio;
    private MenuItem confermaButton;
    private EditText titolo, corpo;
    private Toolbar toolbar;
    private Switch usernameSwitch;
    private RatingBar rating;
    private int numeroStelleSelezionate, usernameChecked, flag=0;
    private ToPresenterScriveRecensione presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrive_recensione);

        titolo = (EditText) findViewById(R.id.titletext);
        corpo = (EditText) findViewById(R.id.box);

        usernameSwitch = (Switch) findViewById(R.id.switch2);
        rating = (RatingBar) findViewById(R.id.ratingBar);
        rating.setStepSize((float)1.0);

        rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            flag=1;
            numeroStelleSelezionate = (int)rating;
        });
        if(flag==0) numeroStelleSelezionate=1;

        toolbar = findViewById(R.id.toolbar_orange);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Nuova recensione");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        titolo.addTextChangedListener(RecTextWatcher);
        corpo.addTextChangedListener(RecTextWatcher);

        presenter = new ScriveRecensionePresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_conferma, menu);
        confermaButton = (MenuItem) menu.findItem(R.id.action_menu_done);

        return true;
    }

    public Context getContext(){ return getApplicationContext(); }

    private TextWatcher RecTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String titleInput = titolo.getText().toString();
            String corpoInput = corpo.getText().toString();

            attivaBottoneInvio = titleInput.length() > 0 && corpoInput.length() > 0;

            invalidateOptionsMenu();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        confermaButton.setEnabled(attivaBottoneInvio);

        if(attivaBottoneInvio) confermaButton.getIcon().setAlpha(255);
        else confermaButton.getIcon().setAlpha(130);

        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_menu_done) {
            if (usernameSwitch.isChecked()) usernameChecked = 1;
            else usernameChecked = 0;

            presenter.writeReview(titolo.getText().toString().trim(), corpo.getText().toString().trim(), usernameChecked, numeroStelleSelezionate);
        }
        return true;
    }

    public void reviewDone(){

        Intent intent = new Intent(this, SchedaStruttura.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        Toast.makeText(getContext(), "La tua recensione verrà valutata il prima possibile, a presto!", Toast.LENGTH_LONG).show();
        this.finish();
    }
}