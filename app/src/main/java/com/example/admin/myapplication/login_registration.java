package com.example.admin.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login_registration extends AppCompatActivity {

    Button register,login;
    TextInputEditText user_email,user_password;
    String useremail,userpassword;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(login_registration.this, MainActivity.class));
            finish();
        }

        //find by Id all
        register=findViewById(R.id.regsiter);
        login=findViewById(R.id.login);
        user_email=findViewById(R.id.user_email);
        user_password=findViewById(R.id.user_password);

        //Registration Activity Open
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_registration.this,Register.class));
            }
        });

        //login with Auth
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                useremail=user_email.getText().toString();
                userpassword=user_password.getText().toString();

                auth.signInWithEmailAndPassword(useremail,userpassword).addOnCompleteListener(login_registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                   if(!task.isSuccessful()){
                       if(userpassword.length()<6){
                           Toast.makeText(getApplicationContext(),"enter Correct Password",Toast.LENGTH_SHORT).show();
                       }

                   }
                   else {
                       Intent i=new Intent(login_registration.this,MainActivity.class);
                       i.putExtra(useremail,"email_id");
                       startActivity(i);
                       finish();
                   }
                    }
                });

            }
        });

    }
}
