package com.example.admin.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Register extends AppCompatActivity {

    TextInputEditText sname,emailid,contactss,busno,stop,password;
    private static final String TAG = "Data";
    Button register; int otp;
    Spinner spinner,spinner2;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    int otp_save;

    Map<String, Object> user = new HashMap<>();
    List<String> bus_number=new ArrayList<String>();
    List<String> bus_stop=new ArrayList<String>();

    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        sname=(TextInputEditText)findViewById(R.id.sname);
        emailid=(TextInputEditText)findViewById(R.id.email);
        contactss=(TextInputEditText)findViewById(R.id.contact);
        spinner=findViewById(R.id.spinner);
        spinner2=findViewById(R.id.spinner2);
        password=(TextInputEditText)findViewById(R.id.password);
        register=findViewById(R.id.button);


        //get Buses Names from collection
       db.collection("College_Bus").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   for(DocumentSnapshot snapshot:task.getResult()){
                       String bus_data=(String) snapshot.getData().get("bus");
                       bus_number.add(bus_data);
                   }
                   ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Register.this, R.layout.spinner_list, bus_number);
                   areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                   spinner.setAdapter(areasAdapter);
               }
           }
       });

       //get Route names info according to bus Names
       spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               db.collection(parent.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (DocumentSnapshot snapshot:task.getResult()){
                            String bus_stops=(String) snapshot.getData().get("stop");
                            bus_stop.add(bus_stops);
                        }
                        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(Register.this,R.layout.spinner_list,bus_stop);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner2.setAdapter(arrayAdapter);
                    }
                    }
               });
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {
               //when nothing selected show message here......
           }
       });


       // on register Button Click
        register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                try
                    {
                        otp= generateOTP();
                    } catch ( Exception e )
                    {
                        Toast.makeText( getApplicationContext(),"Check Your Balance & Internet Connection",Toast.LENGTH_SHORT ).show();
                        e.printStackTrace();
                    }
                Log.d( TAG, String.valueOf( otp ) );
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage( contactss.getText().toString(), null, "Bus Tracking System One Time Password(OTP) is" + otp + "Please do not share this with Anyone.", null, null );
                final AlertDialog.Builder alert=new AlertDialog.Builder(Register.this);
                LayoutInflater layoutInflater=Register.this.getLayoutInflater();
                final View dialog=layoutInflater.inflate(R.layout.otp_validation,null);
                alert.setView(dialog);
                final EditText forgot_pass=dialog.findViewById(R.id.textInputEditText);
                Button btn=dialog.findViewById(R.id.send);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (forgot_pass.getText().toString().equals( String.valueOf( otp ) )) {
                           register();
                        } else {
                            forgot_pass.setError("Enter OTP");
                        }
                    }


                });
                AlertDialog aa=alert.create();
                aa.show();
            }
        });

    }

    //register Method
    private void register(){
        String cname=sname.getText().toString();
        final String email_id=emailid.getText().toString();
        String contact=contactss.getText().toString();
        String bus_no=spinner.getSelectedItem().toString();
        String stop_name=spinner2.getSelectedItem().toString();
        String password_user=password.getText().toString();

        Map<String,Object> register=new HashMap<>();
        register.put("name",cname);
        register.put("email_id",email_id);
        register.put("contact",contact);
        register.put("bus_no",bus_no);
        register.put("stop_name",stop_name);
        register.put("password",password_user);

        Log.d("data is","datra"+register);

        mAuth.createUserWithEmailAndPassword(email_id,password_user).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG,"Registration Success");
            }
        });

        db.collection("student_data").document(email_id).set(register).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent i=new Intent(Register.this,MainActivity.class);

                startActivity(i);
                finish();
            }
        });

    }

    //otp Generation code
    private void otp(){
    }
    //generate random otp
    public int generateOTP() throws Exception {
        Random generator = new Random();
        generator.setSeed(System.currentTimeMillis());

        int num = generator.nextInt(99999) + 99999;
        if (num < 100000 || num > 999999) {
            num = generator.nextInt(99999) + 99999;
            if (num < 100000 || num > 999999) {
                throw new Exception("Bus Tracking Exception");
            }
        }
        return num;
    }

}
