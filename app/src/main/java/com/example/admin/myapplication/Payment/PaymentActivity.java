package com.example.admin.myapplication.Payment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {


    private static final String TAG = PaymentActivity.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    String user_name, user_email, user_contact;
    String remaining_fees;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_payment );
        auth = FirebaseAuth.getInstance();
        user_name = auth.getCurrentUser().getDisplayName();
        user_email = auth.getCurrentUser().getEmail();
        remaining_fees = getIntent().getStringExtra( "remaining_fees" );

        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload( getApplicationContext() );

        // Payment button created by you in XML layout
        Button button = (Button) findViewById( R.id.btn_pay );

        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startPayment();
            }
        } );
        DocumentReference docRef = db.collection( "student_data" ).document( user_email );
        docRef.get().addOnCompleteListener( new OnCompleteListener <DocumentSnapshot>() {
            @Override
            public void onComplete (@NonNull Task <DocumentSnapshot> task) {
                if ( task.isSuccessful() )
                    {
                        DocumentSnapshot document = task.getResult();
                        if ( document.exists() )
                            {
                                user_contact = document.getData().get( "contact" ).toString();

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
        // Toast.makeText(getApplicationContext(),dm,Toast.LENGTH_SHORT).show();
    }


    public void startPayment ( ) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try
            {
                JSONObject options = new JSONObject();
                options.put( "name", user_name );
                options.put( "Email", user_email );
                options.put( "description", "Bus Pooling Charges" );
                //You can omit the image option to fetch the image from dashboard
                options.put( "image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png" );
                options.put( "currency", "INR" );
                options.put( "amount", remaining_fees + "00" );
                JSONObject preFill = new JSONObject();
                preFill.put( "email", user_email );
                preFill.put( "contact", user_contact );
                options.put( "prefill", preFill );
                co.open( activity, options );
            } catch ( Exception e )
            {
                Toast.makeText( activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT )
                        .show();
                e.printStackTrace();
            }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess (String razorpayPaymentID) {
        try
            {
                Toast.makeText( this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT ).show();
            } catch ( Exception e )
            {
                Log.e( TAG, "Exception in onPaymentSuccess", e );
            }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError (int code, String response) {
        try
            {
                Toast.makeText( this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT ).show();
            } catch ( Exception e )
            {
                Log.e( TAG, "Exception in onPaymentError", e );
            }
    }

}
