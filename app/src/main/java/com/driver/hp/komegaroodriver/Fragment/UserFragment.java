package com.driver.hp.komegaroodriver.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

/**28/04/2017
 */
public class UserFragment extends Fragment {
    private View userView;
    private DatabaseReference tRef, customer, stateDriver, validation, rDriverStatus, tripState;
    private TextView name, direccion, num, comentarios, telefono, phoneE, phoneR, nomE, recep;
    private Button cancelar, contactar, exit, emer;
    private String uidDriver, uidClient, nom, phone, contac, nomR, dTo, dFr;
    private AlertDialog alertDialog, alertReceptor, cancelDriver;
    private View one, two, btns, emisor, receptor;
    private String estado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        stateDriver = FirebaseDatabase.getInstance().getReference().child("driverState");
        tRef = FirebaseDatabase.getInstance().getReference().child("requestedTravels").child("Santiago");
        rDriverStatus = FirebaseDatabase.getInstance().getReference().child("driverStatus").child("driverCoordenates").child("Santiago");
        customer = FirebaseDatabase.getInstance().getReference().child("customers");
        tripState = FirebaseDatabase.getInstance().getReference().child("tripState");
        validation = FirebaseDatabase.getInstance().getReference().child("customerValidation");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userView = v.findViewById(R.id.userData);
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
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(UserFragment.this).commit();
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
        cancelDriver = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        cancelDriver.setTitle("Cancelar viaje");
        cancelDriver.setMessage("¿Quieres cancelar el viaje?");
        cancelDriver.setCancelable(false);
        cancelDriver.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        rDriverStatus.child(uidDriver).removeValue();
                        tripState.child(uidClient).child("state").setValue("canceledByDriver");
                        cancelDriver.dismiss();
                        FragmentManager fm = getFragmentManager();
                        fm.beginTransaction().remove(UserFragment.this).commit();
                    }
                });
        cancelDriver.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelDriver.dismiss();
                    }
                });
        canceledDriver();
        getUidClient();
        return v;
    }

    public void getUidClient(){
        rDriverStatus.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    uidClient = mapS.get("customerUid");
                    statusDriver();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void statusDriver(){
        stateDriver.child(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        estado = mapS.get("state");
                        switch (estado) {
                            case "onWay":
                                cancelar.setVisibility(View.VISIBLE);
                                showDataonWay();
                                showDataCustomer();
                                break;
                            case "onTrip":
                                cancelar.setVisibility(View.GONE);
                                showDataCustomer();
                                showDataonTrip();
                                break;
                            case "onReturn":
                                cancelar.setVisibility(View.GONE);
                                showDataCustomer();
                                showDataonTrip();
                                break;
                            case "onTripReturn":
                                cancelar.setVisibility(View.GONE);
                                showDataCustomer();
                                showDataonTrip();
                                changeData();
                                break;
                        }
                    }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
    }

    public void showDataCustomer(){
        if(uidClient!=null) {
            customer.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        nom = mapS.get("name");
                        phone = mapS.get("phoneNumber");
                        name.setText(nom);
                        telefono.setText(phone);
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }
    }

    public void showDataonWay(){
        if(uidClient!=null) {
            tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        String nume = mapS.get("blockInfo");
                        dFr = mapS.get("from");
                        String com = mapS.get("comments");
                        one.setVisibility(View.VISIBLE);
                        btns.setVisibility(View.VISIBLE);
                        two.setVisibility(View.GONE);
                        emer.setVisibility(View.GONE);
                        if (!nume.isEmpty()) {
                            num.setText(nume);
                        }
                        if (!com.isEmpty()) {
                            comentarios.setText(com);
                        }
                        direccion.setText(dFr);
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }
    }

    public void showDataonTrip(){
        if(uidClient!=null) {
            tRef.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        String nume = mapS.get("blockInfo");
                        dFr = mapS.get("from");
                        dTo = mapS.get("to");
                        String com = mapS.get("comments");
                        contac = mapS.get("contactNumber");
                        nomR = mapS.get("receptorName");
                        one.setVisibility(View.GONE);
                        btns.setVisibility(View.GONE);
                        two.setVisibility(View.VISIBLE);
                        emer.setVisibility(View.VISIBLE);
                        if (!contac.isEmpty()) {
                            phoneR.setText(contac);
                        }
                        if (!nume.isEmpty()) {
                            num.setText(nume);
                        }
                        if (!com.isEmpty()) {
                            comentarios.setText(com);
                        }
                        if (!nomR.isEmpty()) {
                            recep.setText(nomR);
                        }
                        nomE.setText(nom);
                        phoneE.setText(phone);
                        if (estado.equals("onTrip")) {
                            direccion.setText(dTo);
                        } else {
                            direccion.setText(dFr);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                }
            });
        }
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
                cancelDriver.show();
            }
        });
    }

    public void changeData() {
        if (uidClient != null) {
            validation.child(uidClient).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String direc = "Sta Mónica 2121, Santiago, Región Metropolitana, Chile";
                        Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                        String validateR = mapS.get("validateReturn");
                        switch (validateR) {
                            case "no":
                                direccion.setText(direc);
                                break;
                            case "yes":
                                direccion.setText(dFr);
                                break;
                            default:
                                direccion.setText(dTo);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static UserFragment newInstance(String text) {
        UserFragment f = new UserFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }
}
