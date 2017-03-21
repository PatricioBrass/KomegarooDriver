package com.driver.hp.komegaroodriver;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.driver.hp.komegaroodriver.Fragment.MapsFragment;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.AyudaActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.HistorialActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.NosotrosActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.PagoActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.PerfilActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.PromoActivity;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView photoUrl;
    private Firebase mRef;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Customers");
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);*/
                    startActivity(intent);
                }
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metodo();
                finish();
            }
        });*/

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*drawer.openDrawer(GravityCompat.START);*/

        NavigationView leftNavigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = leftNavigationView.getHeaderView(0);


        /*nameTextView = (TextView) header.findViewById(R.id.nameTextView);
        emailTextView = (TextView) header.findViewById(R.id.emailTextView);
        photoUrl = (ImageView) header.findViewById(R.id.myProfilePic);

        Uri photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        nameTextView.setText(name);
        emailTextView.setText(email);
        Picasso.with(this).load(photo).transform(new CircleTransform()).into(photoUrl);*/

        leftNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_historial) {
                    Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else if (id == R.id.nav_ayuda) {
                    Intent intent = new Intent(MainActivity.this, AyudaActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if (id == R.id.nav_pago) {
                    Intent intent = new Intent(MainActivity.this, PagoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                } else if (id == R.id.nav_notificaciones) {


                } else if (id == R.id.nav_promociones) {
                    Intent intent = new Intent(MainActivity.this, PromoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                } else if (id == R.id.nav_perfil) {
                    Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                } else if (id == R.id.nav_nosotros) {
                    Intent intent = new Intent(MainActivity.this, NosotrosActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        /*NavigationView rightNavigationView = (NavigationView) findViewById(R.id.nav_view2);
        rightNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();



                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.END);
                return true;
            }
        });*/

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_main, new MapsFragment()).commit();
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private void metodo(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Handle countdown stop here
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Handle countdown start here
    }

    public void lockedDrawer(){
        toggle.setDrawerIndicatorEnabled(false);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unlockedDrawer(){
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.d("VIVZq",""+newConfig.orientation);
    }
    /*
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_historial) {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else if (id == R.id.nav_ayuda) {
            Intent intent = new Intent(MainActivity.this, AyudaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_pago) {
            Intent intent = new Intent(MainActivity.this, PagoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_notificaciones) {

        } else if (id == R.id.nav_promociones) {
            Intent intent = new Intent(MainActivity.this, PromoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_perfil) {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        } else if (id == R.id.nav_nosotros) {
            Intent intent = new Intent(MainActivity.this, NosotrosActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);

    }

}