package com.driver.hp.komegaroodriver.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinderListener;
import com.driver.hp.komegaroodriver.Fragment.Modules.Route;
import com.driver.hp.komegaroodriver.MainActivity;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
/**Created by HP on 18/10/2016.*/
public class  MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, DirectionFinderListener {
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    Marker mCurrLocationMarker;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Firebase mRef, nRef, sRef, cRef, tRef, cTravels, dTravels;
    private Double lat, lng;
    private String  uidDriver, fDirec, tDirec, key;
    public String uidClient;
    private StringBuilder str, str2;
    View mMapView, fValorizar;
    private Timer timer2, timer, timer1;
    private LatLng latLngDriver;
    private Calendar calander, calander2, calendar, calendar2;
    private AlertDialog alertDialog, alertDialogOnWay, alertDialogOnTrip;
    private Geocoder geocoder, geocoder2;
    private SeekBar sb, sb2, sb3, sb4;
    private int index;
    private Integer price;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(Places.GEO_DATA_API).build();
        Firebase.setAndroidContext(getActivity());
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Available Drivers/Santiago");
        nRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/On Way Drivers/Santiago");
        sRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Requested Drivers/Santiago");
        cRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Status/Driver On Trip/Santiago");
        tRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Requested Travels/Santiago");
        cTravels = new Firebase("https://decoded-pilot-144921.firebaseio.com/Customers Travels");
        dTravels = new Firebase("https://decoded-pilot-144921.firebaseio.com/Drivers Travels");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sb = (SeekBar)v.findViewById(R.id.myseek);
        sb2 = (SeekBar)v.findViewById(R.id.myseek2);
        sb3 = (SeekBar)v.findViewById(R.id.myseek3);
        sb4 = (SeekBar)v.findViewById(R.id.myseek4);
        sb2.setVisibility(View.GONE);
        sb3.setVisibility(View.GONE);
        sb4.setVisibility(View.GONE);
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialogOnTrip = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialogOnWay = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        geocoder2 = new Geocoder(getActivity(), Locale.ENGLISH);
        timer = new Timer();
        timer1 = new Timer();
        timer2 = new Timer();
        fValorizar = v.findViewById(R.id.valorizar);
        fValorizar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();}
        delete();
        piden();
        load();
        onWay();
        driverOnTrip();
        slideButtons();
        mRef.child(uidDriver).removeValue();
        nRef.child(uidDriver).removeValue();
        cRef.child(uidDriver).removeValue();
        return v;}

    public void slideButtons(){
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int n = seekBar.getProgress();
                if(n < 90) {
                    seekBar.setProgress(1);
                }else{((MainActivity)getActivity()).lockedDrawer();
                        sb2.setVisibility(View.VISIBLE);
                        sb.setVisibility(View.GONE);
                        seekBar.setProgress(1);
                        timer = new Timer();
                        send();}}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {}});
        sb2.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int b = seekBar.getProgress();
                if(b < 90) {
                    seekBar.setProgress(1);
                }else{((MainActivity)getActivity()).unlockedDrawer();
                        sb.setVisibility(View.VISIBLE);
                        sb2.setVisibility(View.GONE);
                        seekBar.setProgress(1);
                        mRef.child(uidDriver).removeValue();
                        nRef.child(uidDriver).removeValue();
                        buildGoogleApiClient();
                        timer.cancel();}}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {}});
        sb3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int v = seekBar.getProgress();
                if(v < 90){
                    seekBar.setProgress(1);
                }else{sb4.setVisibility(View.VISIBLE);
                    sb2.setVisibility(View.GONE);
                    sb3.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    setOnTrip();
                    setCustomerTravel();
                    setDriversTravels();
                    timer1.cancel();
                    timer2 = new Timer();}}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}});
        sb4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int l = seekBar.getProgress();
                if(l < 90) {
                    seekBar.setProgress(1);
                }else{sb.setVisibility(View.VISIBLE);
                    sb4.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    cRef.child(uidDriver).removeValue();
                    mMap.clear();
                    setFinish();
                    setFinishTravel();
                    valorizar();
                    buildGoogleApiClient();
                    timer2.cancel();}}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}});}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapView = fragment.getView();
        fragment.getMapAsync(this);}

    public void valorizar() {
            cTravels.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final ArrayList<String> arrayKeys = new ArrayList<>();
                        final ArrayList<String> arrayCalif = new ArrayList<>();
                        ArrayList<String> arrayClients = new ArrayList<>();
                        final ArrayList<String> arrayDrivers = new ArrayList<>();
                        for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()){
                            String keys = infoSnapshot.getKey();
                            String uidClients = (String) infoSnapshot.child("customerUid").getValue();
                            String uidDrivers = (String) infoSnapshot.child("driverUid").getValue();
                            String calification = (String) infoSnapshot.child("calification").getValue();
                            arrayKeys.add(keys);
                            arrayClients.add(uidClients);
                            arrayDrivers.add(uidDrivers);
                            arrayCalif.add(calification);
                        }
                                    if (arrayCalif.contains("")) {
                                        index = arrayCalif.indexOf("");
                                        String driver = arrayDrivers.get(index);
                                        if(uidDriver.equals(driver)){
                                            Log.v("DRIVER!", driver);
                                            fValorizar.setVisibility(View.VISIBLE);}}}}
                @Override
                public void onCancelled(FirebaseError firebaseError) {}});}

    public void delete() {
        mRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                mRef.child(uidDriver).removeValue();}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});
        sRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRef.child(uidDriver).removeValue();}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});}

    public void load() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRef.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                            mRef.child(uidDriver).child("latitude").setValue(lat);
                            mRef.child(uidDriver).child("longitude").setValue(lng);}}
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}});}}, 1000, 3000);
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                nRef.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("driverLatitude") && dataSnapshot.hasChild("driverLongitude")) {
                            nRef.child(uidDriver).child("driverLatitude").setValue(lat);
                            nRef.child(uidDriver).child("driverLongitude").setValue(lng);}}
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}});}}, 1000, 3000);
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                cRef.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("driverLatitude") && dataSnapshot.hasChild("driverLongitude")) {
                            cRef.child(uidDriver).child("driverLatitude").setValue(lat);
                            cRef.child(uidDriver).child("driverLongitude").setValue(lng);}}
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}});    }
        }, 1000, 3000);
    }

    public void getData() {
        tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    Map<Integer, Integer> map = dataSnapshot.getValue(Map.class);
                    fDirec = mapS.get("from");
                    tDirec = mapS.get("to");
                    price = map.get("price");
                if(fDirec!=null||tDirec!=null){
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLngDriver.latitude, latLngDriver.longitude, 1);
                    str2 = new StringBuilder();
                    if (geocoder.isPresent()) {
                        Address returnAddress = addresses.get(0);
                        String direccion = returnAddress.getAddressLine(0) + ", " + returnAddress.getAddressLine(1) + ", " + returnAddress.getAddressLine(3);
                        str2.append(direccion);}
                } catch (IOException e) {Log.e("tag", e.getMessage());}
                try {
                    new DirectionFinder(MapsFragment.this, str2.toString(), fDirec).execute();
                } catch (UnsupportedEncodingException e) {e.printStackTrace();}
                sb3.setVisibility(View.VISIBLE);
                sb2.setVisibility(View.GONE);}}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});}

    public void onWay() {
        alertDialogOnWay.setTitle("Viaje cancelado");
        alertDialogOnWay.setMessage("El cliente ha cancelado el viaje.");
        alertDialogOnWay.setCancelable(false);
        alertDialogOnWay.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sb4.setVisibility(View.GONE);
                        sb.setVisibility(View.VISIBLE);
                        sb3.setVisibility(View.GONE);
                        mMap.clear();
                        ((MainActivity)getActivity()).unlockedDrawer();
                        buildGoogleApiClient();
                        alertDialogOnWay.dismiss();
                        timer1.cancel();}});
        nRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRef.child(uidDriver).removeValue();
                sRef.child(uidDriver).removeValue();
                getData();}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getData();}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                nRef.child(uidDriver).removeValue();
                cRef.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            alertDialogOnWay.show();}}
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}});}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});
        }

    public void setOnTrip() {
        cRef.child(uidDriver).child("customerUid").setValue(uidClient);
        cRef.child(uidDriver).child("driverLatitude").setValue(lat);
        cRef.child(uidDriver).child("driverLongitude").setValue(lng);
        nRef.child(uidDriver).removeValue();
    }

    public void driverOnTrip(){
        cRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                nRef.child(uidDriver).removeValue();
                onTrip();}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                onTrip();}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                cRef.child(uidDriver).removeValue();}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});
    }

    public void onTrip(){
                    if (fDirec != null || tDirec != null) {
                    try {
                        List<Address> addresses = geocoder2.getFromLocation(latLngDriver.latitude, latLngDriver.longitude, 1);
                        str = new StringBuilder();
                        if (geocoder2.isPresent()) {
                            Address returnAddress = addresses.get(0);
                            String direccion = returnAddress.getAddressLine(0) + ", " + returnAddress.getAddressLine(1) + ", " + returnAddress.getAddressLine(3);
                            str.append(direccion);}
                    } catch (IOException e) {Log.e("tag", e.getMessage());}
                    try {
                        new DirectionFinder(MapsFragment.this, str.toString(), tDirec).execute();
                    } catch (UnsupportedEncodingException e) {e.printStackTrace();}
                    sb4.setVisibility(View.VISIBLE);
                    sb3.setVisibility(View.GONE);
                    tRef.child(uidClient).removeValue();}
    }

    public void setCustomerTravel(){
        Log.v("From", fDirec);
        Log.v("To", tDirec);
        calander = Calendar.getInstance();
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
        }else{set.child("date").setValue(fecha);}
        if(String.valueOf(cHour).length()<2&&String.valueOf(cMinute).length()<2){
            set.child("startHour").setValue(hora2);
        }else if(String.valueOf(cHour).length()<2){
            set.child("startHour").setValue(hora4);
        }else if(String.valueOf(cMinute).length()<2){
            set.child("startHour").setValue(hora3);
        }else{set.child("startHour").setValue(hora);}
        set.child("endHour").setValue("");
        set.child("code").setValue("");
    }
    public void setFinish(){
        cTravels.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> arrayKeys = new ArrayList<>();
                    ArrayList<String> arrayEnd = new ArrayList<>();
                    calander2 = Calendar.getInstance();
                    int cHour = calander2.get(Calendar.HOUR_OF_DAY);
                    int cMinute = calander2.get(Calendar.MINUTE);
                    String horaE = cHour+":"+cMinute;
                    String horaE2 = "0"+cHour+":"+"0"+cMinute;
                    String horaE3 = cHour+":"+"0"+cMinute;
                    String horaE4 = "0"+cHour+":"+cMinute;
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String keys = infoSnapshot.getKey();
                        String endH = (String) infoSnapshot.child("endHour").getValue();
                        arrayKeys.add(keys);
                        arrayEnd.add(endH);
                    }
                    if(arrayEnd.contains("")){
                        int indeX = arrayEnd.indexOf("");
                        String keyS = arrayKeys.get(indeX);
                        if(String.valueOf(cHour).length()<2&&String.valueOf(cMinute).length()<2){
                            cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE2);
                        }else if(String.valueOf(cHour).length()<2){
                            cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE4);
                        }else if(String.valueOf(cMinute).length()<2){
                            cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE3);
                        }else{cTravels.child(uidClient).child(keyS).child("endHour").setValue(horaE);}}}}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});
    }

    public void setDriversTravels(){
        Log.v("From", fDirec);
        Log.v("To", tDirec);
        calendar = Calendar.getInstance();
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        int cMonth = calendar.get(Calendar.MONTH) + 1;
        int cYear = calendar.get(Calendar.YEAR);
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        int cMinute = calendar.get(Calendar.MINUTE);
        String fecha = cDay+"/"+cMonth+"/"+cYear;
        String fecha2 = "0"+cDay+"/"+"0"+cMonth+"/"+cYear;
        String fecha3 = cDay+"/"+"0"+cMonth+"/"+cYear;
        String fecha4 = "0"+cDay+"/"+cMonth+"/"+cYear;
        String hora = cHour+":"+cMinute;
        String hora2 = "0"+cHour+":"+"0"+cMinute;
        String hora3 = cHour+":"+"0"+cMinute;
        String hora4 = "0"+cHour+":"+cMinute;
        Firebase set = dTravels.child(uidDriver).push();
        set.child("customerUid").setValue(uidClient);
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
        }else{set.child("date").setValue(fecha);}
        if(String.valueOf(cHour).length()<2&&String.valueOf(cMinute).length()<2){
            set.child("startHour").setValue(hora2);
        }else if(String.valueOf(cHour).length()<2){
            set.child("startHour").setValue(hora4);
        }else if(String.valueOf(cMinute).length()<2){
            set.child("startHour").setValue(hora3);
        }else{set.child("startHour").setValue(hora);}
        set.child("endHour").setValue("");
        set.child("code").setValue("");
    }

    public void setFinishTravel(){
        dTravels.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<String> arrayKeys = new ArrayList<String>();
                    ArrayList<String> arrayEnd = new ArrayList<String>();
                    calendar2 = Calendar.getInstance();
                    int cHour = calendar2.get(Calendar.HOUR_OF_DAY);
                    int cMinute = calendar2.get(Calendar.MINUTE);
                    String horaE = cHour+":"+cMinute;
                    String horaE2 = "0"+cHour+":"+"0"+cMinute;
                    String horaE3 = cHour+":"+"0"+cMinute;
                    String horaE4 = "0"+cHour+":"+cMinute;
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String keys = infoSnapshot.getKey();
                        String endH = (String) infoSnapshot.child("endHour").getValue();
                        arrayKeys.add(keys);
                        arrayEnd.add(endH);
                    }
                    if(arrayEnd.contains("")){
                        int indeX = arrayEnd.indexOf("");
                        String keyS = arrayKeys.get(indeX);
                        if(String.valueOf(cHour).length()<2&&String.valueOf(cMinute).length()<2){
                            dTravels.child(uidDriver).child(keyS).child("endHour").setValue(horaE2);
                        }else if(String.valueOf(cHour).length()<2){
                            dTravels.child(uidDriver).child(keyS).child("endHour").setValue(horaE4);
                        }else if(String.valueOf(cMinute).length()<2){
                            dTravels.child(uidDriver).child(keyS).child("endHour").setValue(horaE3);
                        }else{dTravels.child(uidDriver).child(keyS).child("endHour").setValue(horaE);}}}}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();}
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();}
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.4724227, -70.7699159);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);}
        } else {buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);}
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }
    public void piden(){
        sRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&&dataSnapshot.child(uidDriver).hasChild("customerUid")) {
                    final ArrayList<String> arrayDriver = new ArrayList<>();
                    final ArrayList<String> arrayClient = new ArrayList<>();
                    for (DataSnapshot infoSnapshot : dataSnapshot.getChildren()) {
                        String uid = infoSnapshot.getKey();
                        String client = (String) infoSnapshot.child("customerUid").getValue();
                        arrayClient.add(client);
                        arrayDriver.add(uid);
                    }
                    final int v = arrayDriver.indexOf(uidDriver);
                        alertDialog.setTitle("Están solicitando un Kamegaroo");
                        alertDialog.setMessage("¿Aceptas el viaje?");
                        alertDialog.setCancelable(false);
                        alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity)getActivity()).lockedDrawer();
                            uidClient = arrayClient.get(v);
                            nRef.child(uidDriver).child("customerUid").setValue(uidClient);
                            nRef.child(uidDriver).child("driverLatitude").setValue(lat);
                            nRef.child(uidDriver).child("driverLongitude").setValue(lng);
                            sRef.child(uidDriver).removeValue();
                            alertDialog.closeOptionsMenu();
                            timer.cancel();
                            timer1 = new Timer();}});
                    alertDialog.setButton2("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            send();
                            alertDialog.closeOptionsMenu();}});
                    alertDialog.show();
                    Timer timer4 = new Timer();
                    timer4.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            sRef.child(uidDriver).removeValue();}}, 10000);}}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});
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
    public void onConnectionSuspended(int i) {}

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();}
        latLngDriver = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngDriver);
        lat = location.getLatitude();
        lng = location.getLongitude();
        nRef.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    cRef.child(uidDriver).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngDriver));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));}}
                        @Override
                        public void onCancelled(FirebaseError firebaseError) {}});}}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}});
    }

    public void send() {
        mRef.child(uidDriver).child("latitude").setValue(lat);
        mRef.child(uidDriver).child("longitude").setValue(lat);
        sRef.child(uidDriver).removeValue();
        nRef.child(uidDriver).removeValue();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);}
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode)
        {    case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();}
                        mMap.setMyLocationEnabled(true);}
                } else {Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();}}
        }
    }
    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }}
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }}
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }}
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        for (Route route : routes) {
            mMap.clear();
            //LatLngBounds.Builder builder = new LatLngBounds.Builder();
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.kan))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finaly))
                    .title(route.endAddress)
                    .position(route.endLocation)));
            //builder.include(route.startLocation);
            //builder.include(route.endLocation);
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(119, 21, 204)).
                    width(8);
            for (int i = 0; i < route.points.size(); i++){
                polylineOptions.add(route.points.get(i));
                //builder.include(route.points.get(i));
                }
            polylinePaths.add(mMap.addPolyline(polylineOptions));
            //LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 18));
        }
    }
}