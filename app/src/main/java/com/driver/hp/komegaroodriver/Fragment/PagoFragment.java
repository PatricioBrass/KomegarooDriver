package com.driver.hp.komegaroodriver.Fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.MenuLaterales.PagoActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PagoFragment extends Fragment {

    private Spinner cuentas, bancos;
    private Button guardar;
    String[] datosBanco ={"Banco Chile-Edwards-Citi","Banco Consorcio", "Banco del Desarrollo",
            "Banco del Estado", "Banco Falabella", "Banco Internacional", "Banco Itaú",
            "Banco Paris", "Banco Rabobank", "Banco Ripley", "Banco Santander", "Banco Security",
            "Banco BBVA", "Banco BCI", "Banco BICE", "Banco COOPEUCH", "Banco CorpBanca",
            "HSBC BANK", "Banco Scotiabank"};
    String[] datosCuenta = {"Cuenta de ahorro","Cuenta Corriente","Cuenta Vista"};
    private EditText nCuenta, id;
    private String uidDriver;
    private TextView nombre;
    private ImageView photo;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseD;
    private ImageButton close;
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
        close = (ImageButton) v.findViewById(R.id.btn_closePago);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(PagoFragment.this).commit();
            }
        });
        guardar = (Button) v.findViewById(R.id.buttonGuardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkEmpty())
                    return;
                sendData();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(PagoFragment.this).commit();
                if(PagoActivity.fa != null){
                    ((PagoActivity) getActivity()).showDataPago();
                }
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
        detectPagoActivity();
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
        if (!validaRut(rut)){
            alertDialog.show();
            return false;
        }
        return true;
    }
    public static Boolean validaRut ( String rut ) {
        Pattern pattern = Pattern.compile("^[0-9]+-[0-9kK]{1}$");
        Matcher matcher = pattern.matcher(rut);
        if ( !matcher.matches() )
            return false;
        String[] stringRut = rut.split("-");
        return stringRut[1].toLowerCase().equals(dv(stringRut[0]));

    }
    public static String dv ( String rut ) {
        Integer M=0,S=1,T=Integer.parseInt(rut);
        for (;T!=0;T=(int) Math.floor(T/=10))
            S=(S+T%10*(9-M++%6))%11;
        return ( S > 0 ) ? String.valueOf(S-1) : "k";
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

    public static PagoFragment newInstance(String text) {
        PagoFragment f = new PagoFragment();
        Bundle b = new Bundle();
        b.putString("pagoFragment", text);
        f.setArguments(b);
        return f;
    }

    public void detectPagoActivity(){
        if(PagoActivity.fa != null){
            close.setVisibility(View.VISIBLE);
        }
    }


}
