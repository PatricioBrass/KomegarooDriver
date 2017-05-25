package com.driver.hp.komegaroodriver.MenuLaterales.PreguntasAyuda;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.driver.hp.komegaroodriver.R;

public class Pregunta4Activity extends AppCompatActivity {
    private Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta4);
        close = (Button) findViewById(R.id.btnP4);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metodo();
            }
        });
    }
    public void metodo() {
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
