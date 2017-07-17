package com.driver.hp.komegaroodriver.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.Timer;
import java.util.TimerTask;

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
    private Firebase mRef, sRef, tRef, cTravels, dTravels, set, rDriverStatus, stateDriver, stateClient, tripState, customer, pagoDriver;
    private String uidDriver, fDirec, tDirec, estado, estadoTrip;
    public String key, uidClient;
    private StringBuilder str2;
    View mMapView, fValorizar, travelData, userView, pagoFrag, seb1, seb2, seb3, seb4;
    private LatLng latLngDriver;
    private Calendar calander, calander2, calendar, calendar2;
    private AlertDialog alertDialog, cancelCustomer;
    public AlertDialog cancelDriver;
    private Geocoder geocoder;
    private SeekBar sb, sb2, sb3, sb4;
    private Integer price;
    private TextView name, destino, conect, descon, iniciar, finalizar;
    private ImageView user;
    private FloatingActionButton infor;
    protected Firebase paymentStatus;
    protected Firebase payment;
    protected String tokenPago;
    protected String customerId;
    protected String paymentMethod;
    protected String status;
    protected String statusDetail;
    protected String blackToken;
    protected String lastD;
    protected Integer priceKm;
    protected Integer priceTime;
    protected String email;
    protected String nombre;
    protected String token;
    protected boolean actionW=true;
    protected boolean actionT=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(Places.GEO_DATA_API).build();
        Firebase.setAndroidContext(getActivity());
        payment = new Firebase("https://decoded-pilot-144921.firebaseio.com/customerPayments");
        paymentStatus = new Firebase("https://decoded-pilot-144921.firebaseio.com/paymentStatus");
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverStatus/availableDrivers/Santiago");
        sRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverStatus/requestedDrivers/Santiago");
        rDriverStatus = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverStatus/driverCoordenates/Santiago");
        tRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/requestedTravels/Santiago");
        cTravels = new Firebase("https://decoded-pilot-144921.firebaseio.com/customerTravels");
        dTravels = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverTravels");
        stateDriver = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverState");
        stateClient = new Firebase("https://decoded-pilot-144921.firebaseio.com/customerState");
        tripState = new Firebase("https://decoded-pilot-144921.firebaseio.com/tripState");
        customer = new Firebase("https://decoded-pilot-144921.firebaseio.com/customers");
        pagoDriver = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverPayments");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        getUidClient();
        pagoExiste();
        delete();
        piden();
        onWay();
        statusDriver();
        slideButtons();
        mRef.child(uidDriver).removeValue();
        return v;
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
                    setCustomerTravel();
                    setDriversTravels();
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
                    seb1.setVisibility(View.VISIBLE);
                    seb4.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    mMap.clear();
                    setFinish();
                    setFinishTravel();
                    buildGoogleApiClient();
                    stateDriver.child(uidDriver).child("state").setValue("endTrip");
                    stateClient.child(uidClient).child("state").setValue("endTrip");
                    rDriverStatus.child(uidDriver).removeValue();
                    tripState.child(uidClient).removeValue();
                    travelData.setVisibility(View.GONE);
                    infor.setVisibility(View.GONE);
                    fValorizar.setVisibility(View.VISIBLE);
                    actionT = true;
                    actionW = true;
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
            public void onCancelled(FirebaseError firebaseError) {}
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
                            alertDialog.dismiss();}}
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
                });}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
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
            public void onCancelled(FirebaseError firebaseError) {}
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
            public void onCancelled(FirebaseError firebaseError) {}
        });
        rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                    rDriverStatus.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
                    rDriverStatus.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());}
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
    public void statusDriver() {
        stateDriver.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    estado = mapS.get("state");}
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
    public void getData() {
        if(uidClient!=null) {
            tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                        Map<Integer, Integer> map = dataSnapshot.getValue(Map.class);
                        fDirec = mapS.get("from");
                        tDirec = mapS.get("to");
                        price = map.get("price");
                        priceKm = map.get("kPrice");
                        priceTime = map.get("tPrice");
                        if (fDirec != null || tDirec != null) {
                            showDataTravels();
                            try {
                                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                                str2 = new StringBuilder();
                                if (geocoder.isPresent()) {
                                    Address returnAddress = addresses.get(0);
                                    String direccion = returnAddress.getAddressLine(0) + ", " + returnAddress.getAddressLine(1) + ", " + returnAddress.getAddressLine(3);
                                    str2.append(direccion);}
                            } catch (IOException e) {
                                Log.e("tag", e.getMessage());
                            }
                            if (estado.equals("onWay")) {
                                try {
                                    new DirectionFinder(MapsFragment.this, str2.toString(), fDirec).execute();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                showButtonOnWay();
                                seb2.setVisibility(View.GONE);
                                destino.setText(fDirec);
                            } else if (estado.equals("onTrip")) {
                                try {
                                    new DirectionFinder(MapsFragment.this, str2.toString(), tDirec).execute();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                showButtonOnTrip();
                                destino.setText(tDirec);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {}
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
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
    public void setOnTrip() {
        rDriverStatus.child(uidDriver).child("customerUid").setValue(uidClient);
        rDriverStatus.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
        rDriverStatus.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
        stateDriver.child(uidDriver).child("state").setValue("onTrip");
        stateClient.child(uidClient).child("state").setValue("onTrip");
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
                        infor.setVisibility(View.GONE);
                        seb4.setVisibility(View.GONE);
                        seb1.setVisibility(View.VISIBLE);
                        seb3.setVisibility(View.GONE);
                        stateDriver.child(uidDriver).child("state").setValue("nil");
                        stateClient.child(uidClient).child("state").setValue("nil");
                        cTravels.child(uidClient).child(key).removeValue();
                        dTravels.child(uidDriver).child(key).removeValue();
                        mMap.clear();
                        ((MainActivity) getActivity()).unlockedDrawer();
                        buildGoogleApiClient();
                        travelData.setVisibility(View.GONE);
                        cancelDriver.dismiss();
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
                        dTravels.child(uidDriver).child(key).removeValue();
                        infor.setVisibility(View.GONE);
                        seb4.setVisibility(View.GONE);
                        seb1.setVisibility(View.VISIBLE);
                        seb3.setVisibility(View.GONE);
                        tripState.child(uidClient).removeValue();
                        cTravels.child(uidClient).child(key).removeValue();
                        mMap.clear();
                        ((MainActivity) getActivity()).unlockedDrawer();
                        buildGoogleApiClient();
                        travelData.setVisibility(View.GONE);
                        cancelCustomer.dismiss();
                    }
                });
        if(uidClient!=null) {
            tripState.child(uidClient).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                        estadoTrip = mapS.get("state");
                        if (estadoTrip.equals("canceledByCustomer")) {
                            stateDriver.child(uidDriver).child("state").setValue("nil");
                            cancelCustomer.show();}}
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }
    }
    public void setCustomerTravel() {
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
        set = cTravels.child(uidClient).child(key);
        set.child("customerUid").setValue(uidClient);
        set.child("from").setValue(fDirec);
        set.child("to").setValue(tDirec);
        set.child("calification").setValue("");
        set.child("comments").setValue("");
        set.child("tripPrice").setValue(price);
        if (String.valueOf(cDay).length() < 2 && String.valueOf(cMonth).length() < 2) {
            set.child("date").setValue(fecha2);
        } else if (String.valueOf(cMonth).length() < 2) {
            set.child("date").setValue(fecha3);
        } else if (String.valueOf(cDay).length() < 2) {
            set.child("date").setValue(fecha4);
        } else {
            set.child("date").setValue(fecha);
        }
        if (String.valueOf(cHour).length() < 2 && String.valueOf(cMinute).length() < 2) {
            set.child("startHour").setValue(hora2);
        } else if (String.valueOf(cHour).length() < 2) {
            set.child("startHour").setValue(hora4);
        } else if (String.valueOf(cMinute).length() < 2) {
            set.child("startHour").setValue(hora3);
        } else {
            set.child("startHour").setValue(hora);
        }
        set.child("endHour").setValue("");
        set.child("code").setValue("");
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
        } else if (String.valueOf(cHour).length() < 2) {
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE4);
            rDriverStatus.child(uidDriver).removeValue();
        } else if (String.valueOf(cMinute).length() < 2) {
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE3);
            rDriverStatus.child(uidDriver).removeValue();
        } else {
            cTravels.child(uidClient).child(key).child("endHour").setValue(horaE);
            rDriverStatus.child(uidDriver).removeValue();
        }
    }
    public void setDriversTravels() {
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
        set = dTravels.child(uidDriver).child(key);
        set.child("customerUid").setValue(uidClient);
        set.child("from").setValue(fDirec);
        set.child("to").setValue(tDirec);
        set.child("calification").setValue("");
        set.child("comments").setValue("");
        set.child("tripPrice").setValue(price);
        if (String.valueOf(cDay).length() < 2 && String.valueOf(cMonth).length() < 2) {
            set.child("date").setValue(fecha2);
        } else if (String.valueOf(cMonth).length() < 2) {
            set.child("date").setValue(fecha3);
        } else if (String.valueOf(cDay).length() < 2) {
            set.child("date").setValue(fecha4);
        } else {
            set.child("date").setValue(fecha);
        }
        if (String.valueOf(cHour).length() < 2 && String.valueOf(cMinute).length() < 2) {
            set.child("startHour").setValue(hora2);
        } else if (String.valueOf(cHour).length() < 2) {
            set.child("startHour").setValue(hora4);
        } else if (String.valueOf(cMinute).length() < 2) {
            set.child("startHour").setValue(hora3);
        } else {
            set.child("startHour").setValue(hora);
        }
        set.child("endHour").setValue("");
        set.child("code").setValue("");
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
        } else if (String.valueOf(cHour).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE4);
        } else if (String.valueOf(cMinute).length() < 2) {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE3);
        } else {
            dTravels.child(uidDriver).child(key).child("endHour").setValue(horaE);
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
        mGoogleApiClient.connect();
        super.onStop();
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
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("customerUid")) {
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    uidClient = mapS.get("customerUid");
                    alertDialog.setTitle("Están solicitando un Kamegaroo");
                    alertDialog.setMessage("¿Aceptas el viaje?");
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ((MainActivity) getActivity()).lockedDrawer();
                            rDriverStatus.child(uidDriver).child("customerUid").setValue(uidClient);
                            rDriverStatus.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
                            rDriverStatus.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
                            stateDriver.child(uidDriver).child("state").setValue("onWay");
                            stateClient.child(uidClient).child("state").setValue("onWay");
                            tripState.child(uidClient).child("state").setValue("ok");
                            sRef.child(uidDriver).removeValue();
                            key = cTravels.child(uidClient).push().getKey();
                            cTravels.child(uidClient).child(key).child("driverUid").setValue(uidDriver);
                            getDataPayment();
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
            public void onCancelled(FirebaseError firebaseError) {}
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
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
    public void send() {
        mRef.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
        mRef.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
        sRef.child(uidDriver).removeValue();
        rDriverStatus.child(uidDriver).removeValue();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();}
                        mMap.setMyLocationEnabled(true);}
                } else {Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();}
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
                    width(8);
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
                Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                nombre = mapS.get("name");
                email = mapS.get("email");
                token = mapS.get("deviceToken");
                String photo = mapS.get("photoUrl");
                name.setText(nombre);
                Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(9, 1)).into(user);
                user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userView.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
        travelData.setVisibility(View.VISIBLE);
    }
    public void showButtonOnWay(){
        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        Float distancia;
        Integer distancia1;
        try {
            address = coder.getFromLocationName(fDirec, 5);
            if (address == null) {}
            Address location = address.get(0);
            Location target = new Location("");
            Location target2 = new Location("");
            target.setLatitude(mLastLocation.getLatitude());
            target.setLongitude(mLastLocation.getLongitude());
            target2.setLatitude(location.getLatitude());
            target2.setLongitude(location.getLongitude());
            distancia = target.distanceTo(target2);
            distancia1 = distancia.intValue();
            Log.v("Distancia",distancia1.toString());
            if(distancia1<500){
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
        Float distancia;
        Integer distancia1;
        try {
            address = coder.getFromLocationName(tDirec, 5);
            if (address == null) {}
            Address location = address.get(0);
            Location target = new Location("");
            Location target2 = new Location("");
            target.setLatitude(mLastLocation.getLatitude());
            target.setLongitude(mLastLocation.getLongitude());
            target2.setLatitude(location.getLatitude());
            target2.setLongitude(location.getLongitude());
            distancia = target.distanceTo(target2);
            distancia1 = distancia.intValue();
            Log.v("Distancia",distancia1.toString());
            if(distancia1<500){
                postOnTrip();
                seb4.setVisibility(View.VISIBLE);
                travelData.setVisibility(View.GONE);
                infor.setVisibility(View.VISIBLE);
            }else{
                seb4.setVisibility(View.GONE);
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
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
    public void getUidClient(){
        rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    uidClient = mapS.get("customerUid");
                    getLastKey();
                    seb1.setVisibility(View.GONE);}
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }
    public void getLastKey(){
        cTravels.child(uidClient).orderByKey().limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                            key = mapS.keySet().toString().replace("[","").replace("]","");
                            getDataPayment();
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
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
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    customerId = mapS.get("payer");
                    paymentMethod = mapS.get("paymentMethod");
                    tokenPago = mapS.get("token");
                    blackToken = mapS.get("blackToken");
                    lastD = mapS.get("lastDigits");
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
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
            String packages = "com.kome.hp.komegarooandroid";
            String body = "token=" + token + "&message=" + message + "&payload=" + payload + "&package=" + packages;
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
            String packages = "com.kome.hp.komegarooandroid";
            String body = "token=" + token + "&message=" + message + "&payload=" + payload + "&package=" + packages;
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