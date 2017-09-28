package com.driver.hp.komegaroodriver;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference versionApp;
    private String versionName;
    private View actualizar;
    private Button playStore;
    private String playS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        versionName = "1.0.0";
        setContentView(R.layout.activity_splash);
        versionApp = FirebaseDatabase.getInstance().getReference().child("appInfo");
        actualizar = findViewById(R.id.actualizar);
        playStore = (Button)findViewById(R.id.btn_playStore);
        mAuth = FirebaseAuth.getInstance();
        getVersionApp();
    }

    public void getVersionApp(){
        versionApp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                String name = mapS.get("androidVersionDriver");
                playS = mapS.get("androidAppDriver");
                if(name.equals(versionName)){
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            mAuthListener = new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    if (firebaseAuth.getCurrentUser() != null) {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            };
                            mAuth.addAuthStateListener(mAuthListener);
                        }
                    },5000);
                }else{
                    actualizar.setVisibility(View.VISIBLE);
                    playStore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchMarket();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void launchMarket() {
        Uri uri = Uri.parse("market://details?id="+playS);
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }
}
