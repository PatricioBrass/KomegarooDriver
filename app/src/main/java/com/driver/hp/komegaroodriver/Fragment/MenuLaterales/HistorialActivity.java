package com.driver.hp.komegaroodriver.Fragment.MenuLaterales;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinderListener;
import com.driver.hp.komegaroodriver.Fragment.Modules.Route;
import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistorialActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {

    private Button close;
    private Firebase mRef;
    private TextView text, text2;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        String uid = "EHnTb5kIRSOqJV2c8O2KFX2T8Nr2";
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Requesting Travel/"+uid);
        setContentView(R.layout.activity_historial);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapHistorial);
        mapFragment.getMapAsync(this);
        text2 = (TextView)findViewById(R.id.textView3);
        text = (TextView)findViewById(R.id.textView4);
        close = (Button)findViewById(R.id.btnHistorial);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo();
            }
        });

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<Double, Double> map = dataSnapshot.getValue(Map.class);
                Double inicioD = map.get("Destination Latitude");
                Double finD = map.get("Destination Longitude");
                Double inicio = map.get("Latitude");
                Double fin = map.get("Longitude");

                try
                {
                 Geocoder geocoder = new Geocoder(HistorialActivity.this, Locale.ENGLISH);
                    List<Address> addresses1 = geocoder.getFromLocation(inicio, fin, 1);
                    List<Address> addresses2 = geocoder.getFromLocation(inicioD, finD, 1);
                    StringBuilder str = new StringBuilder();
                    StringBuilder str2 = new StringBuilder();
                    if (geocoder.isPresent())
                    {
                        Address returnAddress = addresses1.get(0);
                        Address returnAddress1 = addresses2.get(0);

                        String direccion = returnAddress.getAddressLine(0)+", "+returnAddress.getAddressLine(1)+", "+returnAddress.getAddressLine(3);
                        String direccion2 = returnAddress1.getAddressLine(0)+", "+returnAddress1.getAddressLine(1)+", "+returnAddress1.getAddressLine(3);
                        str.append(direccion);
                        str2.append(direccion2);
                        text.setText(str);
                        text2.setText(str2);
                        try {

                            new DirectionFinder(HistorialActivity.this, direccion, direccion2).execute();

                        } catch (UnsupportedEncodingException e) {

                            e.printStackTrace();
                        }
                   }

                } catch (IOException e)
                {
                    Log.e("tag", e.getMessage());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void metodo(){
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
        Intent intent = new Intent(HistorialActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HistorialActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        for (Route route : routes) {

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(route.startLocation);
                        builder.include(route.endLocation);
                        LatLngBounds bounds = builder.build();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 170));

                        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.inicio))
                                .title(route.startAddress)
                                .position(route.startLocation)));
                        destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_final))
                                .title(route.endAddress)
                                .position(route.endLocation)));

                        PolylineOptions polylineOptions = new PolylineOptions().
                                geodesic(true).
                                color(Color.rgb(119, 21, 204)).
                                width(14);


                        for (int i = 0; i < route.points.size(); i++)
                            polylineOptions.add(route.points.get(i));

                        polylinePaths.add(mMap.addPolyline(polylineOptions));

                    }
            return;


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-33.4724227, -70.7699159);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));
    }
}
