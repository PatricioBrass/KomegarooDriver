package com.driver.hp.komegaroodriver.Fragment.MenuLaterales;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.LoginActivity;
import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.RoundedTransformation;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private Button close, sClose;
    private Firebase mRef, travel;
    private TextView nom, ape, num, trip, nomApe, dat, nomT, telT, envT, calT;
    private ImageView pho;
    private RatingBar stars;
    private String uidDriver, key;
    private View mProgressView;
    private View mPerfilFormView;
    private ArrayList<String> arrayKey = new ArrayList<>();
    private ArrayList<String> arrayCalif = new ArrayList<>();
    private Integer califi, trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/drivers");
        travel = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverTravels");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        close = (Button) findViewById(R.id.btnPerfil);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Firebase.setAndroidContext(this);
        Typeface face= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Medium.ttf");
        Typeface face1= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-SemiBold.ttf");
        Typeface face2= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Light.ttf");
        Typeface face3= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Regular.ttf");
        Typeface face4= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Bold.ttf");
        nomApe = (TextView)findViewById(R.id.txtNombreApellido);
        nomApe.setTypeface(face1);
        nom = (TextView)findViewById(R.id.txtNombre);
        nom.setTypeface(face);
        ape = (TextView)findViewById(R.id.txtApellido);
        ape.setTypeface(face);
        pho = (ImageView)findViewById(R.id.imgPhoto);
        num = (TextView)findViewById(R.id.txtNumero);
        num.setTypeface(face2);
        trip = (TextView)findViewById(R.id.txtTrips);
        trip.setTypeface(face2);
        stars = (RatingBar) findViewById(R.id.ratingBar);
        mPerfilFormView = findViewById(R.id.perfil_form);
        mProgressView = findViewById(R.id.progressBarPerfil);
        sClose = (Button)findViewById(R.id.btnSesionClose);
        sClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo();
            }
        });
        showProgress(true);
        perfil();
        dat = (TextView)findViewById(R.id.txtDatos);
        dat.setTypeface(face1);
        nomT = (TextView)findViewById(R.id.nombreT);
        nomT.setTypeface(face4);
        telT = (TextView)findViewById(R.id.telefonoT);
        telT.setTypeface(face1);
        envT = (TextView)findViewById(R.id.enviosT);
        envT.setTypeface(face1);
        calT = (TextView)findViewById(R.id.califT);
        calT.setTypeface(face1);
        getTravels();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void perfil(){

        Firebase mRefChild = mRef.child(uidDriver);
        mRefChild.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<Integer, Integer> map = dataSnapshot.getValue(Map.class);
                    Map<String, String>mapS =dataSnapshot.getValue(Map.class);
                    califi = map.get("calification");
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    String name = mapS.get("name");
                    String phones = mapS.get("phoneNumber");
                    String photos = mapS.get("photoUrl");
                    trips = map.get("trips");
                    String nombre = name.substring(0,name.indexOf(" "));
                    String apellido = name.replace(nombre+" " ,"");
                    nom.setText(nombre);
                    ape.setText(apellido);
                    num.setText(phones);
                    Picasso.with(PerfilActivity.this).load(photos).transform(new RoundedTransformation(9,1)).into(pho);
                    updateData();
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
        finish();
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
        travel.child(uidDriver).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> map = dataSnapshot.getValue(Map.class);
                    String califica = map.get("calification");
                    Integer t = arrayCalif.size();
                    if (!califica.equals("")&&t>trips){
                        Double d = new Double((((t-1)*califi) + Integer.parseInt(califica)) / t);
                        Integer f = d.intValue();
                        mRef.child(uidDriver).child("calification").setValue(f);
                        mRef.child(uidDriver).child("trips").setValue(t);
                        trip.setText(t.toString());
                        stars.setRating(f.floatValue());
                        showProgress(false);
                    }else{
                        mRef.child(uidDriver).child("trips").setValue(t);
                        trip.setText(t.toString());
                        stars.setRating(califi.floatValue());
                        showProgress(false);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void updateData(){
        travel.child(uidDriver).orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                            key = mapS.keySet().toString().replace("[","").replace("]","");
                            getRating();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }

    public void getTravels(){
        travel.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    arrayCalif.clear();
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String calif = infoSnapshot.getKey();
                        arrayCalif.add(calif);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}