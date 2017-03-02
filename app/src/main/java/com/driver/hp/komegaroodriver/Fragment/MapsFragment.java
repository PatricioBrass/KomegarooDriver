package com.driver.hp.komegaroodriver.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinderListener;
import com.driver.hp.komegaroodriver.Fragment.Modules.Route;
import com.driver.hp.komegaroodriver.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HP on 18/10/2016.
 */

public class  MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, DirectionFinderListener {

    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private static final int PLACE_PICKER_FLAG = 1;
    public static final String MESSAGE_KEY = "com.kome.hp.komegarooandroid.message_key";
    public static final String MESSAGE_KEYS = "com.kome.hp.komegarooandroid.message_keys";
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Button btnFindPath, fab, btnTrip, btnfinish;
    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;
    private CharSequence constraint;
    private Firebase mRef, mRef2, nRef, pRef, sRef, cRef, tRef, cTravels, dTravels;
    private Double lat, lng;
    private Integer u;
    private ArrayList<String> arrayfDirec = new ArrayList<>();
    private ArrayList<String> arraytDirec = new ArrayList<>();
    private ArrayList<String> arrayClient = new ArrayList<>();
    private ArrayList<String> arrayClient2 = new ArrayList<>();
    private ArrayList<String> arrayDriver = new ArrayList<>();
    private String dire, uidDriver, uidClient, fDirec, tDirec, price;
    private StringBuilder str, str2, str3;
    View mMapView;
    private Timer timer2, timer, timer1;
    private LatLng latLngDriver;
    private Calendar calander;
    private AlertDialog alertDialog;
    private Geocoder geocoder, geocoder2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();
        Firebase.setAndroidContext(getActivity());
        calander = Calendar.getInstance();
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Available Drivers/Santiago");
        nRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/On Way Drivers/Santiago");
        sRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Requested Drivers/Santiago");
        cRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Driver On Trip/Santiago");
        pRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status");
        tRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Requested Travels/Santiago");
        cTravels = new Firebase("https://decoded-pilot-144921.firebaseio.com/Customers Travels");
        dTravels = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Travels");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnFindPath = (Button) v.findViewById(R.id.btnFindPath);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                send();

