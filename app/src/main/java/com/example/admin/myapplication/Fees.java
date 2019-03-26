package com.example.admin.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.Payment.PaymentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Fees extends AppCompatActivity {
    private static final String TAG = "Fees";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authListener;
    TextView paid, remainng;
    Button pay;
    String paid_fees, remaining_fees;
    ListView listView;
    private List <String> namesList = new ArrayList <String>();


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_fees );

        paid = findViewById( R.id.paid );
        remainng = findViewById( R.id.remaining );
        pay = findViewById( R.id.pay );
        listView = findViewById( R.id.list_view );

        auth = FirebaseAuth.getInstance();
        String dm = auth.getCurrentUser().getEmail();
        DocumentReference docRef = db.collection( "student_data" ).document( dm );
        docRef.get().addOnCompleteListener( new OnCompleteListener <DocumentSnapshot>() {
            @Override
            public void onComplete (@NonNull Task <DocumentSnapshot> task) {
                if ( task.isSuccessful() )
                    {
                        DocumentSnapshot document = task.getResult();
                        if ( document.exists() )
                            {
                                paid_fees = document.getData().get( "fess_paid" ).toString();
                                paid.setText( paid_fees );
                                remaining_fees = document.getData().get( "remaining_fees" ).toString();
                                remainng.setText( remaining_fees );

                            } else
                            {
                                Toast.makeText( getApplicationContext(), "No Such Document", Toast.LENGTH_SHORT ).show();
                                Log.d( TAG, "No such document" );
                            }
                    } else
                    {
                        Log.d( TAG, "get failed with ", task.getException() );
                    }
            }
        } );


        //Spinner List
        db.collection( "Fees" )
                .get()
                .addOnCompleteListener( new OnCompleteListener <QuerySnapshot>() {
                    @Override
                    public void onComplete (@NonNull Task <QuerySnapshot> task) {
                        if ( task.isSuccessful() )
                            {
                                for (QueryDocumentSnapshot document : task.getResult())
                                    {
                                        Log.d( TAG, document.getId() + " => " + document.getData() );
                                        namesList.add( document.getData().toString() );
                                    }
                            } else
                            {
                                Log.d( TAG, "Error getting documents: ", task.getException() );
                            }
                        ArrayAdapter <String> arrayAdapter = new ArrayAdapter <String>( Fees.this, android.R.layout.simple_list_item_1, namesList );
                        listView.setAdapter( arrayAdapter );
                    }
                } );

        pay.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent pay_fees = new Intent( Fees.this, PaymentActivity.class );
                pay_fees.putExtra( "remaining_fees", remaining_fees );
                startActivity( pay_fees );
            }
        } );

    }


}
