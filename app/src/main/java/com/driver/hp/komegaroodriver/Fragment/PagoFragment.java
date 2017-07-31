package com.driver.hp.komegaroodriver.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.RoundedTransformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class PagoFragment extends Fragment {

    private Spinner cuentas, bancos;
    private Button guardar;
    String[] datosBanco ={"Banco Chile-Edwards-Citi","Banco Consorcio", "Banco del Desarrollo",
            "Banco del Estado", "Banco Falabella", "Banco Internacional", "Banco Itaú",
            "Banco Paris", "Banco Rabobank", "Banco Ripley", "Banco Santander", "Banco Security",
            "Banco BBVA", "Banco BCI", "Banco BICE", "Banco COOPEUCH", "Banco CorpBanca",
            "HSBC BANK", "Banco Scotiabank"};
    String[] datosCuenta = {"Cuenta de ahorro","Cuenta Corriente","Cuenta Vista"};
    private View close;
    private EditText nCuenta, id;
    private String uidDriver;
    private TextView nombre;
    private ImageView photo;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseD;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pago, container, false);
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("driverPayments");
        mDatabaseD = FirebaseDatabase.getInstance().getReference().child("drivers");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.v("Uid", uidDriver);
        Log.v("data", database.toString());
        showData();
        close = v.findViewById(R.id.pagoFragment);
        close.setVisibility(View.GONE);
        guardar = (Button) v.findViewById(R.id.buttonGuardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkEmpty())
                    return;
                sendData();
                close.setVisibility(View.GONE);
            }
        });
        cuentas = (Spinner)v.findViewById(R.id.spinnerCuentasFr);
        bancos = (Spinner)v.findViewById(R.id.spinnerBancoFr);
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,datosBanco);
        ArrayAdapter adapter1 = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,datosCuenta);
        bancos.setAdapter(adapter);
        cuentas.setAdapter(adapter1);
        nCuenta = (EditText)v.findViewById(R.id.nCuentaFr);
        id = (EditText)v.findViewById(R.id.rutFr);
        nombre = (TextView) v.findViewById(R.id.nameDriverPago);
        photo = (ImageView)v.findViewById(R.id.imageDriverPago);
        return v;
    }

    public void sendData(){
        mDatabase.child(uidDriver).child("account").setValue(cuentas.getSelectedItem().toString());
        mDatabase.child(uidDriver).child("accountNumber").setValue(nCuenta.getText().toString());
        mDatabase.child(uidDriver).child("bank").setValue(bancos.getSelectedItem().toString());
        mDatabase.child(uidDriver).child("dni").setValue(id.getText().toString());
    }

    private boolean checkEmpty(){
        final AlertDialog alertDialog2 = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog2.setTitle("Campo vacío");
        alertDialog2.setMessage("Debe rellenar el campo vacío.");
        alertDialog2.setCancelable(false);
        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog2.dismiss();
                    }
                });
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle).create();
        alertDialog.setTitle("Rut inválido!");
        alertDialog.setMessage("Ingrese rut válido.");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        String num = nCuenta.getText().toString();
        String rut = id.getText().toString();
        if(num.isEmpty()){
            alertDialog2.show();
            return false;
        }
        if(rut.isEmpty()){
            alertDialog2.show();
            return false;
        }
        String digito = rut.substring(rut.length()-1);
        char dverif = digito.charAt(0);
        String rutDV = rut.substring(0,rut.length()-1);
        Integer dni = Integer.parseInt(rutDV);
        if (!ValidarRut(dni,dverif)){
            alertDialog.show();
            return false;
        }
        return true;
    }

    public static boolean ValidarRut(int rut, char dv)
    {
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10)
        {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }

    public void showData(){
        mDatabaseD.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = (Map<String, String>) dataSnapshot.getValue();
                    String name = mapS.get("name");
                    String photos = mapS.get("photoUrl");
                    nombre.setText(name);
                    Picasso.with(getActivity()).load(photos).transform(new RoundedTransformation(9,1)).into(photo);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
