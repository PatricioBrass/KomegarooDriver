package com.driver.hp.komegaroodriver;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;

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
    private Button mEmailSignInButton, plomo;
    private String emails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/drivers");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(firebaseAuth.getCurrentUser() != null){
                    /*String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    Firebase mRefChild2 = mRef.child(uid.toString());
                    Firebase mRefChild = mRefChild2.child("email");
                    Firebase mRefChild3 = mRefChild2.child("name");
                    Firebase mRefChild1 = mRefChild2.child("photoUrl");
                    Firebase mRefChild4 = mRefChild2.child("calification");
                    Firebase mRefChild5 = mRefChild2.child("trips");
                    mRefChild4.setValue(3);
                    mRefChild.setValue(email);
                    mRefChild1.setValue("https://lh4.googleusercontent.com/-k0YDYiIWlAQ/AAAAAAAAAAI/AAAAAAAAAAA/AKB_U8sc4yp9o3yiG9tbs78q_6BeqM77YQ/s96-c/photo.jpg");
                    mRefChild5.setValue(0);*/
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);*/
                    startActivity(intent);

                }
            }
        };
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        Drawable drawables = getResources().getDrawable(R.mipmap.ic_user);
        drawables.setBounds(0, -8, (int) (drawables.getIntrinsicWidth() * 0.5),
                (int) (drawables.getIntrinsicHeight() * 0.4));
        ScaleDrawable sds = new ScaleDrawable(drawables, 0, scaleWidth, scaleHeight);
        Drawable drawables1 = getResources().getDrawable(R.mipmap.password);
        drawables1.setBounds(0, -8, (int) (drawables1.getIntrinsicWidth() * 0.5),
                (int) (drawables1.getIntrinsicHeight() * 0.4));
        ScaleDrawable sds1 = new ScaleDrawable(drawables1, 0, scaleWidth, scaleHeight);
        mEmailView.setCompoundDrawables(sds.getDrawable(), null, null, null);
        mPasswordView.setCompoundDrawables(sds1.getDrawable(), null, null, null);
        plomo = (Button)findViewById(R.id.buttonPlomo);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkFormFields())
                    return;
                showProgress(true);
                startSignIn();

            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
        showButton();

    }
    private void startSignIn(){
        emails = mEmailView.getText().toString()+"@komegaroo.com";
        String pass = mPasswordView.getText().toString();

        mAuth.signInWithEmailAndPassword(emails,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Email o Password inválidos", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                }
            }
        });
    }

    public void showButton(){
        TextWatcher clear = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mEmailView.getEditableText().toString().isEmpty()&&!mPasswordView.getEditableText().toString().isEmpty())
                {
                    plomo.setVisibility(View.GONE);
                    mEmailSignInButton.setVisibility(View.VISIBLE);
                }else{
                    mEmailSignInButton.setVisibility(View.GONE);
                    plomo.setVisibility(View.VISIBLE);
                }
            }
        };

        mEmailView.addTextChangedListener(clear);
        mPasswordView.addTextChangedListener(clear);
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
        }if (password.length() < 5){
            mPasswordView.setError("Password debe ser mayor o igual a 6 carácteres");
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

