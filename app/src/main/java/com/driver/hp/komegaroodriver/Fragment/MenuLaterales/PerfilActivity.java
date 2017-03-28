package com.driver.hp.komegaroodriver.Fragment.MenuLaterales;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.CircleTransform;
import com.driver.hp.komegaroodriver.LoginActivity;
import com.driver.hp.komegaroodriver.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private Button close, sClose, edit;
    private Firebase mRef, travel;
    private TextView ema, nom, ape, num, trip;
    private ImageView pho;
    private RatingBar stars;
    private String uidDriver;
    private View mProgressView;
    private View mPerfilFormView;
    private View editPopup;
    private ArrayList<String> arrayKey = new ArrayList<String>();
    private ArrayList<String> arrayCalif = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers");
        travel = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Travels");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        close = (Button) findViewById(R.id.btnPerfil);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Firebase.setAndroidContext(this);
        ema = (TextView)findViewById(R.id.txtCorreo);
        nom = (TextView)findViewById(R.id.txtNombre);
        ape = (TextView)findViewById(R.id.txtApellido);
        pho = (ImageView)findViewById(R.id.imgPhoto);
        num = (TextView)findViewById(R.id.txtNumero);
        trip = (TextView)findViewById(R.id.txtTrips);
        stars = (RatingBar) findViewById(R.id.ratingBar);
        mPerfilFormView = findViewById(R.id.perfil_form);
        mProgressView = findViewById(R.id.progressBarPerfil);
        /*editPopup = findViewById(R.id.editpopup);
        editPopup.setVisibility(View.GONE);*/
        edit = (Button)findViewById(R.id.btnEdit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*editPopup.setVisibility(View.VISIBLE);*/
            }
        });
        sClose = (Button)findViewById(R.id.btnSesionClose);
        sClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo();
            }
        });
        showProgress(true);
        getRating();
        perfil();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void perfil(){

        Firebase mRefChild = mRef.child(uidDriver);
        mRefChild.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<Integer, Integer> map = dataSnapshot.getValue(Map.class);
                    Map<Double, Double> mapD = dataSnapshot.getValue(Map.class);
                    Map<String, String>mapS =dataSnapshot.getValue(Map.class);
                    Integer califi = map.get("calification");
                    String email = mapS.get("email");
                    String name = mapS.get("name");
                    String phones = mapS.get("phoneNumber");
                    String photos = mapS.get("photoUrl");
                    Integer trips = map.get("trips");
                    String nombre = name.substring(0,name.indexOf(" "));
                    String apellido = name.replace(nombre ,"");
                    nom.setText(nombre);
                    ape.setText(apellido);
                    ema.setText(email);
                    num.setText(phones);
                    trip.setText(trips.toString());
                    stars.setRating(califi.floatValue());
                    Picasso.with(PerfilActivity.this).load(photos).transform(new CircleTransform()).into(pho);
                    showProgress(false);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void metodo(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPerfilFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void getRating(){
        travel.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String key = infoSnapshot.getKey();
                        String calif = (String) infoSnapshot.child("calification").getValue();
                        arrayKey.add(key);
                        arrayCalif.add(calif);
                    }

                    int sum = 0;
                    for (int i = 0; i < arrayCalif.size(); i++)
                        sum += Double.parseDouble(arrayCalif.get(i));
                    Double d = new Double(sum / arrayCalif.size());
                    mRef.child(uidDriver).child("calification").setValue(d.intValue());
                    Log.v("Calificaciones", String.valueOf(sum));
                    Log.v("arraysize", String.valueOf(arrayCalif.size()));
                    mRef.child(uidDriver).child("trips").setValue(arrayCalif.size());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}