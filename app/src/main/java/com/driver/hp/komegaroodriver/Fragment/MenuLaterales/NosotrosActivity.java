package com.driver.hp.komegaroodriver.Fragment.MenuLaterales;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.driver.hp.komegaroodriver.MainActivity;
import com.driver.hp.komegaroodriver.R;

public class NosotrosActivity extends AppCompatActivity {

    private Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nosotros);
        close = (Button) findViewById(R.id.btnNosotros);

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
        Intent intent = new Intent(NosotrosActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NosotrosActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}