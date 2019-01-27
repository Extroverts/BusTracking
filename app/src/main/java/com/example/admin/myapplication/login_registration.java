package com.example.admin.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_registration extends AppCompatActivity {

    Button register,login;
    TextView forogt;
    TextInputEditText user_email,user_password;
    String useremail,userpassword;
    private ProgressBar progressBar;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(login_registration.this, MainActivity.class));
            finish();
        }

        //find by Id all
        register=findViewById(R.id.regsiter);
        login=findViewById(R.id.login);
        user_email=findViewById(R.id.user_email);
        user_password=findViewById(R.id.user_password);
        forogt=findViewById( R.id.forgot );

        //Registration Activity Open
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        //login with Auth
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremail = user_email.getText().toString();
                userpassword = user_password.getText().toString();

                if(useremail.isEmpty()){
                    Toast.makeText( getApplicationContext(),"enter Correct Mail",Toast.LENGTH_SHORT ).show();
                }
                else if(userpassword.length()<6){
                    Toast.makeText( getApplicationContext(),"enter Correct Password",Toast.LENGTH_SHORT ).show();
                }
                else{
                    login();
                    }
            }
        });

        forogt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
               forgot();
            }
        } );
    }


    //login activity
    private void login(){
        auth.signInWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(login_registration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity( new Intent( login_registration.this,MainActivity.class ) );
                }
            }
        });
    }

    // register Activity
    private void register(){
        startActivity(new Intent(login_registration.this,Register.class));
    }


    //forgot Activity
    private void forgot() {
        final AlertDialog.Builder alert=new AlertDialog.Builder(login_registration.this);
        alert.setTitle("Forgot Password ?");
        LayoutInflater layoutInflater=login_registration.this.getLayoutInflater();
        final View dialog=layoutInflater.inflate(R.layout.forgot_password,null);
        alert.setView(dialog);
        final EditText forgot_pass=dialog.findViewById(R.id.textInputEditText);
        Button btn=dialog.findViewById(R.id.send);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!forgot_pass.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(forgot_pass.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(login_registration.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(login_registration.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    forgot_pass.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }


        });

        AlertDialog aa=alert.create();
        aa.show();

    }

        }