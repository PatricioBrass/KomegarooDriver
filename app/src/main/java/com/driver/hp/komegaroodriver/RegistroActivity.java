package com.driver.hp.komegaroodriver;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etPass, etPass2, nombre;
    private EditText etEmail;
    private Button btningresar;
    private View mProgressView;
    private View mLoginFormView;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mRef = new Firebase("https://decoded-pilot-144921.firebaseio.com/Customers");
        nombre = (EditText)findViewById(R.id.nombreS);
        etEmail = (EditText)findViewById(R.id.email);
        etPass = (EditText)findViewById(R.id.password);
        etPass2 = (EditText)findViewById(R.id.password2);
        mAuth = FirebaseAuth.getInstance();
        btningresar = (Button)findViewById(R.id.ingresar);
        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkFormFields())
                    return;
                showProgress(true);
                createUserAccount();


            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void createUserAccount() {


        String email = etEmail.getText().toString();
        String password = etPass.getText().toString();

        // TODO: Create the user account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegistroActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            Firebase mRefChild2 = mRef.child(uid.toString());
                            Firebase mRefChild1 = mRefChild2.child("Email");
                            Firebase mRefChild = mRefChild2.child("Name");
                            mRefChild1.setValue(email.toString());
                            mRefChild.setValue(nombre.getText().toString());
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(RegistroActivity.this, "Usuario no creado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistroActivity.this, RegistroActivity.class);
                            startActivity(intent);
                        }
                    }
                });

    }
    private boolean checkFormFields() {
        String email, password, password2;

        email = etEmail.getText().toString();
        password = etPass.getText().toString();
        password2 = etPass2.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("ingrese Email");
            return false;
        }
        if (password.isEmpty()){
            etPass.setError("ingrese Password");
            return false;
        }if (password2.isEmpty()){
            etPass2.setError("ingrese Password");
            return false;
        }if(!email.contains("@")){
            etEmail.setError("ingrese Email válido");
            return false;
        }if (password.length() < 4){
            etPass.setError("Password debe ser mayor o igual a 6 carácteres");
            return false;
        }if (password2.length() < 4){
            etPass2.setError("Password debe ser mayor o igual a 6 carácteres");
            return false;
        }if(!password2.equals(password)){
            etPass2.setError("Las password deben ser iguales");
            return false;
        }

        return true;
    }

    public void onBackPressed() {
        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
