package com.example.admin.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    private static final String TAG ="Bus Tracking" ;
    TextView user_name,stop_name;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        String dm=auth.getCurrentUser().getEmail();

        //find by id
        user_name=findViewById(R.id.username);
        stop_name=findViewById(R.id.stop_name);
        DocumentReference docRef = db.collection("student_data").document(dm);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user_name.setText(document.get("name").toString());
                        stop_name.setText(document.getData().get("stop_name").toString());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        Toast.makeText(getApplicationContext(),dm,Toast.LENGTH_SHORT).show();
    }
}