                fab.setVisibility(View.VISIBLE);
                btnFindPath.setVisibility(View.GONE);

            }
        });

        fab = (Button) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child(uidDriver).removeValue();
                nRef.child(uidDriver).removeValue();
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                getActivity().finish();
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        fab.setVisibility(View.GONE);

        btnTrip = (Button) v.findViewById(R.id.trip);
        btnTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnTrip();
                setCustomerTravel();
            }
        });
        btnTrip.setVisibility(View.GONE);
        btnfinish = (Button) v.findViewById(R.id.finish);
        btnfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cRef.child(uidDriver).removeValue();
                Intent i = getActivity().getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                getActivity().finish();
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                setFinish();
            }
        });
        btnfinish.setVisibility(View.GONE);
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        geocoder2 = new Geocoder(getActivity(), Locale.ENGLISH);
        timer = new Timer();
        timer1 = new Timer();
        timer2 = new Timer();
        uidClient = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        delete();
        piden();
        load();
        onWay();
        onTrip();
        mRef.child(uidDriver).removeValue();
        nRef.child(uidDriver).removeValue();
        cRef.child(uidDriver).removeValue();

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapView = fragment.getView();
        fragment.getMapAsync(this);

    }

    public void delete() {

        mRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mRef.child(uidDriver).removeValue();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        sRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRef.child(uidDriver).removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        nRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRef.child(uidDriver).removeValue();
                sRef.child(uidDriver).removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                nRef.child(uidDriver).removeValue();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        cRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                nRef.child(uidDriver).removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                cRef.child(uidDriver).removeValue();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void load() {

        Firebase mRefChild = pRef.child("Available Drivers");
        final Firebase mRefChild2 = mRefChild.child("Santiago");
        Firebase mRefChild3 = mRefChild2.child(uidDriver);
        final Firebase mRefChild4 = mRefChild3.child("Latitude");
        final Firebase mRefChild5 = mRefChild3.child("Longitude");
        Firebase sRefChild = pRef.child("On Way Drivers");
        final Firebase sRefChild2 = sRefChild.child("Santiago");
        final Firebase sRefChild3 = sRefChild2.child(uidDriver);
        final Firebase sRefChild4 = sRefChild3.child("Customer Uid");
        final Firebase sRefChild5 = sRefChild3.child("Driver Latitude");
        final Firebase sRefChild6 = sRefChild3.child("Driver Longitude");
        final Firebase sRequested = sRef.child(uidDriver);
        final Firebase latF = cRef.child(uidDriver).child("Driver Latitude");
        final Firebase lngF = cRef.child(uidDriver).child("Driver Longitude");


        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(uidDriver) && dataSnapshot.child(uidDriver).hasChild("Latitude") && dataSnapshot.child(uidDriver).hasChild("Longitude")) {
                            mRefChild4.setValue(lat);
                            mRefChild5.setValue(lng);
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }, 3000, 5000);


        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                nRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(uidDriver) && dataSnapshot.child(uidDriver).hasChild("Driver Latitude") && dataSnapshot.child(uidDriver).hasChild("Driver Longitude")) {
                            sRefChild5.setValue(lat);
                            sRefChild6.setValue(lng);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }, 3000, 5000);


        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                cRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(uidDriver) && dataSnapshot.child(uidDriver).hasChild("Driver Latitude") && dataSnapshot.child(uidDriver).hasChild("Driver Longitude")) {
                            latF.setValue(lat);
                            lngF.setValue(lng);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }, 3000, 5000);

    }

    public void getData() {
        tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    fDirec = mapS.get("From Direction");
                    tDirec = mapS.get("To Direction");
                    price = mapS.get("Price");
                    Log.v("DIRECCION", uidClient);
                    Log.v("DIRECCION", fDirec);

                    try {
                        List<Address> addresses = geocoder.getFromLocation(latLngDriver.latitude, latLngDriver.longitude, 1);
                        str2 = new StringBuilder();
                        if (geocoder.isPresent()) {
                            Address returnAddress = addresses.get(0);

                            String direccion = returnAddress.getAddressLine(0) + ", " + returnAddress.getAddressLine(1) + ", " + returnAddress.getAddressLine(3);

                            str2.append(direccion);

                        }
                    } catch (IOException e) {
                        Log.e("tag", e.getMessage());
                    }

                    try {

                        new DirectionFinder(MapsFragment.this, str2.toString(), fDirec.toString()).execute();

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }

                    btnTrip.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



    public void onWay() {

        nRef.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    getData();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        }



    public void setOnTrip() {

        Firebase clientF = cRef.child(uidDriver).child("Customer Uid");
        Firebase latF = cRef.child(uidDriver).child("Driver Latitude");
        Firebase lngF = cRef.child(uidDriver).child("Driver Longitude");
        clientF.setValue(uidClient);
        latF.setValue(lat);
        lngF.setValue(lng);
        nRef.child(uidDriver).removeValue();

    }

    public void onTrip(){

        cRef.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                            try {

                                List<Address> addresses = geocoder2.getFromLocation(latLngDriver.latitude, latLngDriver.longitude, 1);
                                str = new StringBuilder();
                                if (geocoder2.isPresent()) {
                                    Address returnAddress = addresses.get(0);

                                    String direccion = returnAddress.getAddressLine(0) + ", " + returnAddress.getAddressLine(1) + ", " + returnAddress.getAddressLine(3);

                                    str.append(direccion);

                                }
                            } catch (IOException e) {
                                Log.e("tag", e.getMessage());
                            }

                            try {
                                new DirectionFinder(MapsFragment.this, str.toString(), tDirec.toString()).execute();

                            } catch (UnsupportedEncodingException e) {

                                e.printStackTrace();
                            }
                            btnfinish.setVisibility(View.VISIBLE);
                            btnTrip.setVisibility(View.GONE);
                            fab.setVisibility(View.GONE);
                            tRef.child(uidClient).removeValue();

                        }


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setCustomerTravel(){
        Log.v("From", fDirec);
        Log.v("To", tDirec);

        int cDay = calander.get(Calendar.DAY_OF_MONTH);
        int cMonth = calander.get(Calendar.MONTH) + 1;
        int cYear = calander.get(Calendar.YEAR);
        int cHour = calander.get(Calendar.HOUR_OF_DAY);
        int cMinute = calander.get(Calendar.MINUTE);
        String fecha = cDay+"/"+cMonth+"/"+cYear;
        String fecha2 = "0"+cDay+"/"+"0"+cMonth+"/"+cYear;
        String fecha3 = cDay+"/"+"0"+cMonth+"/"+cYear;
        String fecha4 = "0"+cDay+"/"+cMonth+"/"+cYear;
        String hora = cHour+":"+cMinute;
        String hora2 = "0"+cHour+":"+"0"+cMinute;
        String hora3 = cHour+":"+"0"+cMinute;
        String hora4 = "0"+cHour+":"+cMinute;
        Firebase set = cTravels.child(uidClient).push();
        set.child("customerUid").setValue(uidClient);
        set.child("driverUid").setValue(uidDriver);
        set.child("from").setValue(fDirec);
        set.child("to").setValue(tDirec);
        set.child("calification").setValue("");
        set.child("comments").setValue("");
        set.child("tripPrice").setValue(price);
        if(String.valueOf(cDay).length()<2&&String.valueOf(cMonth).length()<2){
            set.child("date").setValue(fecha2);
        }else if(String.valueOf(cMonth).length()<2){
            set.child("date").setValue(fecha3);
        }else if(String.valueOf(cDay).length()<2){
            set.child("date").setValue(fecha4);
        }else{
        set.child("date").setValue(fecha);
        }
        if(String.valueOf(cHour).length()<2&&String.valueOf(cMinute).length()<2){
            set.child("startHour").setValue(hora2);
        }else if(String.valueOf(cHour).length()<2){
            set.child("startHour").setValue(hora4);
        }else if(String.valueOf(cMinute).length()<2){
            set.child("startHour").setValue(hora3);
        }else{
            set.child("startHour").setValue(hora);
        }
        set.child("endHour").setValue("");
        set.child("code").setValue("");

    }
    public void setFinish(){
        final int cHour = calander.get(Calendar.HOUR_OF_DAY);
        final int cMinute = calander.get(Calendar.MINUTE);
        final String horaE = cHour+":"+cMinute;
        final String horaE2 = "0"+cHour+":"+"0"+cMinute;
        final String horaE3 = cHour+":"+"0"+cMinute;
        final String horaE4 = "0"+cHour+":"+cMinute;
        cTravels.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> arrayKeys = new ArrayList<String>();
                    ArrayList<String> arrayEnd = new ArrayList<String>();
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String keys = infoSnapshot.getKey();
                        String endH = (String) infoSnapshot.child("endHour").getValue();
                        arrayKeys.add(keys);
                        arrayEnd.add(endH);
                    }if(arrayEnd.contains("")){
                        int indeX = arrayEnd.indexOf("");
                        String keyS = arrayKeys.get(indeX);
                        if(String.valueOf(cHour).length()<2&&String.valueOf(cMinute).length()<2){
                            cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE2);
                        }else if(String.valueOf(cHour).length()<2){
                            cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE4);
                        }else if(String.valueOf(cMinute).length()<2){
                            cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE3);
                        }else{
                            cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        load();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.4724227, -70.7699159);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

    }

    public void piden(){

        sRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&&dataSnapshot.child(uidDriver).hasChild("Customer Uid")) {
                    final ArrayList<String> arrayDriver = new ArrayList<String>();
                    final ArrayList<String> arrayClient = new ArrayList<String>();

                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String uid = infoSnapshot.getKey();
                        String client = (String) infoSnapshot.child("Customer Uid").getValue();
                        arrayClient.add(client);
                        arrayDriver.add(uid);
                    }
                    final int v = arrayDriver.indexOf(uidDriver);
                    final Firebase sRefChild = sRef.child(uidDriver);
                    Firebase mRefChild = pRef.child("On Way Drivers");
                    final Firebase mRefChild2 = mRefChild.child("Santiago");
                    final Firebase mRefChild3 = mRefChild2.child(uidDriver);
                    final Firebase mRefChild4 = mRefChild3.child("Customer Uid");
                    final Firebase mRefChild5 = mRefChild3.child("Driver Latitude");
                    final Firebase mRefChild6 = mRefChild3.child("Driver Longitude");


                    alertDialog.setTitle("Están solicitando un Kamegaroo");
                    alertDialog.setMessage("¿Aceptas el viaje?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            uidClient = arrayClient.get(v);
                            mRefChild4.setValue(uidClient);
                            mRefChild5.setValue(lat);
                            mRefChild6.setValue(lng);
                            sRef.child(uidDriver).removeValue();
                            fab.setVisibility(View.VISIBLE);
                            btnFindPath.setVisibility(View.GONE);
                            alertDialog.closeOptionsMenu();

                        }
                    });
                    alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            send();
                            fab.setVisibility(View.VISIBLE);
                            btnFindPath.setVisibility(View.GONE);

                        }
                    });
                    alertDialog.show();
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            sRef.child(uidDriver).removeValue();


                        }
                    }, 10000);




                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });




    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }



    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latLngDriver = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngDriver);
        lat = location.getLatitude();
        lng = location.getLongitude();
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngDriver));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates/
        /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/
