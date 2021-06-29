package com.example.lezzetkitapkullanici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lezzetkitapkullanici.Arayuz.ItemClickListener;
import com.example.lezzetkitapkullanici.Model.Tur;
import com.example.lezzetkitapkullanici.Model.Yemekler;
import com.example.lezzetkitapkullanici.ViewHolder.TurViewHolder;
import com.example.lezzetkitapkullanici.ViewHolder.YemekViewHolder;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

public class YemeklerActivity extends AppCompatActivity {
    MaterialEditText edtYemekAdi;
    MaterialEditText edtMalzemeler;
    MaterialEditText edtYapilisi;
    MaterialEditText edtIzlemelinki;
    MaterialEditText edtPufNokta;


    FirebaseDatabase database;
    DatabaseReference YemekYol;
    FirebaseStorage storage;
    StorageReference resimYolu;
    FirebaseRecyclerAdapter<Yemekler, YemekViewHolder> adapter;
    RecyclerView recyler_yemek;
    RecyclerView.LayoutManager layoutManager;


    Yemekler yeniYemek;
    String turId="";
    FirebaseRecyclerAdapter<Yemekler, YemekViewHolder>aramaAdapter;
    List<String> onerListe=new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yemekler);

        database = FirebaseDatabase.getInstance();
        YemekYol = database.getReference("Yemek");
        storage = FirebaseStorage.getInstance();
        resimYolu = storage.getReference();
        recyler_yemek=findViewById(R.id.rv_yemek);
        recyler_yemek.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recyler_yemek.setLayoutManager(layoutManager);
        if(getIntent()!=null){
            turId=getIntent().getStringExtra("TurId");
        }
        if(!turId.isEmpty()){
            yemekYukle(turId);
        }
        materialSearchBar=findViewById(R.id.aramaYemek);
        materialSearchBar.setHint("Aradığınız Yemeği Giriniz...");

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
                    recyler_yemek.setAdapter(adapter);
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

    private void oneriYukle() {
        YemekYol.orderByChild("ad").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot:snapshot.getChildren()){
                    Yemekler item=postSnapshot.getValue(Yemekler.class);
                    onerListe.add(item.getYemekadi());
                }
                materialSearchBar.setLastSuggestions(onerListe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void aramayaBasla(CharSequence text) {
        Query  adagoreAramasorgu=YemekYol.orderByChild("yemekadi").equalTo(text.toString());
        FirebaseRecyclerOptions<Yemekler>yemekSecenekleri=new FirebaseRecyclerOptions.Builder<Yemekler>().setQuery(adagoreAramasorgu,Yemekler.class).build();

        aramaAdapter=new FirebaseRecyclerAdapter<Yemekler, YemekViewHolder>(yemekSecenekleri) {
            @Override
            protected void onBindViewHolder(@NonNull YemekViewHolder yemekViewHolder, int i, @NonNull Yemekler yemekler) {
                yemekViewHolder.txtYemekAdi.setText(yemekler.getYemekadi());
                Picasso.with(getBaseContext()).load(yemekler.getResim()).into(yemekViewHolder.imageView);

                Yemekler lokal=yemekler;
                yemekViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent yemek=new Intent(YemeklerActivity.this,YemekDetayiActivity.class) ;
                        yemek.putExtra("YemekId",aramaAdapter.getRef(position).getKey());
                        startActivity(yemek);
                    }
                });
            }

            @NonNull
            @Override
            public YemekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.yemek_satiri_ogesi,parent,false);
                return new YemekViewHolder(itemView);

            }
        };
        aramaAdapter.startListening();
        recyler_yemek.setAdapter(aramaAdapter);
    }


    private void yemekYukle(String turId) {
        Query filtrele = YemekYol.orderByChild("turid").equalTo(turId);
        FirebaseRecyclerOptions<Yemekler> secenekler = new FirebaseRecyclerOptions.Builder<Yemekler>().setQuery(filtrele, Yemekler.class).build();
        adapter = new FirebaseRecyclerAdapter<Yemekler, YemekViewHolder>(secenekler) {
            @NonNull
            @Override
            public YemekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.yemek_satiri_ogesi, parent, false);
                return new YemekViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull YemekViewHolder yemekViewHolder, int i, @NonNull Yemekler yemekler) {
                yemekViewHolder.txtYemekAdi.setText(yemekler.getYemekadi());

                Picasso.with(getBaseContext()).load(yemekler.getResim()).into(yemekViewHolder.imageView);
                final Yemekler turtikla = yemekler;
                yemekViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent yemek = new Intent(YemeklerActivity.this, YemekDetayiActivity.class);
                        yemek.putExtra("YemekId", adapter.getRef(position).getKey());
                        startActivity(yemek);
                    }
                });
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyler_yemek.setAdapter(adapter);
    }
    }
