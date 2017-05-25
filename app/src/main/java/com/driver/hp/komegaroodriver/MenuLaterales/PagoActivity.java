package com.driver.hp.komegaroodriver.MenuLaterales;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

public class PagoActivity extends AppCompatActivity {

    private TextView cuentas, bancos, rut, nCuentas, nombre, editar;
    private ImageView photo;
    private Button close;
    View frPago;
    private String uidDriver;
    private Firebase driver, payment;
    private View mProgressView;
    private View mPerfilFormView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pago);
        driver = new Firebase("https://decoded-pilot-144921.firebaseio.com/drivers");
        payment = new Firebase("https://decoded-pilot-144921.firebaseio.com/driverPayments");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        close = (Button) findViewById(R.id.btnPago);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        frPago = findViewById(R.id.pagoFrag);
        photo = (ImageView)findViewById(R.id.imageRider);
        cuentas = (TextView)findViewById(R.id.txtCuenta);
        bancos = (TextView)findViewById(R.id.txtBanco);
        rut = (TextView)findViewById(R.id.txtRut);
        nCuentas = (TextView)findViewById(R.id.txtNCuenta);
        nombre = (TextView)findViewById(R.id.textViewNameRider);
        mProgressView = findViewById(R.id.progressBarPago);
        mPerfilFormView = findViewById(R.id.formPago);
        editar = (TextView)findViewById(R.id.txtEditar);
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frPago.setVisibility(View.VISIBLE);
            }
        });
        showProgress(true);
        showData();
        showDataPago();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void showData(){
        driver.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS =dataSnapshot.getValue(Map.class);
                    String name = mapS.get("name");
                    String photos = mapS.get("photoUrl");
                    nombre.setText(name);
                    Picasso.with(PagoActivity.this).load(photos).transform(new RoundedTransformation(9,1)).into(photo);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void showDataPago(){
        payment.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, String> mapS = dataSnapshot.getValue(Map.class);
                    String cuent = mapS.get("account");
                    String nCuent = mapS.get("accountNumber");
                    String bank = mapS.get("bank");
                    String id = mapS.get("dni");
                    cuentas.setText(cuent);
                    nCuentas.setText(nCuent);
                    bancos.setText(bank);
                    rut.setText(id);
                }
                showProgress(false);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPerfilFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPerfilFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
