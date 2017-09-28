package com.driver.hp.komegaroodriver.Fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinderListener;
import com.driver.hp.komegaroodriver.Fragment.Modules.Route;
import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.RoundedTransformation;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private DatabaseReference mRef, sRef, tRef, cTravels, dTravels, set, rDriverStatus, stateDriver, stateClient, tripState, customer, pagoDriver;
    private String uidDriver, fDirec, tDirec, estado, estadoTrip;
    public String key, uidClient;
    private StringBuilder str2;
    View mMapView, fValorizar, travelData, userView, pagoFrag, seb1, seb2, seb3, seb4, seb5;
    private LatLng latLngDriver;
    private Calendar calander, calander2, calendar, calendar2, calendarCancel;
    private AlertDialog alertDialog, cancelCustomer;
    public AlertDialog cancelDriver;
    private Geocoder geocoder;
    private SeekBar sb, sb2, sb3, sb4, sb5;
    private Integer price;
    private TextView name, destino, conect, descon, iniciar, finalizar, retornar;
    private ImageView user;
    private FloatingActionButton infor;
    protected DatabaseReference paymentStatus;
    protected DatabaseReference payment;
    protected DatabaseReference drivers;
    protected String tokenPago;
    protected String customerId;
    protected String paymentMethod;
    protected String status;
    protected String statusDetail;
    protected String blackToken;
    protected String lastD;
    protected Integer priceKm;
    protected Integer priceTime;
    protected Integer tripsDriver;
    protected Integer tripsClient;
    protected String email;
    protected String nombre;
    protected String token;
    protected String code;
    protected String device;
    protected boolean returnTravel;
    protected boolean actionW=true;
    protected boolean actionT=true;
    protected boolean actionR=true;
    protected boolean setCust=true;
    protected boolean setDriv=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(Places.GEO_DATA_API).build();
        payment = FirebaseDatabase.getInstance().getReference().child("customerPayments");
        paymentStatus = FirebaseDatabase.getInstance().getReference().child("paymentStatus");
        mRef = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("availableDrivers").child("Santiago");
        sRef = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("requestedDrivers").child("Santiago");
        rDriverStatus = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("driverCoordenates").child("Santiago");
        tRef = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        cTravels = FirebaseDatabase.getInstance().getReference().child("customerTravels");
        dTravels = FirebaseDatabase.getInstance().getReference().child("driverTravels");
        stateDriver = FirebaseDatabase.getInstance().getReference().child("driverState");
        stateClient = FirebaseDatabase.getInstance().getReference().child("customerState");
        tripState = FirebaseDatabase.getInstance().getReference().child("tripState");
        customer = FirebaseDatabase.getInstance().getReference().child("customers");
        drivers = FirebaseDatabase.getInstance().getReference().child("drivers");
        pagoDriver = FirebaseDatabase.getInstance().getReference().child("driverPayments");
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "monserrat/Montserrat-SemiBold.ttf");
        cancelDriver = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        cancelCustomer = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sb = (SeekBar) v.findViewById(R.id.myseek);
        conect = (TextView)v.findViewById(R.id.txtConectarse);
        conect.setTypeface(face);
        seb1 = v.findViewById(R.id.seek1);
        sb2 = (SeekBar) v.findViewById(R.id.myseek2);
        descon = (TextView)v.findViewById(R.id.txtDesconectarse);
        descon.setTypeface(face);
        seb2 = v.findViewById(R.id.seek2);
        sb3 = (SeekBar) v.findViewById(R.id.myseek3);
        iniciar = (TextView)v.findViewById(R.id.txtIniciar);
        iniciar.setTypeface(face);
        seb3 = v.findViewById(R.id.seek3);
        sb4 = (SeekBar) v.findViewById(R.id.myseek4);
        finalizar = (TextView)v.findViewById(R.id.txtFinalizar);
        finalizar.setTypeface(face);
        seb4 = v.findViewById(R.id.seek4);
        sb5 = (SeekBar) v.findViewById(R.id.myseek5);
        retornar = (TextView)v.findViewById(R.id.txtRetornar);
        retornar.setTypeface(face);
        seb5 = v.findViewById(R.id.seek5);
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        fValorizar = v.findViewById(R.id.valorizar);
        fValorizar.setVisibility(View.GONE);
        name = (TextView) v.findViewById(R.id.txtNameData);
        destino = (TextView) v.findViewById(R.id.txtDestinoData);
        user = (ImageView) v.findViewById(R.id.userImage);
        infor = (FloatingActionButton)v.findViewById(R.id.btnInfo);
        infor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userView.setVisibility(View.VISIBLE);
            }
        });
        infor.setVisibility(View.GONE);
        travelData = v.findViewById(R.id.dataTravel);
        travelData.setVisibility(View.GONE);
        userView = v.findViewById(R.id.userData);
        userView.setVisibility(View.GONE);
        pagoFrag = v.findViewById(R.id.fragmentPago);
        pagoFrag.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        getUidClient();
        pagoExiste();
        delete();
        piden();
        onWay();
        statusDriver();
        slideButtons();
        getDriver();
        mRef.child(uidDriver).removeValue();
        return v;
    }

    public void notificationReceiver(){
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        int requestCode = 100;
        PendingIntent broadcast = PendingIntent.getBroadcast(getActivity(), requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),broadcast);
    }
    public void slideButtons() {
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int n = seekBar.getProgress();
                if (n > 98) {
                    ((MainActivity) getActivity()).lockedDrawer();
                    seb2.setVisibility(View.VISIBLE);
                    seb1.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    send();
                } else {seekBar.setProgress(1);}
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 26) {
                    conect.setText("C o n e c t a r s e");
                } else if (progress >= 68) {
                    conect.setText("");
                } else {
                    switch (progress) {
                        case 26:conect.setText("  o n e c t a r s e");
                            break;
                        case 31:conect.setText("    n e c t a r s e");
                            break;
                        case 36:conect.setText("      e c t a r s e");
                            break;
                        case 41:conect.setText("        c t a r s e");
                            break;
                        case 46:conect.setText("          t a r s e");
                            break;
                        case 49:conect.setText("            a r s e");
                            break;
                        case 54:conect.setText("              r s e");
                            break;
                        case 58:conect.setText("                s e");
                            break;
                        case 63:conect.setText("                  e");
                            break;
                    }}
            }
        });
        sb2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int b = seekBar.getProgress();
                if (b > 98) {
                    ((MainActivity) getActivity()).unlockedDrawer();
                    seb1.setVisibility(View.VISIBLE);
                    seb2.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    mRef.child(uidDriver).removeValue();
                    buildGoogleApiClient();
                } else {seekBar.setProgress(1);}
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 18) {
                    descon.setText("D e s c o n e c t a r s e");
                } else if (progress >= 74) {
                    descon.setText("");
                } else {
                    switch (progress) {
                        case 18:descon.setText("  e s c o n e c t a r s e");
                            break;
                        case 23:descon.setText("    s c o n e c t a r s e");
                            break;
                        case 27:descon.setText("      c o n e c t a r s e");
                            break;
                        case 32:descon.setText("        o n e c t a r s e");
                            break;
                        case 37:descon.setText("          n e c t a r s e");
                            break;
                        case 42:descon.setText("            e c t a r s e");
                            break;
                        case 47:descon.setText("              c t a r s e");
                            break;
                        case 52:descon.setText("                t a r s e");
                            break;
                        case 55:descon.setText("                  a r s e");
                            break;
                        case 60:descon.setText("                    r s e");
                            break;
                        case 64:descon.setText("                      s e");
                            break;
                        case 68:descon.setText("                        e");
                            break;
                    }}
            }
        });
        sb3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int v = seekBar.getProgress();
                if (v > 98) {
                    showDataTravels();
                    seb2.setVisibility(View.GONE);
                    seb3.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    setOnTrip();
                } else {seekBar.setProgress(1);}
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 14) {
                    iniciar.setText("I n i c i a r   E n t r e g a");
                } else if (progress >= 78) {
                    iniciar.setText("");
                } else {
                    switch (progress) {
                        case 14:iniciar.setText("  n i c i a r   E n t r e g a");
                            break;
                        case 19:iniciar.setText("    i c i a r   E n t r e g a");
                            break;
                        case 23:iniciar.setText("      c i a r   E n t r e g a");
                            break;
                        case 28:iniciar.setText("        i a r   E n t r e g a");
                            break;
                        case 32:iniciar.setText("          a r   E n t r e g a");
                            break;
                        case 37:iniciar.setText("            r   E n t r e g a");
                            break;
                        case 41:iniciar.setText("                E n t r e g a");
                            break;
                        case 50:iniciar.setText("                  n t r e g a");
                            break;
                        case 55:iniciar.setText("                    t r e g a");
                            break;
                        case 58:iniciar.setText("                      r e g a");
                            break;
                        case 62:iniciar.setText("                        e g a");
                            break;
                        case 67:iniciar.setText("                          g a");
                            break;
                        case 72:iniciar.setText("                            a");
                            break;
                    }}
            }
        });
        sb4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int l = seekBar.getProgress();
                if (l > 98) {
                    postGetPayments();
                    seb2.setVisibility(View.VISIBLE);
                    seb4.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    mMap.clear();
                    setFinish();
                    setFinishTravel();
                    buildGoogleApiClient();
                    stateDriver.child(uidDriver).child("state").setValue("endTrip");
                    stateClient.child(uidClient).child("state").setValue("endTrip");
                    tripState.child(uidClient).child("state").setValue("end");
                    rDriverStatus.child(uidDriver).removeValue();
                    travelData.setVisibility(View.GONE);
                    infor.setVisibility(View.GONE);
                    fValorizar.setVisibility(View.VISIBLE);
                    actionT = true;
                    actionW = true;
                    actionR = true;
                } else {seekBar.setProgress(1);}
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 11) {
                    finalizar.setText("F i n a l i z a r   E n t r e g a");
                } else if (progress >= 81) {
                    finalizar.setText("");
                } else {
                    switch (progress) {
                        case 11:finalizar.setText("  i n a l i z a r   E n t r e g a");
                            break;
                        case 16:finalizar.setText("    n a l i z a r   E n t r e g a");
                            break;
                        case 20:finalizar.setText("      a l i z a r   E n t r e g a");
                            break;
                        case 24:finalizar.setText("        l i z a r   E n t r e g a");
                            break;
                        case 27:finalizar.setText("          i z a r   E n t r e g a");
                            break;
                        case 31:finalizar.setText("            z a r   E n t r e g a");
                            break;
                        case 36:finalizar.setText("              a r   E n t r e g a");
                            break;
                        case 41:finalizar.setText("                r   E n t r e g a");
                            break;
                        case 45:finalizar.setText("                    E n t r e g a");
                            break;
                        case 54:finalizar.setText("                      n t r e g a");
                            break;
                        case 59:finalizar.setText("                        t r e g a");
                            break;
                        case 62:finalizar.setText("                          r e g a");
                            break;
                        case 66:finalizar.setText("                            e g a");
                            break;
                        case 72:finalizar.setText("                              g a");
                            break;
                        case 76:finalizar.setText("                                a");
                            break;
                    }}
            }
        });
        sb5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int l = seekBar.getProgress();
                if (l > 98) {
                    seekBar.setProgress(1);
                    seb5.setVisibility(View.GONE);
                    mMap.clear();
                    showDataTravels();
                    postOnReturn();
                    stateDriver.child(uidDriver).child("state").setValue("onReturn");
                    stateClient.child(uidClient).child("state").setValue("onReturn");
                    returnTravel = false;
                } else {seekBar.setProgress(1);}
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 30) {
                    retornar.setText("R e t o r n a r");
                } else if (progress >= 62) {
                    retornar.setText("");
                } else {
                    switch (progress) {
                        case 30:retornar.setText("  e t o r n a r");
                            break;
                        case 34:retornar.setText("    t o r n a r");
                            break;
                        case 39:retornar.setText("      o r n a r");
                            break;
                        case 43:retornar.setText("        r n a r");
                            break;
                        case 48:retornar.setText("          n a r");
                            break;
                        case 52:retornar.setText("            a r");
                            break;
                        case 57:retornar.setText("              r");
                            break;
                    }}
            }
        });
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
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {mRef.child(uidDriver).removeValue();}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
        sRef.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {mRef.child(uidDriver).removeValue();}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            send();
                            alertDialog.dismiss();
                            uidClient = null;}}
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {}
                });}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
        rDriverStatus.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {rDriverStatus.child(uidDriver).removeValue();}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
    public void load() {
        mRef.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                    mRef.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
                    mRef.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());}
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
        rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                    rDriverStatus.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
                    rDriverStatus.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());}
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
    public void statusDriver() {
        stateDriver.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    estado = mapS.get("state");}
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void getReturn(){
        tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<Boolean, Boolean> mapB = (Map<Boolean, Boolean>) dataSnapshot.getValue();
                    returnTravel = mapB.get("return");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getData() {
        if(uidClient!=null) {
            tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        Map<Long, Long> map = (Map<Long, Long>) dataSnapshot.getValue();
                        fDirec = mapS.get("from");
                        tDirec = mapS.get("to");
                        code = mapS.get("certificatedNumber");
                        price = map.get("price").intValue();
                        priceKm = map.get("kPrice").intValue();
                        priceTime = map.get("tPrice").intValue();
                        if (fDirec != null || tDirec != null) {
                            switch (estado) {
                                case "onWay":
                                    try {
                                        String lat = String.valueOf(mLastLocation.getLatitude());
                                        String lng = String.valueOf(mLastLocation.getLongitude());
                                        new DirectionFinder(MapsFragment.this, lat+","+lng, fDirec).execute();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    setCustomerTravel();
                                    setDriversTravels();
                                    showButtonOnWay();
                                    destino.setText(fDirec);
                                    break;
                                case "onTrip":
                                    try {
                                        String lat = String.valueOf(mLastLocation.getLatitude());
                                        String lng = String.valueOf(mLastLocation.getLongitude());
                                        new DirectionFinder(MapsFragment.this, lat+","+lng, tDirec).execute();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    showButtonOnTrip();
                                    destino.setText(tDirec);
                                    break;
                                case "onReturn":
                                    try {
                                        String lat = String.valueOf(mLastLocation.getLatitude());
                                        String lng = String.valueOf(mLastLocation.getLongitude());
                                        new DirectionFinder(MapsFragment.this, lat+","+lng, fDirec).execute();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    showButtonOnReturn();
                                    destino.setText(fDirec);
                                    break;
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {}
            });
        }
    }
    public void onWay() {
        rDriverStatus.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mRef.child(uidDriver).removeValue();
                sRef.child(uidDriver).removeValue();
                getData();
                canceled();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getData();
                canceled();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                rDriverStatus.child(uidDriver).removeValue();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
    public void setOnTrip() {
        rDriverStatus.child(uidDriver).child("customerUid").setValue(uidClient);
        rDriverStatus.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
        rDriverStatus.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
        stateDriver.child(uidDriver).child("state").setValue("onTrip");
        stateClient.child(uidClient).child("state").setValue("onTrip");
        cTravels.child(uidClient).child(key).child("status").setValue("onTrip");
        dTravels.child(uidDriver).child(key).child("status").setValue("onTrip");
    }
    public void canceled() {
        cancelDriver.setTitle("Cancelar viaje");
        cancelDriver.setMessage("¿Quieres cancelar el viaje?");
        cancelDriver.setCancelable(false);
        cancelDriver.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rDriverStatus.child(uidDriver).removeValue();
                        tripState.child(uidClient).child("state").setValue("canceledByDriver");
                        cTravels.child(uidClient).child(key).child("status").setValue("canceledByDriver");
                        infor.setVisibility(View.GONE);
                        seb4.setVisibility(View.GONE);
                        seb2.setVisibility(View.VISIBLE);
                        seb3.setVisibility(View.GONE);
                        send();
                        stateDriver.child(uidDriver).child("state").setValue("nil");
                        stateClient.child(uidClient).child("state").setValue("nil");
                        cTravels.child(uidClient).child(key).child("tripPrice").setValue(0);
                        dTravels.child(uidDriver).child(key).child("tripPrice").setValue(0);
                        cTravels.child(uidClient).child(key).child("status").setValue("canceledByDriver");
                        dTravels.child(uidDriver).child(key).child("status").setValue("canceledByDriver");
                        tRef.child(uidClient).removeValue();
                        FinishCanceled();
                        mMap.clear();
                        buildGoogleApiClient();
                        travelData.setVisibility(View.GONE);
                        cancelDriver.dismiss();
                        actionW=true;
                        actionT=true;
                        actionR=true;
                    }
                });
        cancelDriver.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelDriver.dismiss();
                    }
                });
        cancelCustomer.setTitle("Viaje Cancelado");
        cancelCustomer.setMessage("Cliente ha cancelado el viaje.");
        cancelCustomer.setCancelable(false);
        cancelCustomer.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rDriverStatus.child(uidDriver).removeValue();
                        cTravels.child(uidClient).child(key).child("status").setValue("canceledByCustomer");
                        uidClient = null;
                        send();
                        infor.setVisibility(View.GONE);
                        seb4.setVisibility(View.GONE);
                        seb2.setVisibility(View.VISIBLE);
                        seb3.setVisibility(View.GONE);
                        mMap.clear();
                        buildGoogleApiClient();
                        travelData.setVisibility(View.GONE);
                        cancelCustomer.dismiss();
                        actionW=true;
                        actionT=true;
                        actionR=true;
                    }
                });
        if(uidClient!=null) {
            tripState.child(uidClient).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        estadoTrip = mapS.get("state");
                        if (estadoTrip.equals("canceledByCustomer")&&uidClient!=null) {
                            stateDriver.child(uidDriver).child("state").setValue("nil");
                            cancelCustomer.show();
                        }}
                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {}
            });
        }
    }
    public void setCustomerTravel() {
        if(setCust) {
            setCust = false;
            Log.v("From", fDirec);
            Log.v("To", tDirec);
            calander = Calendar.getInstance();
            int cDay = calander.get(Calendar.DAY_OF_MONTH);
            int cMonth = calander.get(Calendar.MONTH) + 1;
            int cYear = calander.get(Calendar.YEAR);
            int cHour = calander.get(Calendar.HOUR_OF_DAY);
            int cMinute = calander.get(Calendar.MINUTE);
            String fecha = cDay + "/" + cMonth + "/" + cYear;
            String fecha2 = "0" + cDay + "/" + "0" + cMonth + "/" + cYear;
            String fecha3 = cDay + "/" + "0" + cMonth + "/" + cYear;
            String fecha4 = "0" + cDay + "/" + cMonth + "/" + cYear;
            String hora = cHour + ":" + cMinute;
            String hora2 = "0" + cHour + ":" + "0" + cMinute;
            String hora3 = cHour + ":" + "0" + cMinute;
            String hora4 = "0" + cHour + ":" + cMinute;
            int viajesC = tripsClient + 1;
            customer.child(uidClient).child("trips").setValue(viajesC);
            cTravels.child(uidClient).child(key).child("customerUid").setValue(uidClient);
            cTravels.child(uidClient).child(key).child("driverUid").setValue(uidDriver);
            cTravels.child(uidClient).child(key).child("status").setValue("onWay");
            cTravels.child(uidClient).child(key).child("from").setValue(fDirec);
            cTravels.child(uidClient).child(key).child("to").setValue(tDirec);
            cTravels.child(uidClient).child(key).child("calification").setValue("");
            cTravels.child(uidClient).child(key).child("comments").setValue("");
            cTravels.child(uidClient).child(key).child("tripPrice").setValue(price);
            if (String.valueOf(cDay).length() < 2 && String.valueOf(cMonth).length() < 2) {
                cTravels.child(uidClient).child(key).child("date").setValue(fecha2);
            } else if (String.valueOf(cMonth).length() < 2) {
                cTravels.child(uidClient).child(key).child("date").setValue(fecha3);
            } else if (String.valueOf(cDay).length() < 2) {
                cTravels.child(uidClient).child(key).child("date").setValue(fecha4);
            } else {
                cTravels.child(uidClient).child(key).child("date").setValue(fecha);
            }
            if (String.valueOf(cHour).length() < 2 && String.valueOf(cMinute).length() < 2) {
                cTravels.child(uidClient).child(key).child("startHour").setValue(hora2);
            } else if (String.valueOf(cHour).length() < 2) {
                cTravels.child(uidClient).child(key).child("startHour").setValue(hora4);
            } else if (String.valueOf(cMinute).length() < 2) {
                cTravels.child(uidClient).child(key).child("startHour").setValue(hora3);
            } else {
                cTravels.child(uidClient).child(key).child("startHour").setValue(hora);
            }
            cTravels.child(uidClient).child(key).child("endHour").setValue("");
            if(!code.equals("")) {
                cTravels.child(uidClient).child(key).child("certificatedNumber").setValue(code);
            }else{
                cTravels.child(uidClient).child(key).child("certificatedNumber").setValue("");
            }
        }
    }
    public void setFinish() {
        calander2 = Calendar.getInstance();
        int cHour = calander2.get(Calendar.HOUR_OF_DAY);
        int cMinute = calander2.get(Calendar.MINUTE);
        String horaE = cHour + ":" + cMinute;
        String horaE2 = "0" + cHour + ":" + "0" + cMinute;
        String horaE3 = cHour + ":" + "0" + cMinute;
        String horaE4 = "0" + cHour + ":" + cMinute;
        if (String.valueOf(cHour).length() < 2 && String.valueOf(cMinute).length() < 2) {
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE2);
            rDriverStatus.child(uidDriver).removeValue();
            cTravels.child(uidClient).child(key).child("status").setValue("endTrip");
            setCust = true;
        } else if (String.valueOf(cHour).length() < 2) {
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE4);
            rDriverStatus.child(uidDriver).removeValue();
            cTravels.child(uidClient).child(key).child("status").setValue("endTrip");
            setCust = true;
        } else if (String.valueOf(cMinute).length() < 2) {
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE3);
            rDriverStatus.child(uidDriver).removeValue();
            cTravels.child(uidClient).child(key).child("status").setValue("endTrip");
            setCust = true;
        } else {
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE);
            rDriverStatus.child(uidDriver).removeValue();
            cTravels.child(uidClient).child(key).child("status").setValue("endTrip");
            setCust = true;
        }
    }

    public void getDriver(){
        drivers.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<Long, Long> map = (Map<Long, Long>) dataSnapshot.getValue();
                    tripsDriver = map.get("trips").intValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setDriversTravels() {
        if(setDriv) {
            setDriv = false;
            Log.v("From", fDirec);
            Log.v("To", tDirec);
            calendar = Calendar.getInstance();
            int cDay = calendar.get(Calendar.DAY_OF_MONTH);
            int cMonth = calendar.get(Calendar.MONTH) + 1;
            int cYear = calendar.get(Calendar.YEAR);
            int cHour = calendar.get(Calendar.HOUR_OF_DAY);
            int cMinute = calendar.get(Calendar.MINUTE);
            String fecha = cDay + "/" + cMonth + "/" + cYear;
            String fecha2 = "0" + cDay + "/" + "0" + cMonth + "/" + cYear;
            String fecha3 = cDay + "/" + "0" + cMonth + "/" + cYear;
            String fecha4 = "0" + cDay + "/" + cMonth + "/" + cYear;
            String hora = cHour + ":" + cMinute;
            String hora2 = "0" + cHour + ":" + "0" + cMinute;
            String hora3 = cHour + ":" + "0" + cMinute;
            String hora4 = "0" + cHour + ":" + cMinute;
            int viajesD = tripsDriver + 1;
            drivers.child(uidDriver).child("trips").setValue(viajesD);
            dTravels.child(uidDriver).child(key).child("customerUid").setValue(uidClient);
            dTravels.child(uidDriver).child(key).child("driverUid").setValue(uidDriver);
            dTravels.child(uidDriver).child(key).child("status").setValue("onWay");
            dTravels.child(uidDriver).child(key).child("from").setValue(fDirec);
            dTravels.child(uidDriver).child(key).child("to").setValue(tDirec);
            dTravels.child(uidDriver).child(key).child("calification").setValue("");
            dTravels.child(uidDriver).child(key).child("comments").setValue("");
            dTravels.child(uidDriver).child(key).child("tripPrice").setValue(price);
            if (String.valueOf(cDay).length() < 2 && String.valueOf(cMonth).length() < 2) {
                dTravels.child(uidDriver).child(key).child("date").setValue(fecha2);
            } else if (String.valueOf(cMonth).length() < 2) {
                dTravels.child(uidDriver).child(key).child("date").setValue(fecha3);
            } else if (String.valueOf(cDay).length() < 2) {
                dTravels.child(uidDriver).child(key).child("date").setValue(fecha4);
            } else {
                dTravels.child(uidDriver).child(key).child("date").setValue(fecha);
            }
            if (String.valueOf(cHour).length() < 2 && String.valueOf(cMinute).length() < 2) {
                dTravels.child(uidDriver).child(key).child("startHour").setValue(hora2);
            } else if (String.valueOf(cHour).length() < 2) {
                dTravels.child(uidDriver).child(key).child("startHour").setValue(hora4);
            } else if (String.valueOf(cMinute).length() < 2) {
                dTravels.child(uidDriver).child(key).child("startHour").setValue(hora3);
            } else {
                dTravels.child(uidDriver).child(key).child("startHour").setValue(hora);
            }
            dTravels.child(uidDriver).child(key).child("endHour").setValue("");
            if(!code.equals("")) {
                dTravels.child(uidDriver).child(key).child("certificatedNumber").setValue(code);
            }else {
                dTravels.child(uidDriver).child(key).child("certificatedNumber").setValue("");
            }
        }
    }
    public void setFinishTravel() {
        calendar2 = Calendar.getInstance();
        int cHour = calendar2.get(Calendar.HOUR_OF_DAY);
        int cMinute = calendar2.get(Calendar.MINUTE);
        String horaE = cHour + ":" + cMinute;
        String horaE2 = "0" + cHour + ":" + "0" + cMinute;
        String horaE3 = cHour + ":" + "0" + cMinute;
        String horaE4 = "0" + cHour + ":" + cMinute;
        if (String.valueOf(cHour).length() < 2 && String.valueOf(cMinute).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE2);
            dTravels.child(uidDriver).child(key).child("status").setValue("endTrip");
            setDriv = true;
        } else if (String.valueOf(cHour).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE4);
            dTravels.child(uidDriver).child(key).child("status").setValue("endTrip");
            setDriv = true;
        } else if (String.valueOf(cMinute).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE3);
            dTravels.child(uidDriver).child(key).child("status").setValue("endTrip");
            setDriv = true;
        } else {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE);
            dTravels.child(uidDriver).child(key).child("status").setValue("endTrip");
            setDriv = true;
        }
    }

    public void FinishCanceled(){
        calendarCancel = Calendar.getInstance();
        int cHour = calendarCancel.get(Calendar.HOUR_OF_DAY);
        int cMinute = calendarCancel.get(Calendar.MINUTE);
        String horaE = cHour + ":" + cMinute;
        String horaE2 = "0" + cHour + ":" + "0" + cMinute;
        String horaE3 = cHour + ":" + "0" + cMinute;
        String horaE4 = "0" + cHour + ":" + cMinute;
        if (String.valueOf(cHour).length() < 2 && String.valueOf(cMinute).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE2);
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE2);
            setDriv = true;
            setCust = true;
            uidClient = null;
        } else if (String.valueOf(cHour).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE4);
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE4);
            setDriv = true;
            setCust = true;
            uidClient = null;
        } else if (String.valueOf(cMinute).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE3);
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE3);
            setDriv = true;
            setCust = true;
            uidClient = null;
        } else {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE);
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE);
            setDriv = true;
            setCust = true;
            uidClient = null;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        buildGoogleApiClient();
    }
    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.connect();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-33.4724227, -70.7699159);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 9));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);
    }
    public void piden() {
        sRef.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("customerUid")) {
                    notificationReceiver();
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    uidClient = mapS.get("customerUid");
                    alertDialog.setTitle("Están solicitando un Kamegaroo");
                    alertDialog.setMessage("¿Aceptas el viaje?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(dataSnapshot.exists()) {
                                seb2.setVisibility(View.GONE);
                                showDataTravels();
                                ((MainActivity) getActivity()).lockedDrawer();
                                rDriverStatus.child(uidDriver).child("customerUid").setValue(uidClient);
                                rDriverStatus.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
                                rDriverStatus.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
                                stateDriver.child(uidDriver).child("state").setValue("onWay");
                                stateClient.child(uidClient).child("state").setValue("onWay");
                                tripState.child(uidClient).child("state").setValue("ok");
                                sRef.child(uidDriver).removeValue();
                                key = cTravels.child(uidClient).push().getKey();
                                getDataPayment();
                                getReturn();
                            }
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            send();
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
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
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);}
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
        load();
        rDriverStatus.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngDriver));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));}
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
    public void send() {
        if(mGoogleApiClient!=null) {
            mRef.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
            mRef.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
            sRef.child(uidDriver).removeValue();
            rDriverStatus.child(uidDriver).removeValue();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission. ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Permiso")
                        .setMessage("Concedes?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
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

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission. ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission. ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Request location updates:
                if(mMap!=null&&mGoogleApiClient.isConnected()) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
    }
    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();}
        }
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();}
        }
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();}
        }
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        for (Route route : routes) {
            mMap.clear();
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.kan))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finaly))
                    .title(route.endAddress)
                    .position(route.endLocation)));
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.rgb(119, 21, 204)).
                    width(8).
                    visible(true).
                    zIndex(5);
            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }
            polylinePaths.add(mMap.addPolyline(polylineOptions));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 17));
        }
    }
    public void showDataTravels() {
        customer.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                Map<Long, Long> map = (Map<Long, Long>) dataSnapshot.getValue();
                nombre = mapS.get("name");
                email = mapS.get("email");
                token = mapS.get("deviceToken");
                device = mapS.get("device");
                tripsClient = map.get("trips").intValue();
                String photo = mapS.get("photoUrl");
                name.setText(nombre);
                Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(9, 1)).into(user);
                user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userView.setVisibility(View.VISIBLE);
                    }
                });
                Log.v("ClienteTrips",tripsClient.toString());
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
        travelData.setVisibility(View.VISIBLE);
    }
    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist;
    }
    public void showButtonOnWay(){
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        try {
            address = coder.getFromLocationName(fDirec, 5);
            if (address == null) {}
            Address location = address.get(0);
            Double distancia = distFrom(mLastLocation.getLatitude(),mLastLocation.getLongitude(),location.getLatitude(),location.getLongitude());
            Log.v("DistanciaInicio",distancia.toString());
            if(distancia<1000){
                postOnWay();
                seb3.setVisibility(View.VISIBLE);
                travelData.setVisibility(View.GONE);
                infor.setVisibility(View.VISIBLE);
            }else{
                seb3.setVisibility(View.GONE);
                travelData.setVisibility(View.VISIBLE);
                infor.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showButtonOnTrip() {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        try {
            address = coder.getFromLocationName(tDirec, 5);
            if (address == null) {}
            Address location = address.get(0);
            Double distancia = distFrom(mLastLocation.getLatitude(),mLastLocation.getLongitude(),location.getLatitude(),location.getLongitude());
            Log.v("DistanciaFinal",distancia.toString());
            if(distancia<1000&&!returnTravel){
                postOnTrip();
                seb4.setVisibility(View.VISIBLE);
                travelData.setVisibility(View.GONE);
                infor.setVisibility(View.VISIBLE);
            }else if(distancia<1000&&returnTravel&&returnTravel){
                seb5.setVisibility(View.VISIBLE);
                travelData.setVisibility(View.GONE);
                infor.setVisibility(View.VISIBLE);
            }else{
                seb4.setVisibility(View.GONE);
                seb5.setVisibility(View.GONE);
                travelData.setVisibility(View.VISIBLE);
                infor.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showButtonOnReturn() {
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        try {
            address = coder.getFromLocationName(fDirec, 5);
            if (address == null) {}
            Address location = address.get(0);
            Double distancia = distFrom(mLastLocation.getLatitude(),mLastLocation.getLongitude(),location.getLatitude(),location.getLongitude());
            Log.v("DistanciaFinal",distancia.toString());
            if(distancia<1000&&!returnTravel){
                postOnTrip();
                seb4.setVisibility(View.VISIBLE);
                travelData.setVisibility(View.GONE);
                infor.setVisibility(View.VISIBLE);
            }else{
                seb4.setVisibility(View.GONE);
                seb5.setVisibility(View.GONE);
                travelData.setVisibility(View.VISIBLE);
                infor.setVisibility(View.GONE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void pagoExiste(){
        pagoDriver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(uidDriver)){
                    pagoFrag.setVisibility(View.VISIBLE);}
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
    public void getUidClient(){
        rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    uidClient = mapS.get("customerUid");
                    getLastKey();
                    seb1.setVisibility(View.GONE);}
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
    public void getLastKey(){
        cTravels.child(uidClient).orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                            key = mapS.keySet().toString().replace("[","").replace("]","");
                            getDataPayment();
                            getReturn();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {}
                });
    }

    public static MapsFragment newInstance(String text) {
        MapsFragment f = new MapsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    public void getDataPayment(){
        payment.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    customerId = mapS.get("payer");
                    paymentMethod = mapS.get("paymentMethod");
                    tokenPago = mapS.get("token");
                    blackToken = mapS.get("blackToken");
                    lastD = mapS.get("lastDigits");
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {}
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

    public void postGetPayments(){
        String url = "https://komegaroo-server.herokuapp.com/payments/payment";
        String body ="amount="+price+"&token="+tokenPago+"&paymentMethod="+paymentMethod+"&payer="+customerId;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("POSTNoPayments!", e.getMessage());
                paymentStatus.child(uidClient).child("status").setValue("declined");
                paymentStatus.child(uidClient).child("error").setValue("Error when charging customer, no response from server");
                paymentStatus.child(uidClient).child("debt").setValue(price);
                postAddBlackList();
                postFailPayment();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("POSTYesPayments!", responseStr);
                    try {
                        parseJSon7(responseStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String responseStr = response.body().string();
                    Log.v("POSTNoPayments!", responseStr);
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Error when charging customer, failed payment");
                    paymentStatus.child(uidClient).child("debt").setValue(price);
                    postAddBlackList();
                    postFailPayment();
                }
            }
        });
    }

    private void parseJSon7(String data) throws JSONException {
        if (data == null)
            return;
        JSONObject jsonData = new JSONObject(data);
        status = jsonData.getString("status");
        statusDetail = jsonData.getString("status_detail");
        Log.v("Status",status);
        Log.v("StatusD",statusDetail);
        if( !status.equals("approved")) {
            switch (statusDetail) {
                case "cc_rejected_callfor_authorize":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Not authorized");
                    paymentStatus.child(uidClient).child("debt").setValue(price);
                    postAddBlackList();
                    postFailPayment();
                    break;
                case "cc_rejected_insufficient_amount":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Insufficient amount");
                    paymentStatus.child(uidClient).child("debt").setValue(price);
                    postAddBlackList();
                    postFailPayment();
                    break;
                case "cc_rejected_bad_filled_security_code":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Bad security code");
                    paymentStatus.child(uidClient).child("debt").setValue(price);
                    postAddBlackList();
                    postFailPayment();
                    break;
                case "cc_rejected_bad_filled_date":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Expired Date");
                    paymentStatus.child(uidClient).child("debt").setValue(price);
                    postAddBlackList();
                    postFailPayment();
                    break;
                case "cc_rejected_bad_filled_other":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("From error");
                    paymentStatus.child(uidClient).child("debt").setValue(price);
                    postAddBlackList();
                    postFailPayment();
                    break;
                case "cc_rejected_other_reason":
                    paymentStatus.child(uidClient).child("status").setValue("declined");
                    paymentStatus.child(uidClient).child("error").setValue("Other");
                    paymentStatus.child(uidClient).child("debt").setValue(price);
                    postAddBlackList();
                    postFailPayment();
                    break;
            }
        }else{
            paymentStatus.child(uidClient).child("status").setValue("approved");
            paymentStatus.child(uidClient).child("error").setValue("nil");
            paymentStatus.child(uidClient).child("debt").setValue(0);
            postSuccesPayment();
        }
    }

    public void postAddBlackList(){
        String url = "https://komegaroo-server.herokuapp.com/cards/addBlackList";
        String body ="number="+blackToken;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("postBlackList", responseStr);
                } else {
                    String responseStr = response.body().string();
                    Log.v("postBlackList", responseStr);
                }
            }
        });
    }

    public void postFailPayment(){
        String url = "https://komegaroo-server.herokuapp.com/mobile/failurePaymentEmail";
        String body ="email="+email+"&name="+nombre+"&amount="+price;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("postFailPaymentNo", e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("postFailPaymentYes", responseStr);
                } else {
                    String responseStr = response.body().string();
                    Log.v("postFailPaymentNo", responseStr);
                }
            }
        });
    }

    public void postSuccesPayment(){
        String url = "https://komegaroo-server.herokuapp.com/mobile/succesPaymentEmail";
        String body ="email="+email+"&name="+nombre+"&kPrice="+priceKm+"&tPrice="+priceTime+"&lastDigits="+lastD;
        post(url,body,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("postSuccesPaymentNo", e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.v("postSuccesPaymentYes", responseStr);
                } else {
                    String responseStr = response.body().string();
                    Log.v("postSuccesPaymentNo", responseStr);
                }
            }
        });
    }

    public void postOnWay(){
        if(actionW) {
            actionW = false;
            String url = "https://komegaroo-server.herokuapp.com/mobile/notification";
            String message = "Tu canguro está llegando a retirar tu pedido.";
            String payload = "d";
            String body = "token=" + token + "&message=" + message + "&payload=" + payload + "&package=" + device;
            post(url, body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        Log.v("POSTOnWayYes!", responseStr);
                    } else {
                        String responseStr = response.body().string();
                        Log.v("POSTOnWayNo!", responseStr);
                    }
                }
            });
        }
    }
    public void postOnReturn(){
        if(actionR) {
            actionR = false;
            String url = "https://komegaroo-server.herokuapp.com/mobile/notification";
            String message = "Tu canguro está retornando al punto de origen.";
            String payload = "d";
            String body = "token=" + token + "&message=" + message + "&payload=" + payload + "&package=" + device;
            post(url, body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        Log.v("POSTOnWayYes!", responseStr);
                    } else {
                        String responseStr = response.body().string();
                        Log.v("POSTOnWayNo!", responseStr);
                    }
                }
            });
        }
    }
    public void postOnTrip(){
        if(actionT) {
            actionT = false;
            String url = "https://komegaroo-server.herokuapp.com/mobile/notification";
            String message = "Tu canguro está llegando a destino.";
            String payload = "d";
            String body = "token=" + token + "&message=" + message + "&payload=" + payload + "&package=" + device;
            post(url, body, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {}
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        Log.v("POSTOnTripYes!", responseStr);
                    } else {
                        String responseStr = response.body().string();
                        Log.v("POSTOnTripNo!", responseStr);
                    }
                }
            });
        }
    }
}