//autocompletado edittext origen con localización actual
        try
        {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder str = new StringBuilder();
            if (geocoder.isPresent())
            {
                Address returnAddress = addresses.get(0);

                String direccion = returnAddress.getAddressLine(0)+", "+returnAddress.getAddressLine(1)+", "+returnAddress.getAddressLine(3);

                str.append(direccion);


                dire = returnAddress.getAddressLine(2);

            }
        } catch (IOException e)
        {
            Log.e("tag", e.getMessage());
        }



    }

    public void send() {

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Firebase mRefChild = pRef.child("Available Drivers");
        Firebase mRefChild2 = mRefChild.child("Santiago");
        Firebase mRefChild3 = mRefChild2.child(uid);
        final Firebase mRefChild4 = mRefChild3.child("Latitude");
        final Firebase mRefChild5 = mRefChild3.child("Longitude");
        mRefChild4.setValue(lat);
        mRefChild5.setValue(lng);
        sRef.child(uidDriver).removeValue();
        nRef.child(uidDriver).removeValue();

        /*String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();

        if (origin.isEmpty()) {
            Toast.makeText(getActivity(), "Ingrese dirección de origen!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (destination.isEmpty()) {
            Toast.makeText(getActivity(), "Ingrese dirección de destino!", Toast.LENGTH_SHORT).show();
            return;
        }else if(origin.equals(destination)){
            Toast.makeText(getActivity(), "No se puede generar la ruta, direcciones de origen y destino son iguales", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra(MESSAGE_KEY,origin);
        intent.putExtra(MESSAGE_KEYS,destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);*/

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onDirectionFinderStart() {
        /*progressDialog = ProgressDialog.show(getActivity(), "Un momento.",
                "Generando ruta..!", true);
*/


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
        //progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        for (Route route : routes) {
            mMap.clear();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.kan))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finaly))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            builder.include(latLngDriver);
            builder.include(route.endLocation);

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(119, 21, 204)).
                    width(8);

            for (int i = 0; i < route.points.size(); i++){
                polylineOptions.add(route.points.get(i));
                builder.include(route.points.get(i));}

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

        }

    }


}