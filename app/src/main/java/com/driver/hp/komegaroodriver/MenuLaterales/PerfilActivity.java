package com.driver.hp.komegaroodriver.MenuLaterales;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.RoundedTransformation;
import com.driver.hp.komegaroodriver.TutorialActivity;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PerfilActivity extends AppCompatActivity {

    public static final String MESSAGE_KEY="com.driver.hp.komegaroodriver.message_key";
    private Button close, sClose;
    private Firebase mRef, travel;
    private TextView nom, ape, num, trip, driverSaldo, dat, saldo, telT, envT, calT;
    private ImageView pho;
    private RatingBar stars;
    private String uidDriver, key;
    private View mProgressView;
    private View mPerfilFormView;
    private ArrayList<String> arrayKey = new ArrayList<>();
    private ArrayList<String> arrayCalif = new ArrayList<>();
    private Integer califi, trips;
    private AlertDialog alertDialog;
    private Timer timer;

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
        alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Komegaroo");
        alertDialog.setMessage("Revise su conexión a internet.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                        alertDialog.dismiss();
                    }
                });
        Typeface face= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Medium.ttf");
        Typeface face1= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-SemiBold.ttf");
        Typeface face2= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Light.ttf");
        Typeface face3= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Regular.ttf");
        Typeface face4= Typeface.createFromAsset(getAssets(), "monserrat/Montserrat-Bold.ttf");
        driverSaldo = (TextView)findViewById(R.id.txtDriverSaldo);
        driverSaldo.setTypeface(face1);
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
        dat = (TextView)findViewById(R.id.txtDatos);
        dat.setTypeface(face1);
        saldo = (TextView)findViewById(R.id.txtSaldo);
        saldo.setTypeface(face4);
        telT = (TextView)findViewById(R.id.telefonoT);
        telT.setTypeface(face1);
        envT = (TextView)findViewById(R.id.enviosT);
        envT.setTypeface(face1);
        calT = (TextView)findViewById(R.id.califT);
        calT.setTypeface(face1);
        getTravels();
        showProgress(true);
        perfil();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.show();
                    }
                });
            }
        },10000);
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
                    timer.cancel();
                    updateData();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void metodo(){
        Intent intent = new Intent(this, TutorialActivity.class);
        intent.putExtra(MESSAGE_KEY, uidDriver);
        startActivity(intent);
        FirebaseAuth.getInstance().signOut();
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
                        postGetSaldo();
                    }else{
                        mRef.child(uidDriver).child("trips").setValue(t);
                        trip.setText(t.toString());
                        stars.setRating(califi.floatValue());
                        postGetSaldo();
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
                        }else{
                            showProgress(false);
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

    OkHttpClient client = new OkHttpClient();
    public Call post(String url, String json, okhttp3.Callback callback) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public void postGetSaldo(){
        String url = "https://komegaroo-server.herokuapp.com/mobile/driverBalance";
        String body ="uid="+uidDriver;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("POSTNoSaldo!", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(false);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.v("POSTYesSaldo!", responseStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            driverSaldo.setText("CLP $"+responseStr.substring(0,responseStr.length()-3)+"."+responseStr.substring(responseStr.length()-3));
                            showProgress(false);
                        }
                    });
                } else {
                    String responseStr = response.body().string();
                    Log.v("POSTNoSaldo!", responseStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                        }
                    });
                }
            }
        });
    }
}