package com.example.lezzetkitapkullanici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lezzetkitapkullanici.Arayuz.ItemClickListener;
import com.example.lezzetkitapkullanici.Model.Kategori;
import com.example.lezzetkitapkullanici.Model.Tur;
import com.example.lezzetkitapkullanici.ViewHolder.KategoriViewHolder;
import com.example.lezzetkitapkullanici.ViewHolder.TurViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TurlerActivity extends AppCompatActivity {
    //firebase tanımı
    FirebaseDatabase database;
    DatabaseReference TurYol;
    FirebaseStorage storage;
    StorageReference resimYolu;
    FirebaseRecyclerAdapter<Tur, TurViewHolder> adapter;
    RecyclerView recyler_tur;
    RecyclerView.LayoutManager layoutManager;

    Tur yeniTur;
    String kategoriId="";

    FirebaseRecyclerAdapter<Tur,TurViewHolder>aramaAdapter;
    List<String> onerListe=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turler);

        database = FirebaseDatabase.getInstance();
        TurYol = database.getReference("Tur");
        storage = FirebaseStorage.getInstance();
        resimYolu = storage.getReference();
        recyler_tur=findViewById(R.id.rv_turler);
        recyler_tur.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyler_tur.setLayoutManager(layoutManager);


        if(getIntent()!=null){
            kategoriId=getIntent().getStringExtra("KategoriId");
        }
        if(!kategoriId.isEmpty()) {
            turleriYukle(kategoriId);
        }


            materialSearchBar=findViewById(R.id.aramaTur);
            materialSearchBar.setHint("Aradığınız Türü Giriniz...");

            oneriYukle();
            materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String>oneri=new ArrayList<String>();
                for(String arama:onerListe){
                    if(arama.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))oneri.add(arama);
                }
                materialSearchBar.setLastSuggestions(oneri);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    recyler_tur.setAdapter(adapter);
                }
            }
            @Override
            public void onSearchConfirmed(CharSequence text) {
                aramayaBasla(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void aramayaBasla(CharSequence text) {
        Query  adagoreAramasorgu=TurYol.orderByChild("ad").equalTo(text.toString());
        FirebaseRecyclerOptions<Tur>turSecenekleri=new FirebaseRecyclerOptions.Builder<Tur>().setQuery(adagoreAramasorgu,Tur.class).build();

        aramaAdapter=new FirebaseRecyclerAdapter<Tur,TurViewHolder>(turSecenekleri) {
            @Override
            protected void onBindViewHolder(@NonNull TurViewHolder turViewHolder, int i, @NonNull Tur tur) {
                turViewHolder.txtTurAdi.setText(tur.getAd());
                Picasso.with(getBaseContext()).load(tur.getResim()).into(turViewHolder.imageView);

                Tur lokal=tur;
                turViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent turler=new Intent(TurlerActivity.this,YemeklerActivity.class) ;
                        turler.putExtra("TurId",aramaAdapter.getRef(position).getKey());
                        startActivity(turler);
                    }
                });
            }

            @NonNull
            @Override
            public TurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.tur_satiri_ogesi,parent,false);
                return new TurViewHolder(itemView);

            }
        };
        aramaAdapter.startListening();
        recyler_tur.setAdapter(aramaAdapter);
    }

    private void oneriYukle() {
        TurYol.orderByChild("ad").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot:snapshot.getChildren()){
                    Tur item=postSnapshot.getValue(Tur.class);
                    onerListe.add(item.getAd());
                }
                materialSearchBar.setLastSuggestions(onerListe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void turleriYukle(String kategoriId) {
        Query filtrele=TurYol.orderByChild("kategoriid").equalTo(kategoriId);
        FirebaseRecyclerOptions<Tur> secenekler=new FirebaseRecyclerOptions.Builder<Tur>().setQuery(filtrele,Tur.class).build();
        adapter=new FirebaseRecyclerAdapter<Tur, TurViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull TurViewHolder turViewHolder, int i, @NonNull Tur tur) {
                turViewHolder.txtTurAdi.setText(tur.getAd());
                Picasso.with(getBaseContext()).load(tur.getResim()).into(turViewHolder.imageView);
                final Tur turtikla=tur;
                turViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent turler=new Intent(TurlerActivity.this,YemeklerActivity.class) ;
                        turler.putExtra("TurId",adapter.getRef(position).getKey());
                        startActivity(turler);
                    }
                });
            }

            @NonNull
            @Override
            public TurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.tur_satiri_ogesi,parent,false);
                return new TurViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyler_tur.setAdapter(adapter);
    }
}