package com.driver.hp.komegaroodriver.Fragment;


import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.Fragment.Modules.DirectionFinder;
import com.driver.hp.komegaroodriver.LoginActivity;
import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReturnFragment extends Fragment {

    String[] datosRetornos = {"Destino Cerrado","Destinatario Rechaza Entrega","Destinatario Rechaza Recepción","No Responden","Otros"};
    protected Spinner retorno;
    protected View layout;
    protected Button solicita, cancelar;
    protected TextView chrono;
    protected DatabaseReference validation, drivers, stateDriver, rDriverStatus, customer, tRef;
    protected String uidClient, uidDriver;
    protected String token, device;
    private boolean returnDriver = true;

    public ReturnFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_return, container, false);
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        stateDriver = FirebaseDatabase.getInstance().getReference().child("driverState");
        validation = FirebaseDatabase.getInstance().getReference().child("customerValidation");
        drivers = FirebaseDatabase.getInstance().getReference().child("drivers");
        rDriverStatus = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("driverCoordenates").child("Santiago");
        customer = FirebaseDatabase.getInstance().getReference().child("customers");
        tRef = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        solicita = (Button)v.findViewById(R.id.btnSolicitaRetorno);
        cancelar = (Button)v.findViewById(R.id.btnCancelRetorno);
        layout = v.findViewById(R.id.layoutCausa);
        chrono = (TextView)v.findViewById(R.id.txtChrono);
        retorno = (Spinner)v.findViewById(R.id.spinnerRetorno);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_selectable_list_item,datosRetornos);
        retorno.setAdapter(adapter);
        solicita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkUid())
                    return;
                sendData();
                layout.setVisibility(View.GONE);
                chrono.setVisibility(View.VISIBLE);
                postOnTrip();
                timer.start();
                getReturnValidation();
            }
        });
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFragment();
            }
        });
        getUidClient();
        return v;
    }

    public boolean checkUid(){
        if (uidClient==null){
            return false;
        }
        return true;
    }
    public void getUidClient(){
        rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    uidClient = mapS.get("customerUid");
                    getToken();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
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

    CountDownTimer timer = new CountDownTimer(60000, 1000) {
        public void onTick(long millisUntilFinished) {
            chrono.setText(""+String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
        }
        public void onFinish() {
            validation.child(uidClient).child("validateReturn").setValue("no");
            //stateDriver.child(uidDriver).child("state").setValue("onTripReturn");
        }
    };

    public void sendData(){
        retorno.getSelectedItem().toString();
        validation.child(uidClient).child("validateReturn").setValue("nil");
        stateDriver.child(uidDriver).child("state").setValue("onTripReturn");
        //drivers.child(uidDriver).child("extraBalance").setValue(3000);
    }

    public void getToken() {
        customer.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                token = mapS.get("deviceToken");
                device = mapS.get("package");
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void postOnTrip(){
            String url = "https://komegaroo-server.herokuapp.com/mobile/notification";
            String message = "Tu Kanguro esta solicitando retorno.";
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

    public void getReturnValidation(){
        validation.child(uidClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    String validateR = mapS.get("validateReturn");
                    Log.v("Return", validateR);
                    switch (validateR) {
                        case "no":
                            if (returnDriver) {
                                returnDriver = false;
                                timer.cancel();
                                validation.removeEventListener(this);
                                removeFragment();
                            }
                            break;
                        case "yes":
                            if (returnDriver) {
                                returnDriver = false;
                                timer.cancel();
                                validation.removeEventListener(this);
                                removeFragment();
                            }
                            break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static ReturnFragment newInstance(String text) {
        ReturnFragment f = new ReturnFragment();
        Bundle b = new Bundle();
        b.putString("ReturnFragment", text);
        f.setArguments(b);
        return f;
    }

    public void removeFragment(){
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().remove(ReturnFragment.this).commit();
    }
}