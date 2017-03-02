package com.driver.hp.komegaroodriver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinderListener;
import com.driver.hp.komegaroodriver.Fragment.Modules.Route;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btn;
    public static final String MESSAGE_KEY="com.kome.hp.komegarooandroid.message_key";
    public static final String MESSAGE_KEYS="com.kome.hp.komegarooandroid.message_keys";
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    MediaPlayer mp = new MediaPlayer();
    private Button pedirK;
    private static final String TAG = "MapsActivity";
    private Firebase mRef, mRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn = (Button) findViewById(R.id.button);
        Intent intent = getIntent();
        final String msn1 = intent.getStringExtra(MESSAGE_KEY);
        final String msn2 =intent.getStringExtra(MESSAGE_KEYS);
        /*Firebase.setAndroidContext(this);
        mRef2 = new Firebase("https://decoded-pilot-144921.firebaseio.com/Requesting Travel/Chile/Metropolitana de Santiago/Santiago/-KY0KtzaTpuWCdY8rGky");
        mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map =dataSnapshot.getValue(Map.class);
                String inicio = map.get("Latitude");
                String fin = map.get("Longitude");

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
        try {

            new DirectionFinder(this, msn1, msn2).execute();

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                metodo();
            }
        });

        Geocoder coder = new Geocoder(this);
        List<Address> address = null;
        try {
            address = coder.getFromLocationName(msn2,5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            LatLng p1 = new LatLng( location.getLatitude(), location.getLongitude());


        /*Firebase.setAndroidContext(this);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Requesting Travel/Chile/Metropolitana de Santiago");
        pedirK = (Button)findViewById(R.id.pedirKame);
        pedirK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firebase mRefChild2 = mRef.child("Santiago").push();
                Firebase mRefChild = mRefChild2.child("Latitude");
                Firebase mRefChild3 = mRefChild2.child("Longitude");
                mRefChild.setValue(msn1);
                mRefChild3.setValue(msn2);
            }
        });*/
    }

    public void metodo(){
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-33.4724227, -70.7699159);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));

    }

    @Override
    public void onDirectionFinderStart() {


        progressDialog = ProgressDialog.show(this, "Un momento.",
                "Generando ruta..!", true);



            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Dirección fallida");
        alertDialog.setMessage("La dirección ingresada está fuera de alcance.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });

        if (!routes.isEmpty()){
            for (Route routess : routes){
                LatLngBounds mBounds= new LatLngBounds(
                        new LatLng(-33.9012253,-70.899347),
                        new LatLng(-33.2575545, -70.2504896));
                if(mBounds.contains(new LatLng(routess.startLocation.latitude, routess.startLocation.longitude))&&mBounds.contains(new LatLng(routess.endLocation.latitude, routess.endLocation.longitude))){

        for (Route route : routes) {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(route.startLocation);
                builder.include(route.endLocation);
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 170));

                ((TextView) this.findViewById(R.id.tvDuration)).setText(route.duration.text + " mins");
                ((TextView) this.findViewById(R.id.tvDistance)).setText(route.distance.text);
                DecimalFormatSymbols simb = new DecimalFormatSymbols();
                simb.setGroupingSeparator('.');
                DecimalFormat form = new DecimalFormat("###,###", simb);
                if (route.duration.text.length() > 5) {
                    double pre5 = (((((Double.parseDouble((route.duration.text).substring(0, 1)) * 60) + Double.parseDouble((route.duration.text).substring(route.duration.text.length() - 2))) * 60) + (Double.parseDouble((route.distance.text).replace("km", "")) * 160) + 500) * 0.95);
                    double pre15 = (((((Double.parseDouble((route.duration.text).substring(0, 1)) * 60) + Double.parseDouble((route.duration.text).substring(route.duration.text.length() - 2))) * 60) + (Double.parseDouble((route.distance.text).replace("km", "")) * 160) + 500) * 1.15);
                    ((TextView) this.findViewById(R.id.tvPrecio)).setText("$" + String.valueOf(form.format(pre5)));
                    ((TextView) this.findViewById(R.id.tvPrecio15)).setText("$" + String.valueOf(form.format(pre15)));
                } else {
                    double pre5 = (((Double.parseDouble(route.duration.text) * 60) + (Double.parseDouble((route.distance.text).replace("km", "")) * 160) + 500) * 0.95);
                    double pre15 = (((Double.parseDouble(route.duration.text) * 60) + (Double.parseDouble((route.distance.text).replace("km", "")) * 160) + 500) * 1.15);
                    ((TextView) this.findViewById(R.id.tvPrecio)).setText("$" + String.valueOf(form.format(pre5)));
                    ((TextView) this.findViewById(R.id.tvPrecio15)).setText("$" + String.valueOf(form.format(pre15)));
                }


                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.inicio))
                        .title(route.startAddress)
                        .position(route.startLocation)));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finaly))
                        .title(route.endAddress)
                        .position(route.endLocation)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.rgb(119, 21, 204)).
                        width(14);


                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));

            } }else {
                    alertDialog.show();
                    return;
                }}} else {
            alertDialog.show();
            return;
        }

    }}
