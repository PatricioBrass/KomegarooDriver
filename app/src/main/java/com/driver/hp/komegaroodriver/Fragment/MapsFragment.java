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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
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
    private Firebase mRef, sRef, tRef, cTravels, dTravels, set, rDriverStatus, stateDriver, stateClient, tripState, customer, pagoDriver;
    private String uidDriver, fDirec, tDirec, estado, estadoTrip;
    public String key, uidClient;
    private StringBuilder str, str2;
    View mMapView, fValorizar, travelData, userView, pagoFrag;
    private LatLng latLngDriver;
    private Calendar calander, calander2, calendar, calendar2;
    private AlertDialog alertDialog, cancelCustomer;
    public AlertDialog cancelDriver;
    private Geocoder geocoder, geocoder2;
    private SeekBar sb, sb2, sb3, sb4;
    private Integer price;
    private TextView name, destino;
    private ImageView user;
    private FloatingActionButton infor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main, container, false);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(Places.GEO_DATA_API).build();
        Firebase.setAndroidContext(getActivity());
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
        cancelDriver = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        cancelCustomer = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sb = (SeekBar) v.findViewById(R.id.myseek);
        //sb.setEnabled(false);
        sb2 = (SeekBar) v.findViewById(R.id.myseek2);
        sb3 = (SeekBar) v.findViewById(R.id.myseek3);
        sb4 = (SeekBar) v.findViewById(R.id.myseek4);
        sb2.setVisibility(View.GONE);
        sb3.setVisibility(View.GONE);
        sb4.setVisibility(View.GONE);
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        geocoder2 = new Geocoder(getActivity(), Locale.ENGLISH);
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
                    sb2.setVisibility(View.VISIBLE);
                    sb.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    send();
                } else {
                    seekBar.setProgress(1);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.v("ProgressP", String.valueOf(seekBar.getProgress()));
                Log.v("ProgressD", String.valueOf(seekBar.getProgressDrawable()));
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 30) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse0));
                } else if (progress >= 72) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse10));
                } else {
                    switch (progress) {
                        case 30:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse1));
                            break;
                        case 35:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse2));
                            break;
                        case 40:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse3));
                            break;
                        case 45:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse4));
                            break;
                        case 50:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse5));
                            break;
                        case 54:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse6));
                            break;
                        case 59:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse7));
                            break;
                        case 63:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse8));
                            break;
                        case 67:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.conectarse9));
                            break;
                    }
                }
            }
        });

        sb2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int b = seekBar.getProgress();
                if (b > 98) {
                    ((MainActivity) getActivity()).unlockedDrawer();
                    sb.setVisibility(View.VISIBLE);
                    sb2.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    mRef.child(uidDriver).removeValue();
                    buildGoogleApiClient();
                } else {
                    seekBar.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("Progress", String.valueOf(progress));
                if (progress < 21) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse0));
                } else if (progress >= 77) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse13));
                } else {
                    switch (progress) {
                        case 21:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse1));
                            break;
                        case 26:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse2));
                            break;
                        case 30:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse3));
                            break;
                        case 35:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse4));
                            break;
                        case 40:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse5));
                            break;
                        case 45:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse6));
                            break;
                        case 50:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse7));
                            break;
                        case 55:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse8));
                            break;
                        case 59:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse9));
                            break;
                        case 64:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse10));
                            break;
                        case 68:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse11));
                            break;
                        case 72:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.desconectarse12));
                            break;
                    }
                }
            }
        });

        sb3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int v = seekBar.getProgress();
                if (v > 98) {
                    showDataTravels();
                    sb2.setVisibility(View.GONE);
                    sb3.setVisibility(View.GONE);
                    seekBar.setProgress(1);
                    setOnTrip();
                    setCustomerTravel();
                    setDriversTravels();
                } else {
                    seekBar.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("Progress", String.valueOf(progress));
                if (progress < 19) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega0));
                } else if (progress >= 77) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega14));
                } else {
                    switch (progress) {
                        case 19:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega1));
                            break;
                        case 24:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega2));
                            break;
                        case 27:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega3));
                            break;
                        case 31:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega4));
                            break;
                        case 34:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega5));
                            break;
                        case 38:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega6));
                            break;
                        case 42:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega7));
                            break;
                        case 50:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega8));
                            break;
                        case 55:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega9));
                            break;
                        case 59:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega10));
                            break;
                        case 62:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega11));
                            break;
                        case 67:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega12));
                            break;
                        case 72:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.iniciar_entrega13));
                            break;
                    }
                }
            }
        });

        sb4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int l = seekBar.getProgress();
                if (l > 98) {
                    sb.setVisibility(View.VISIBLE);
                    sb4.setVisibility(View.GONE);
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
                } else {
                    seekBar.setProgress(1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.v("Progress", String.valueOf(progress));
                if (progress < 18) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega0));
                } else if (progress >= 81) {
                    seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega16));
                } else {
                    switch (progress) {
                        case 18:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega1));
                            break;
                        case 21:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega2));
                            break;
                        case 26:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega3));
                            break;
                        case 30:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega4));
                            break;
                        case 33:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega5));
                            break;
                        case 35:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega6));
                            break;
                        case 40:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega7));
                            break;
                        case 44:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega8));
                            break;
                        case 48:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega9));
                            break;
                        case 55:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega10));
                            break;
                        case 60:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega11));
                            break;
                        case 63:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega12));
                            break;
                        case 67:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega13));
                            break;
                        case 72:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega14));
                            break;
                        case 76:seekBar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.finalizar_entrega15));
                            break;
                    }
                }
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
        rDriverStatus.child(uidDriver).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                rDriverStatus.child(uidDriver).removeValue();
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
        mRef.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                    mRef.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
                    mRef.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("latitude") && dataSnapshot.hasChild("longitude")) {
                    rDriverStatus.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
                    rDriverStatus.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void statusDriver() {
        stateDriver.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    estado = mapS.get("state");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
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
                        if (fDirec != null || tDirec != null) {
                            showDataTravels();
                            try {
                                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                                str2 = new StringBuilder();
                                if (geocoder.isPresent()) {
                                    Address returnAddress = addresses.get(0);
                                    String direccion = returnAddress.getAddressLine(0) + ", " + returnAddress.getAddressLine(1) + ", " + returnAddress.getAddressLine(3);
                                    str2.append(direccion);
                                }
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
                                sb2.setVisibility(View.GONE);
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
                public void onCancelled(FirebaseError firebaseError) {
                }
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
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
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
                        sb4.setVisibility(View.GONE);
                        sb.setVisibility(View.VISIBLE);
                        sb3.setVisibility(View.GONE);
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
                        sb4.setVisibility(View.GONE);
                        sb.setVisibility(View.VISIBLE);
                        sb3.setVisibility(View.GONE);
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
                            cancelCustomer.show();
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
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
                            sRef.child(uidDriver).removeValue();
                            tripState.child(uidClient).child("state").setValue("ok");
                            key = cTravels.child(uidClient).push().getKey();
                            cTravels.child(uidClient).child(key).child("driverUid").setValue(uidDriver);
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
                    Timer timer4 = new Timer();
                    timer4.schedule(new TimerTask() {
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
        load();
        rDriverStatus.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngDriver));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void send() {
        mRef.child(uidDriver).child("latitude").setValue(mLastLocation.getLatitude());
        mRef.child(uidDriver).child("longitude").setValue(mLastLocation.getLongitude());
        sRef.child(uidDriver).removeValue();
        rDriverStatus.child(uidDriver).removeValue();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

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
            } else {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
            }
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
                String nombre = mapS.get("name");
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
            public void onCancelled(FirebaseError firebaseError) {

            }
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
            if (address == null) {
            }
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
            if(distancia1<50){
                sb3.setVisibility(View.VISIBLE);
                travelData.setVisibility(View.GONE);
                infor.setVisibility(View.VISIBLE);
            }else{
                sb3.setVisibility(View.GONE);
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
            if (address == null) {
            }
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
            if(distancia1<50){
                sb4.setVisibility(View.VISIBLE);
                travelData.setVisibility(View.GONE);
                infor.setVisibility(View.VISIBLE);
            }else{
                sb4.setVisibility(View.GONE);
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
                    sb.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
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
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }
}