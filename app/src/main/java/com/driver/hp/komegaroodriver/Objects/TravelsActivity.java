package com.driver.hp.komegaroodriver.Objects;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.CircleTransform;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinderListener;
import com.driver.hp.komegaroodriver.Fragment.Modules.Route;
import com.driver.hp.komegaroodriver.R;
import com.google.firebase.database.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TravelsActivity extends AppCompatActivity implements DirectionFinderListener {

    private Button close;
    private DatabaseReference customers, travels;
    private String uidDriver, uidClient, trFrom, trTo, tKey, tStatus;
    private Float trCalif;
    private TextView nombre, from, to, price, apellido;
    private ImageView photoDriver;
    private Integer position, trPrice;
    private RatingBar stars;
    public static final String MESSAGE_KEY="com.driver.hp.komegaroodriver.message_key";
    private ArrayList<String> arrayClient = new ArrayList<>();
    private ArrayList<String> arrayCalif = new ArrayList<>();
    private ArrayList<String> arrayFrom = new ArrayList<>();
    private ArrayList<String> arrayTo = new ArrayList<>();
    private ArrayList<Integer> arrayPrice = new ArrayList<>();
    private ArrayList<String> arrayKey = new ArrayList<>();
    private ArrayList<String> arrayStatus = new ArrayList<>();
    GoogleMap mMap;
    MapView mapView;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private View mProgressView, recyclerView;
    private DecimalFormatSymbols simb;
    private DecimalFormat form;
    private ImageView cancelado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_travels);
        customers = FirebaseDatabase.getInstance().getReference().child("customers");
        travels = FirebaseDatabase.getInstance().getReference().child("driverTravels");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        nombre = (TextView)findViewById(R.id.nameTravels);
        apellido = (TextView)findViewById(R.id.apellidoTravel);
        from = (TextView)findViewById(R.id.textViewOrigen);
        to = (TextView)findViewById(R.id.textViewDestino);
        price = (TextView)findViewById(R.id.textViewPrice);
        photoDriver = (ImageView)findViewById(R.id.imageViewDriverT);
        stars = (RatingBar) findViewById(R.id.rBarT);
        mapView = (MapView)findViewById(R.id.mapTravel);
        mapView.setClickable(false);
        mapView.onCreate(null);
        mapView.onResume();
        simb = new DecimalFormatSymbols();
        simb.setGroupingSeparator('.');
        form = new DecimalFormat("###,###", simb);
        mProgressView = findViewById(R.id.progressBarTravel);
        recyclerView = findViewById(R.id.histotyT);
        Intent intent = getIntent();
        position = intent.getIntExtra(MESSAGE_KEY,1);
        Log.v("Position",String.valueOf(position));
        getDataForm();
        cancelado = (ImageView)findViewById(R.id.imageViajeCancelado);
        close = (Button)findViewById(R.id.btnCloseT);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    public void getDataForm(){
        travels.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Set<String> h1 = new HashSet<>();
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String uid = infoSnapshot.getKey();
                        String driver = (String) infoSnapshot.child("customerUid").getValue();
                        String califi = (String) infoSnapshot.child("calification").getValue();
                        String from = (String) infoSnapshot.child("from").getValue();
                        String to = (String) infoSnapshot.child("to").getValue();
                        Integer price = infoSnapshot.child("tripPrice").getValue().hashCode();
                        String status = (String) infoSnapshot.child("status").getValue();
                        arrayKey.add(uid);
                        arrayClient.add(driver);
                        arrayCalif.add(califi);
                        arrayFrom.add(from);
                        arrayTo.add(to);
                        arrayPrice.add(price);
                        arrayStatus.add(status);
                    }
                    h1.addAll(arrayKey);
                    arrayKey.clear();
                    arrayKey.addAll(h1);
                    tKey = arrayKey.get(position);
                    int posi = arrayKey.indexOf(tKey);
                    uidClient = arrayClient.get(posi);
                    if(!arrayCalif.get(posi).isEmpty()) {
                        trCalif = Float.valueOf(arrayCalif.get(posi));
                    }
                    trFrom = arrayFrom.get(posi);
                    trTo = arrayTo.get(posi);
                    trPrice = arrayPrice.get(posi);
                    tStatus = arrayStatus.get(posi);
                    showDriver();
                    setData();
                    try {

                        new DirectionFinder(TravelsActivity.this, trFrom, trTo).execute();

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public void showDriver(){
        customers.child(uidClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    String photo = mapS.get("photoUrl");
                    String name = mapS.get("name");
                    String nombree = name.substring(0,name.indexOf(" "));
                    String apellidoo = name.replace(nombree ,"");
                    Picasso.with(TravelsActivity.this).load(photo).transform(new CircleTransform()).into(photoDriver);
                    nombre.setText(nombree);
                    apellido.setText(apellidoo);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void setData(){
        String precio = "CLP $"+form.format(trPrice);
        switch (tStatus) {
            case "canceledByCustomer":
                cancelado.setVisibility(View.VISIBLE);
                stars.setRating(1);
                from.setText(trFrom);
                to.setText(trTo);
                price.setText("CLP $1.000");
                break;
            case "canceledByDriver":
                cancelado.setVisibility(View.VISIBLE);
                stars.setRating(1);
                from.setText(trFrom);
                to.setText(trTo);
                price.setText("CLP $0");
                break;
            default:
                if(trCalif!=null) {
                    stars.setRating(trCalif);
                }
                from.setText(trFrom);
                to.setText(trTo);
                price.setText(precio);
        }
    }

    @Override
    public void onDirectionFinderStart() {
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
    public void onDirectionFinderSuccess(final List<Route> routes) {

        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setScrollGesturesEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Route route : routes) {
                    mMap.clear();
                    if(!tStatus.equals("endTrip")){
                        ((TextView) TravelsActivity.this.findViewById(R.id.textViewDistancia)).setText("CLP $0");
                        ((TextView) TravelsActivity.this.findViewById(R.id.textViewTiempo)).setText("CLP $0");
                    }else {
                        Double riderValor = trPrice * .80;
                        Double komeValor = trPrice * .20;
                        String valorD = "CLP $" + String.valueOf(form.format(riderValor.intValue()));
                        String valorT = "CLP $" + String.valueOf(form.format(komeValor.intValue()));
                        ((TextView) TravelsActivity.this.findViewById(R.id.textViewDistancia)).setText(valorD);
                        ((TextView) TravelsActivity.this.findViewById(R.id.textViewTiempo)).setText(valorT);
                    }
                    originMarkers.add(mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.inicio))
                            .title(route.startAddress)
                            .position(route.startLocation)));
                    destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finaly))
                            .title(route.endAddress)
                            .position(route.endLocation)));
                    builder.include(route.startLocation);
                    builder.include(route.endLocation);
                    PolylineOptions polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(Color.rgb(119, 21, 204)).
                            width(8);
                    for (int i = 0; i < route.points.size(); i++) {
                        polylineOptions.add(route.points.get(i));
                        builder.include(route.points.get(i));
                    }
                    polylinePaths.add(mMap.addPolyline(polylineOptions));
                    LatLngBounds bounds = builder.build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
                }
            }
        });
    }
}
