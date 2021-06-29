package com.example.lezzetkitapkullanici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.example.lezzetkitapkullanici.ViewHolder.KategoriViewHolder;
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

public class MainActivity extends AppCompatActivity {
    //firebase tanımı
    FirebaseDatabase database;
    DatabaseReference kategoriYol;
    FirebaseStorage storage;
    StorageReference resimYolu;
    FirebaseRecyclerAdapter<Kategori, KategoriViewHolder> adapter;
    RecyclerView recyler_kategori;
    RecyclerView.LayoutManager layoutManager;

    //model
    Kategori yeniKategori;

    FirebaseRecyclerAdapter<Kategori,KategoriViewHolder>aramaAdapter;
    List<String>onerListe=new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database = FirebaseDatabase.getInstance();
        kategoriYol = database.getReference("Kategori");
        storage = FirebaseStorage.getInstance();
        resimYolu = storage.getReference();
        recyler_kategori=findViewById(R.id.rv_kategori);
        recyler_kategori.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recyler_kategori.setLayoutManager(layoutManager);


        kategoriYükle();

        materialSearchBar=findViewById(R.id.arama);
        materialSearchBar.setHint("Aradığınız Kategoriyi Giriniz...");
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
                    recyler_kategori.setAdapter(adapter);
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
        Query  adagoreAramasorgu=kategoriYol.orderByChild("ad").equalTo(text.toString());
        FirebaseRecyclerOptions<Kategori>kategoriSecenekleri=new FirebaseRecyclerOptions.Builder<Kategori>().setQuery(adagoreAramasorgu,Kategori.class).build();

        aramaAdapter=new FirebaseRecyclerAdapter<Kategori, KategoriViewHolder>(kategoriSecenekleri) {
            @Override
            protected void onBindViewHolder(@NonNull KategoriViewHolder kategoriViewHolder, int i, @NonNull Kategori kategori) {
kategoriViewHolder.txtKategoriAdi.setText(kategori.getAd());
Picasso.with(getBaseContext()).load(kategori.getResim()).into(kategoriViewHolder.imageView);

Kategori lokal=kategori;
kategoriViewHolder.setItemClickListener(new ItemClickListener() {
    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        Intent turler=new Intent(MainActivity.this,TurlerActivity.class) ;
        turler.putExtra("KategoriId",aramaAdapter.getRef(position).getKey());
        startActivity(turler);
    }
});
            }

            @NonNull
            @Override
            public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.kategori_satiri_ogesi,parent,false);
                return new KategoriViewHolder(itemView);

            }
        };
        aramaAdapter.startListening();
        recyler_kategori.setAdapter(aramaAdapter);
    }

    private void oneriYukle() {
        kategoriYol.orderByChild("ad").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              for(DataSnapshot postSnapshot:snapshot.getChildren()){
                  Kategori item=postSnapshot.getValue(Kategori.class);
                  onerListe.add(item.getAd());
              }
              materialSearchBar.setLastSuggestions(onerListe);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void kategoriYükle() {
        FirebaseRecyclerOptions<Kategori> secenekler=new FirebaseRecyclerOptions.Builder<Kategori>().setQuery(kategoriYol,Kategori.class).build();
        adapter=new FirebaseRecyclerAdapter<Kategori, KategoriViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull KategoriViewHolder kategoriViewHolder, int i, @NonNull Kategori kategori) {
                kategoriViewHolder.txtKategoriAdi.setText(kategori.getAd());
                Picasso.with(getBaseContext()).load(kategori.getResim()).into(kategoriViewHolder.imageView);
                Kategori kategoritikla=kategori;
                kategoriViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       Intent turler=new Intent(MainActivity.this,TurlerActivity.class) ;
                        turler.putExtra("KategoriId",adapter.getRef(position).getKey());
                        startActivity(turler);
                    }
                });


            }

            @NonNull
            @Override
            public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.kategori_satiri_ogesi,parent,false);
                return new KategoriViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyler_kategori.setAdapter(adapter);
    }
}