package com.driver.hp.komegaroodriver.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.driver.hp.komegaroodriver.CircleTransform;
import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.RoundedTransformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ValorizarFragment extends Fragment {

    private RatingBar rating;
    private Button btnV;
    private DatabaseReference travel, customers, stateDriver,dTravels;
    private String uidClient, key, uidDriver;
    private ImageView imageDriver;
    private View mProgressView, mPerfilFormView;
    private EditText coment;
    private Integer trips;
    private Integer califica;
    private Double calification;

    public ValorizarFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_valorizar, container, false);
        travel = FirebaseDatabase.getInstance().getReference().child("customerTravels");
        customers = FirebaseDatabase.getInstance().getReference().child("customers");
        stateDriver = FirebaseDatabase.getInstance().getReference().child("driverState");
        dTravels = FirebaseDatabase.getInstance().getReference().child("driverTravels");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPerfilFormView = v.findViewById(R.id.valorLayout);
        mProgressView = v.findViewById(R.id.progressBarValorizar);
        rating = (RatingBar)v.findViewById(R.id.ratingBarDriver);
        rating.setClickable(true);
        coment = (EditText)v.findViewById(R.id.editTextComent);
        imageDriver = (ImageView)v.findViewById(R.id.imageViewDriver);
        btnV = (Button)v.findViewById(R.id.btnValor);
        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                califica = Math.round(rating.getRating());
                travel.child(uidClient).child(key).child("calification").setValue(califica.toString());
                travel.child(uidClient).child(key).child("comments").setValue(coment.getText().toString());
                stateDriver.child(uidDriver).child("state").setValue("nil");
                sendCalificationFirebase();
                removeFragment();
            }
        });
        showProgress(true);
        getKey();
        return v;

    }

    public void getKey() {
        dTravels.child(uidDriver).orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                            key = mapS.keySet().toString().replace("[", "").replace("]", "");
                            getUidClient();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                    }
                });
    }

    public void getUidClient(){
        dTravels.child(uidDriver).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    uidClient = mapS.get("customerUid");
                    showClient();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showClient(){
            customers.child(uidClient).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        Map<Double, Double> mapD = (Map<Double, Double>) dataSnapshot.getValue();
                        Map<Long, Long> mapI = (Map<Long, Long>) dataSnapshot.getValue();
                        calification = mapD.get("calification");
                        trips = mapI.get("trips").intValue();
                        String photo = mapS.get("photoUrl");
                        Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(8, 1)).into(imageDriver);
                        showProgress(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {

                }
            });
    }

    public void sendCalificationFirebase(){
        Double calificacion0 = ((calification+califica)/2);
        Double calificacion1 = (((trips *calification)+califica)/(trips + 1));
        Integer viajes = trips + 1;
        customers.child(uidClient).child("trips").setValue(viajes);
        if (!trips.equals(0)){
            customers.child(uidClient).child("calification").setValue(calificacion1);
        }else{
            customers.child(uidClient).child("calification").setValue(calificacion0);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPerfilFormView.animate().alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().alpha(
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

    public static ValorizarFragment newInstance(String text) {
        ValorizarFragment f = new ValorizarFragment();
        Bundle b = new Bundle();
        b.putString("Valorizar", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(ValorizarFragment.this).commit();
    }
}
