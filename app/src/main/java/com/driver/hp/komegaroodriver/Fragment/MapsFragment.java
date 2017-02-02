package com.driver.hp.komegaroodriver.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

/**
 * Created by HP on 18/10/2016.
 */

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int PLACE_PICKER_FLAG = 1;
    public static final String MESSAGE_KEY="com.kome.hp.komegarooandroid.message_key";
    public static final String MESSAGE_KEYS="com.kome.hp.komegarooandroid.message_keys";
    private PlacesAutoCompleteAdapter mPlacesAdapter;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Button btnFindPath, fab;
    private ImageButton btnFindPath2, btnFindPath3;
    private AutoCompleteTextView etOrigin;
    private AutoCompleteTextView etDestination;
    private LatLngBounds mBounds;
    private AutocompleteFilter mPlaceFilter;
    private CharSequence constraint;
    private Firebase mRef, mRef2, nRef, pRef, sRef;
    private Double lat, lng;
    private Integer u;
    private ArrayList<String> arrayStatus = new ArrayList<>();
    private ArrayList<String> arrayClient = new ArrayList<>();
    private ArrayList<String> arrayDriver = new ArrayList<>();
    private String dire, uidDriver, driv;
    View mMapView;
    private Timer timer2 = new Timer();
    private Timer timer = new Timer();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();
        Firebase.setAndroidContext(getActivity());
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Available Drivers/Santiago");
        nRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/On Way Drivers/Santiago");
        sRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Requested Drivers/Santiago");
        pRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnFindPath = (Button) v.findViewById(R.id.btnFindPath);
        btnFindPath2 = (ImageButton) v.findViewById(R.id.imageButton);
        btnFindPath3 = (ImageButton) v.findViewById(R.id.imageButton2);
        etOrigin = (AutoCompleteTextView) v.findViewById(R.id.etOrigin);
        etDestination = (AutoCompleteTextView) v.findViewById(R.id.etDestination);
        etOrigin.setOnItemClickListener(mAutocompleteClickListener);
        etDestination.setOnItemClickListener(mAutocompleteClickListener);
        mPlacesAdapter = new PlacesAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
                mGoogleApiClient, null, null);
        etOrigin.setAdapter(mPlacesAdapter);
        etDestination.setAdapter(mPlacesAdapter);


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
                fab.setVisibility(View.GONE);
                btnFindPath.setVisibility(View.VISIBLE);
            }
        });
        fab.setVisibility(View.GONE);

        btnFindPath2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                etOrigin.setText("");
            }
        });
        btnFindPath3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                etDestination.setText("");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        delete();
        piden();
        load();


        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapView = fragment.getView();
        fragment.getMapAsync(this);




    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_FLAG:
                    Place place = PlacePicker.getPlace(data, getActivity());
                    etOrigin.setText(place.getName() + ", " + place.getAddress());

                    etDestination.setText(place.getName() + ", " + place.getAddress());
                    break;
            }
        }
    }



    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mPlacesAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow (view.getApplicationWindowToken (), 0);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("place", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
        }
    };

    public void delete(){
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
        sRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uidDriver)){
                    mRefChild4.removeValue();
                    mRefChild5.removeValue();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void load(){

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

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(uidDriver)&&dataSnapshot.child(uidDriver).hasChild("Latitude")&&dataSnapshot.child(uidDriver).hasChild("Longitude")){
                            mRefChild4.setValue(lat);
                            mRefChild5.setValue(lng);
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        },4000,7000);



        /*pRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Requested Drivers").child("Santiago").hasChild(uidDriver)){
                    timer.cancel();
                    pRef.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });*/
        Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                nRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(uidDriver)&&dataSnapshot.child(uidDriver).hasChild("Driver Latitude")&&dataSnapshot.child(uidDriver).hasChild("Driver Longitude")){
                            sRefChild5.setValue(lat);
                            sRefChild6.setValue(lng);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        },4000,7000);

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
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

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

                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setTitle("Están solicitando un Kamegaroo");
                                alertDialog.setMessage("¿Aceptas el viaje?");
                                alertDialog.setCancelable(false);
                                alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mRefChild4.setValue(arrayClient.get(v));
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

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        lat = location.getLatitude();
        lng = location.getLongitude();
        //move map camera
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

                etOrigin.setText(str);
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


}

