package com.driver.hp.komegaroodriver.Fragment.MenuLaterales;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.driver.hp.komegaroodriver.CircleTransform;
import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.R;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends AppCompatActivity {

    private Button close;
    private Firebase mRef;
    private TextView ema, nom, ape;
    private ImageView pho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        close = (Button) findViewById(R.id.btnPerfil);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo();
            }
        });
        Firebase.setAndroidContext(this);
        ema = (TextView)findViewById(R.id.txtCorreo);
        nom = (TextView)findViewById(R.id.txtNombre);
        ape = (TextView)findViewById(R.id.txtApellido);
        pho = (ImageView)findViewById(R.id.imgPhoto);
        Uri photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        nom.setText(name);
        ema.setText(email);
        Picasso.with(this).load(photo).transform(new CircleTransform()).into(pho);

    }

    public void metodo() {
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}