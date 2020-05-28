package com.example.consigliaviaggi.presenter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.consigliaviaggi.R;
import com.example.consigliaviaggi.model.Foto;
import com.example.consigliaviaggi.model.Recensione;
import com.example.consigliaviaggi.model.Struttura;
import com.example.consigliaviaggi.view.ListaStrutture;
import com.example.consigliaviaggi.view.SchedaRecensione;
import com.example.consigliaviaggi.view.SchedaStruttura;
import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.structureHolder> {

    private Context context;
    private ArrayList<Struttura> strutturaArrayList;
    private ArrayList<Foto> fotoArrayList;
    private ArrayList<Recensione> recensioneArrayList, recensioneStrutturaArrayList;
    private int flag;

    public CardAdapter(Context context, ArrayList<?> arrayList, int flag) {

        this.context = context;
        this.flag = flag;
        if(flag==1)this.strutturaArrayList = (ArrayList<Struttura>) arrayList;
        if(flag==2)this.fotoArrayList = (ArrayList<Foto>) arrayList;
        if(flag==3)this.recensioneStrutturaArrayList = (ArrayList<Recensione>) arrayList;
        if(flag==4)this.recensioneArrayList = (ArrayList<Recensione>) arrayList;
    }

    @NonNull
    @Override
    public CardAdapter.structureHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = null;

        if(flag==1)view = LayoutInflater.from(context).inflate(R.layout.row_struttura, parent, false);
        if(flag==2)view = LayoutInflater.from(context).inflate(R.layout.row_fotostruttura, parent, false);
        if(flag==3)view = LayoutInflater.from(context).inflate(R.layout.row_recensionestruttura, parent, false);
        if(flag==4)view = LayoutInflater.from(context).inflate(R.layout.row_recensione, parent, false);

        return new CardAdapter.structureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.structureHolder holder, int position) {

        if(flag==1){

            Struttura struttura = strutturaArrayList.get(position);
            holder.setDetails(struttura);
        }
        if(flag==2){

            Foto foto = fotoArrayList.get(position);
            holder.setDetails(foto);
        }
        if(flag==3){

            Recensione recensione = recensioneStrutturaArrayList.get(position);
            holder.setDetails(recensione);
        }
        if(flag==4){

            Recensione recensione = recensioneArrayList.get(position);
            holder.setDetails(recensione);
        }
    }

    @Override
    public int getItemCount() {

        if(flag==1)return strutturaArrayList.size();
        if(flag==2)return fotoArrayList.size();
        if(flag==3)return recensioneStrutturaArrayList.size();
        if(flag==4)return recensioneArrayList.size();
        return 0;
    }

    class structureHolder extends RecyclerView.ViewHolder {

        private TextView strutturaNome, indirizzo, titolo, corpo, usernameUtente, nomeStruttura, prezzo;
        private ImageView copertina, immagineDaVisualizzare;
        private RatingBar rating;
        private Button frecciaButton;
        private CardView card;

        structureHolder(View itemView) {

            super(itemView);

            if(flag==1) {

                strutturaNome = itemView.findViewById(R.id.strutturanome);
                indirizzo = itemView.findViewById(R.id.indirizzo);
                rating = itemView.findViewById(R.id.ratingrow);
                copertina = itemView.findViewById(R.id.image);
                prezzo = itemView.findViewById(R.id.prezzo);
                card = itemView.findViewById(R.id.card);
                copertina.setClipToOutline(true);
            }
            if(flag==2) immagineDaVisualizzare = itemView.findViewById(R.id.imagerow);

            if(flag==3){

                titolo = itemView.findViewById(R.id.titlerow);
                corpo = itemView.findViewById(R.id.bodyrow);
                rating = itemView.findViewById(R.id.ratingrow);
                usernameUtente = itemView.findViewById(R.id.user);
            }

            if(flag==4){

                titolo = itemView.findViewById(R.id.titlerow);
                corpo = itemView.findViewById(R.id.bodyrow);
                rating = itemView.findViewById(R.id.ratingrow);
                nomeStruttura = itemView.findViewById(R.id.struttura);
                frecciaButton = itemView.findViewById(R.id.frecciarece);
            }
        }

        void setDetails(Foto foto){

            Glide
                    .with(context)
                    .load(foto.getUrl())
                    .centerCrop()
                    .into(immagineDaVisualizzare);
        }

        @SuppressLint("SetTextI18n")
        void setDetails(Recensione recensione) {

            if (flag==3){

                titolo.setText(recensione.getTitolo());
                corpo.setText(recensione.getCorpo());
                rating.setRating(recensione.getRating());

                if (recensione.getUsenameCheck() == 1) usernameUtente.setText(recensione.getUsernameUtente());
                else usernameUtente.setText(recensione.getNomeUtente() + " " + recensione.getCognomeUtente());
            }

            if(flag==4){

                titolo.setText(recensione.getTitolo());
                corpo.setText(recensione.getCorpo());
                rating.setRating(recensione.getRating());
                nomeStruttura.setText(recensione.getNomeStruttura());

                final Bundle bundle = new Bundle();
                bundle.putString("struttura", nomeStruttura.getText().toString());
                bundle.putString("bodyrow", corpo.getText().toString());
                bundle.putInt("rating", (int)rating.getRating());

                frecciaButton.setOnClickListener(view -> view.getContext().startActivity(new Intent(view.getContext(), SchedaRecensione.class).putExtras(bundle)));
            }
        }

        @SuppressLint("SetTextI18n")
        void setDetails(Struttura struttura) {

            strutturaNome.setText(struttura.getTermine());
            rating.setRating((float) struttura.getRating());
            indirizzo.setText(struttura.getIndirizzo());
            prezzo.setText(struttura.getDa()+" - "+struttura.getA()+"€");

            Glide
                    .with(context)
                    .load(struttura.getUrl())
                    .centerCrop()
                    .into(copertina);

            card.setOnClickListener(view -> {

                final Bundle bundle = new Bundle();
                bundle.putString("titolo",struttura.getTermine());
                bundle.putString("informazioni", struttura.getInformazioni());
                bundle.putString("indirizzo",struttura.getIndirizzo());
                bundle.putInt("id",struttura.getId());
                bundle.putDouble("longitudine", struttura.getLongitudine());
                bundle.putDouble("latitudine", struttura.getLatitudine());
                bundle.putDouble("rating", rating.getRating());
                bundle.putDouble("prezzoda", struttura.getDa());
                bundle.putDouble("prezzoa", struttura.getA());

                card.getContext().startActivity(new Intent(card.getContext(), SchedaStruttura.class).putExtras(bundle));
            });
        }
    }
}

