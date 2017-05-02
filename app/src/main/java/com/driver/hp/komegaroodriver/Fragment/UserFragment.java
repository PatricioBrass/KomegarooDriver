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
import android.widget.ImageView;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.RoundedTransformation;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**28/04/2017
 */
public class UserFragment extends Fragment {
    private View userView, salir;
    private Firebase tRef, customer, stateDriver, stateTrip;
    private ImageView image;
    private TextView name, lastname, direccion, num, comentarios;
    private Button cancelar, contactar;
    private String uidDriver, uidClient, nom, phone;
    private AlertDialog alertDialog;

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
        stateTrip = new Firebase("https://decoded-pilot-144921.firebaseio.com/tripState");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userView = v.findViewById(R.id.userData);
        salir = v.findViewById(R.id.exit);
        userView.setVisibility(View.GONE);
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userView.setVisibility(View.GONE);
            }
        });
        image = (ImageView)v.findViewById(R.id.imgPhotoData);
        name = (TextView)v.findViewById(R.id.txtNombreData);
        lastname = (TextView)v.findViewById(R.id.txtApellidoData);
        direccion = (TextView)v.findViewById(R.id.txtDirecData);
        num = (TextView)v.findViewById(R.id.txtNumberData);
        comentarios = (TextView)v.findViewById(R.id.txtComentData);
        cancelar = (Button)v.findViewById(R.id.btnCancelData);
        alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        contactar = (Button)v.findViewById(R.id.btnContactarData);
        callCustomer();
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
                    String photo = mapS.get("photoUrl");
                    phone = mapS.get("phoneNumber");
                    String nombr = nom.substring(0,nom.indexOf(" "));
                    String apellid = nom.replace(nombr+" " ,"");
                    name.setText(nombr);
                    lastname.setText(apellid);
                    Picasso.with(getActivity()).load(photo).transform(new RoundedTransformation(9,1)).into(image);
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
                    num.setText(nume);
                    direccion.setText(fro);
                    comentarios.setText(com);
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
                    num.setText(nume);
                    direccion.setText(dTo);
                    comentarios.setText(com);
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
        contactar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
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
