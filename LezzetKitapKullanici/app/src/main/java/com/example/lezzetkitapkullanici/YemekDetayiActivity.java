package com.example.lezzetkitapkullanici;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lezzetkitapkullanici.Model.Yemekler;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import info.hoang8f.widget.FButton;

public class YemekDetayiActivity extends AppCompatActivity {
TextView yemek_ad,yemek_malzeme,yemek_yapilis,yemek_pufnokta,yemek_izleme_video;
ImageView yemek_detay_resim;
FButton btn_izle;
CollapsingToolbarLayout  collapsingToolbarLayout;

FirebaseDatabase database;
DatabaseReference yemekYolu;
String yemekId="";
Yemekler tiklananYemek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yemek_detayi);

        yemek_ad=findViewById(R.id.txt_yemekadi);
        yemek_malzeme=findViewById(R.id.txt_yemekmalzeme);
        yemek_yapilis=findViewById(R.id.txt_yemekyapilis);
        yemek_pufnokta=findViewById(R.id.txt_yemekpufnokta);
        yemek_izleme_video=findViewById(R.id.video_id);

        yemek_detay_resim=findViewById(R.id.yemek_detay_resmi);
        btn_izle=findViewById(R.id.btn_izle);
        btn_izle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoId=new Intent(YemekDetayiActivity.this,VideoIzlemeActivity.class);
                videoId.putExtra("Link",yemek_izleme_video.getText().toString());
                startActivity(videoId);
            }
        });

        collapsingToolbarLayout=findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expantedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapseAppBar);


   database=FirebaseDatabase.getInstance();
   yemekYolu=database.getReference("Yemek");


        
        if(getIntent()!=null){
            yemekId=getIntent().getStringExtra("YemekId");
        }
        if(!yemekId.isEmpty()){
            detayAl(yemekId);
        }
    }

    private void detayAl(String yemekId) {
        yemekYolu.child(yemekId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tiklananYemek=snapshot.getValue(Yemekler.class);

                Picasso.with(getBaseContext()).load(tiklananYemek.getResim()).into(yemek_detay_resim);
                collapsingToolbarLayout.setTitle(tiklananYemek.getYemekadi());
                yemek_malzeme.setText(tiklananYemek.getMalzemeler());
                yemek_yapilis.setText(tiklananYemek.getYapilis());
                yemek_pufnokta.setText(tiklananYemek.getPufnoktasi());
                yemek_ad.setText(tiklananYemek.getYemekadi());
                yemek_izleme_video.setText(tiklananYemek.getIzlemelinki());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}