package com.driver.hp.komegaroodriver;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.driver.hp.komegaroodriver.Fragment.MapsFragment;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.AyudaActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.HistorialActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.NosotrosActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.PagoActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.PerfilActivity;
import com.driver.hp.komegaroodriver.Fragment.MenuLaterales.PromoActivity;
import com.driver.hp.komegaroodriver.Notification.GCMRegistrationIntentService;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView photoUrl;
    private Firebase mRef;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private HttpURLConnection connection;
    private URL url;
    private BufferedReader reader;
    private String uidDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/drivers");
        uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        /*drawer.openDrawer(GravityCompat.START);*/

        NavigationView leftNavigationView = (NavigationView) findViewById(R.id.nav_view);


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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Registration success
                    String token = intent.getStringExtra("token");
                    //Toast.makeText(getApplicationContext(), "GCM token:" + token, Toast.LENGTH_LONG).show();
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
                } else {
                    //Tobe define
                }
            }
        };

        //Check status of Google play service in device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode) {
            //Check type of error
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Start service
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
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

    /*public void conectionHttp(){
        try {
            url = new URL("");
            connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while((line =reader.readLine())!=null){
                buffer.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(connection!=null) {
                connection.disconnect();
            }
            try {
                if(reader!=null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    public void loadData(){
        mRef.child(uidDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild("name")&&dataSnapshot.exists()){
                    final AlertDialog alertDialog3 = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog3.setTitle("Ingrese nombre completo");
                    alertDialog3.setCancelable(false);
                    final EditText input2 = new EditText(MainActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input2.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);//se puede agregar otro con el signo |
                    input2.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_user, 0, 0, 0);
                    alertDialog3.setView(input2);
                    alertDialog3.setButton(AlertDialog.BUTTON_NEUTRAL, "Ingresar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mRef.child(uidDriver).child("name").setValue(input2.getText().toString());
                                }
                            });
                    alertDialog3.show();

                }
                if (!dataSnapshot.hasChild("phoneNumber")&&dataSnapshot.exists()) {
                    final AlertDialog alertDialog2 = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog2.setTitle("Ingrese número de celular o contacto");
                    alertDialog2.setCancelable(false);
                    final EditText input = new EditText(MainActivity.this);
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(8);
                    input.setFilters(FilterArray);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);//se puede agregar otro con el signo |
                    input.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.num, 0, 0, 0);
                    alertDialog2.setView(input);
                    alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "Ingresar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mRef.child(uidDriver).child("phoneNumber").setValue("+569" + input.getText().toString());
                                }
                            });
                    alertDialog2.show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
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