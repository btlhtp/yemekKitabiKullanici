package com.example.lezzetkitapkullanici;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class ReklamActivity extends AppCompatActivity implements RewardedVideoAdListener {


    private RewardedVideoAd mRewardedVideoAd;
    Button btn_reklam;

    String yemekId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reklam);


        if(getIntent()!=null){
            yemekId=getIntent().getStringExtra("YemekId");
        }
       MobileAds.initialize(this,"ca-app-pub-8544191306755317~9145770199");
        mRewardedVideoAd=MobileAds.getRewardedVideoAdInstance(this);
      mRewardedVideoAd.loadAd("ca-app-pub-8544191306755317/3224000304",
                new AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE231").build());


        btn_reklam=findViewById(R.id.btn_reklam);

        btn_reklam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRewardedVideoAd.isLoaded()){
                    mRewardedVideoAd.show();
                }
                else{
                    Toast.makeText(ReklamActivity.this,"Lütfen Tekrar Deneyin...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Intent yemekdatay=new Intent(ReklamActivity.this,YemekDetayiActivity.class);
        yemekdatay.putExtra("YemekId",yemekId);
        startActivity(yemekdatay);
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mRewardedVideoAd.pause(this);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}


//ca-app-pub-3940256099942544/5224354917