package com.driver.hp.komegaroodriver.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

/**28/04/2017
 */
public class UserFragment extends Fragment {
    private View userView;
    private Firebase tRef, customer, stateDriver;
    private TextView name, direccion, num, comentarios, telefono, phoneE, phoneR, nomE, recep;
    private Button cancelar, contactar, exit, emer;
    private String uidDriver, uidClient, nom, phone, contac, nomR;
    private AlertDialog alertDialog, alertReceptor;
    private View one, two, btns, emisor, receptor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        stateDriver = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverState");
        tRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/requestedTravels/Santiago");
        customer = new Firebase("https://decoded-pilot-144921.firebaseio.com/customers");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userView = v.findViewById(R.id.userData);
        userView.setVisibility(View.GONE);
        name = (TextView)v.findViewById(R.id.txtNombreData);
        phoneE = (TextView)v.findViewById(R.id.txtPhoneEmisor);
        phoneR = (TextView)v.findViewById(R.id.txtPhoneReceptor);
        nomE = (TextView)v.findViewById(R.id.txtEmisor);
        telefono = (TextView)v.findViewById(R.id.txtNumeroData);
        direccion = (TextView)v.findViewById(R.id.txtDirecData);
        num = (TextView)v.findViewById(R.id.txtNumberData);
        comentarios = (TextView)v.findViewById(R.id.txtComentData);
        cancelar = (Button)v.findViewById(R.id.btnCancelData);
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertReceptor = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        contactar = (Button)v.findViewById(R.id.btnContactarData);
        one = v.findViewById(R.id.onePhone);
        two = v.findViewById(R.id.twoPhone);
        recep = (TextView) v.findViewById(R.id.txtReceptor);
        emer = (Button)v.findViewById(R.id.btnEmergencia);
        btns = v.findViewById(R.id.btnTwo);
        exit = (Button)v.findViewById(R.id.buttonExit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userView.setVisibility(View.GONE);
            }
        });
        emisor = v.findViewById(R.id.emisorCall);

        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCustomer();
            }
        });
        emisor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCustomer();
            }
        });
        receptor = v.findViewById(R.id.receptorCall);
        receptor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callReceptor();
            }
        });
        statusDriver();
        canceledDriver();
        return v;
    }

    public void statusDriver(){
        stateDriver.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    String estado = mapS.get("state");
                    if(estado.equals("onWay")){
                        uidClient = ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).uidClient;
                        showDataonWay();
                        showDataCustomer();
                    }else if(estado.equals("onTrip")){
                        uidClient = ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).uidClient;
                        showDataCustomer();
                        showDataonTrip();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void showDataCustomer(){
        customer.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    nom = mapS.get("name");
                    phone = mapS.get("phoneNumber");
                    name.setText(nom);
                    telefono.setText(phone);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void showDataonWay(){
        tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    String nume = mapS.get("blockInfo");
                    String fro = mapS.get("from");
                    String com = mapS.get("comments");
                    one.setVisibility(View.VISIBLE);
                    btns.setVisibility(View.VISIBLE);
                    two.setVisibility(View.GONE);
                    emer.setVisibility(View.GONE);
                    if(!nume.isEmpty()){num.setText(nume);}
                    if(!com.isEmpty()){comentarios.setText(com);}
                    direccion.setText(fro);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void showDataonTrip(){
        tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    String nume = mapS.get("blockInfo");
                    String dTo = mapS.get("to");
                    String com = mapS.get("comments");
                    contac = mapS.get("contactNumber");
                    nomR = mapS.get("receptorName");
                    one.setVisibility(View.GONE);
                    btns.setVisibility(View.GONE);
                    two.setVisibility(View.VISIBLE);
                    emer.setVisibility(View.VISIBLE);
                    if(!contac.isEmpty()){phoneR.setText(contac);}
                    if(!nume.isEmpty()){num.setText(nume);}
                    if(!com.isEmpty()){comentarios.setText(com);}
                    if(!nomR.isEmpty()){recep.setText(nomR);}
                    direccion.setText(dTo);
                    nomE.setText(nom);
                    phoneE.setText(phone);

                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void callCustomer(){
        alertDialog.setTitle(nom);
        alertDialog.setMessage(phone);
        alertDialog.setCancelable(false);
        alertDialog.setButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton2("Llamar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uri = "tel:" + phone.trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
        alertDialog.show();
    }

    public void callReceptor(){
        alertReceptor.setTitle(nomR);
        alertReceptor.setMessage(contac);
        alertReceptor.setCancelable(false);
        alertReceptor.setButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertReceptor.dismiss();
            }
        });
        alertReceptor.setButton2("Llamar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String uri = "tel:" + contac.trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
        alertReceptor.show();
    }

    public void canceledDriver(){
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userView.setVisibility(View.GONE);
                ((MapsFragment)getActivity().getFragmentManager().findFragmentById(R.id.content_main)).cancelDriver.show();
            }
        });
    }
}
