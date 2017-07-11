package com.driver.hp.komegaroodriver.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
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
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ValorizarFragment extends Fragment {

    private RatingBar rating;
    private Button btnV;
    private Firebase travel, customers, stateDriver;
    private String uidClients, calificacion, keys, uidDriver;
    private View layout;
    private ImageView imageDriver;
    private View mProgressView, mPerfilFormView;
    private EditText coment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_valorizar, container, false);
        Firebase.setAndroidContext(getActivity());
        travel = new Firebase("https://decoded-pilot-144921.firebaseio.com/customerTravels");
        customers = new Firebase("https://decoded-pilot-144921.firebaseio.com/customers");
        stateDriver = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverState");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mPerfilFormView = v.findViewById(R.id.valorLayout);
        mProgressView = v.findViewById(R.id.progressBarValorizar);
        rating = (RatingBar)v.findViewById(R.id.ratingBarDriver);
        rating.setClickable(true);
        coment = (EditText)v.findViewById(R.id.editTextComent);
        layout = v.findViewById(R.id.valorizar);
        imageDriver = (ImageView)v.findViewById(R.id.imageViewDriver);
        btnV = (Button)v.findViewById(R.id.btnValor);
        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calificacion = String.valueOf(Math.round(rating.getRating()));
                travel.child(uidClients).child(keys).child("calification").setValue(calificacion);
                travel.child(uidClients).child(keys).child("comments").setValue(coment.getText().toString());
                ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).buildGoogleApiClient();
                stateDriver.child(uidDriver).child("state").setValue("nil");
                ((MainActivity)getActivity()).unlockedDrawer();
                coment.setText("");
                rating.setRating(1);
                layout.setVisibility(View.GONE);

            }
        });

        layout.setVisibility(View.GONE);
        showProgress(true);
        getCliente();
        return v;

    }


    public void getCliente(){
        stateDriver.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    String estado = mapS.get("state");
                    if(estado.equals("endTrip")){
                        uidClients = ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).uidClient;
                        keys = ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).key;
                        showDriver();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void showDriver(){
        customers.child(uidClients).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    String photo = mapS.get("photoUrl");
                    Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(8,1)).into(imageDriver);
                    showProgress(false);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

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
}
