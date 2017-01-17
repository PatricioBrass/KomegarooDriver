package com.driver.hp.komegaroodriver;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText mPasswordView, mEmailView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private SignInButton googlebtn;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "LoginActivity";
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*getHashKey();*/
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Customers");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() != null){
                    /*String error = "No disponible";
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Uri photo = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    Firebase mRefChild2 = mRef.child(uid.toString());
                    Firebase mRefChild = mRefChild2.child("Email");
                    Firebase mRefChild3 = mRefChild2.child("Name");
                    Firebase mRefChild1 = mRefChild2.child("Photo Url");
                        mRefChild.setValue(email.toString());
                        mRefChild1.setValue(error.toString());*/

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);*/
                    startActivity(intent);

                }
            }
        };
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkFormFields())
                    return;
                showProgress(true);
                startSignIn();

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

   /* private void getHashKey(){

        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        }catch (PackageManager.NameNotFoundException e){
            Log.e("name not found", e.toString());
        }catch (NoSuchAlgorithmException e){
            Log.e("no such algoritmo", e.toString());
        }
    }*/
    private void startSignIn(){
        String email = mEmailView.getText().toString();
        String pass = mPasswordView.getText().toString();

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Email o Password inválidos", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                }
            }
        });
    }


    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean checkFormFields() {
        final AlertDialog alertDialog2 = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog2.setTitle("Correo inválido");
        alertDialog2.setMessage("Correo electrónico no corresponde a Komegaroo Driver.");
        alertDialog2.setCancelable(false);
        alertDialog2.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog2.closeOptionsMenu();
                    }
                });
        String email, password;

        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();

        if (email.isEmpty()) {
            mEmailView.setError("ingrese Email");
            return false;
        }
        if (password.isEmpty()){
            mPasswordView.setError("ingrese Password");
            return false;
        }if(!email.contains("@")){
            mEmailView.setError("ingrese Email válido");
            return false;
        }if (password.length() < 5){
            mPasswordView.setError("Password debe ser mayor o igual a 6 carácteres");
            return false;
        }if(!email.contains("@komegaroo.com")){
            alertDialog2.show();
            return false;
        }

        return true;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptLogin() {
        if (!checkFormFields())
            return;
        showProgress(true);
        startSignIn();

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);

    }


}

