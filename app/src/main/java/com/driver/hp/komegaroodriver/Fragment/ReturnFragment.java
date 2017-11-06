package com.driver.hp.komegaroodriver.Fragment;


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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    protected View vista, layout;
    protected Button solicita, cancelar;
    protected TextView chrono;
    protected DatabaseReference validation, drivers;
    protected String uidClient, uidDriver;
    protected String token, device;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_return, container, false);
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        validation = FirebaseDatabase.getInstance().getReference().child("customerValidation");
        drivers = FirebaseDatabase.getInstance().getReference().child("drivers");
        solicita = (Button)v.findViewById(R.id.btnSolicitaRetorno);
        cancelar = (Button)v.findViewById(R.id.btnCancelRetorno);
        vista = v.findViewById(R.id.fragmentRetorno);
        vista.setVisibility(View.GONE);
        layout = v.findViewById(R.id.layoutCausa);
        chrono = (TextView)v.findViewById(R.id.txtChrono);
        retorno = (Spinner)v.findViewById(R.id.spinnerRetorno);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_selectable_list_item,datosRetornos);
        retorno.setAdapter(adapter);
        solicita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                vista.setVisibility(View.GONE);
            }
        });
        return v;
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
            chrono.setVisibility(View.GONE);
            vista.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        }
    };

    public void sendData(){
        uidClient = ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).uidClient;
        retorno.getSelectedItem().toString();
        validation.child(uidClient).child("validateReturn").setValue("nil");
        //drivers.child(uidDriver).child("extraBalance").setValue(3000);
    }

    public void postOnTrip(){
        token = ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).token;
        device = ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).device;
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
                    switch (validateR) {
                        case "no":
                            timer.onFinish();
                            timer.cancel();
                            ((MapsFragment) getActivity().getFragmentManager().findFragmentById(R.id.content_main)).validationReturn();
                            break;
                        case "yes":
                            timer.cancel();
                            chrono.setVisibility(View.GONE);
                            vista.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            ((MapsFragment) getActivity().getFragmentManager().findFragmentById(R.id.content_main)).validationReturn();
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}