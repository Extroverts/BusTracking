package com.example.admin.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.UUID;

public class LiveFeedback extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private final int PICK_IMAGE_REQUEST = 71;
    TextInputEditText feedback;
    Bitmap photo;
    Button btnUpload;
    FirebaseStorage storage;
    StorageReference storageReference;
    private ImageView imageView;
    private Uri filePath;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_live_feedback );
        this.imageView = (ImageView) this.findViewById( R.id.imageView1 );
        feedback = findViewById( R.id.feedback );
        btnUpload = (Button) findViewById( R.id.btnUpload );

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Button photoButton = (Button) this.findViewById( R.id.button1 );
        photoButton.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick (View v) {
                if ( checkSelfPermission( Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED )
                    {
                        requestPermissions( new String[]{ Manifest.permission.CAMERA }, MY_CAMERA_PERMISSION_CODE );
                    } else
                    {
                        Intent cameraIntent = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
                        startActivityForResult( cameraIntent, CAMERA_REQUEST );
                    }
            }
        } );
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if ( requestCode == MY_CAMERA_PERMISSION_CODE )
            {
                if ( grantResults[0] == PackageManager.PERMISSION_GRANTED )
                    {
                        Toast.makeText( this, "camera permission granted", Toast.LENGTH_LONG ).show();
                        Intent cameraIntent = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
                        startActivityForResult( cameraIntent, CAMERA_REQUEST );
                    } else
                    {
                        Toast.makeText( this, "camera permission denied", Toast.LENGTH_LONG ).show();
                    }
            }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {

        if ( requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK )
            {
                photo = (Bitmap) data.getExtras().get( "data" );
                imageView.setImageBitmap( photo );

            }
        filePath = data.getData();
    }

    public void uploadImage ( ) {
        if ( filePath != null )
            {
                final ProgressDialog progressDialog = new ProgressDialog( this );
                progressDialog.setTitle( "Uploading..." );
                progressDialog.show();

                StorageReference ref = storageReference.child( "images/" + UUID.randomUUID().toString() );
                ref.putFile( filePath )
                        .addOnSuccessListener( new OnSuccessListener <UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess (UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText( LiveFeedback.this, "Uploaded", Toast.LENGTH_SHORT ).show();
                            }
                        } )
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure (@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText( LiveFeedback.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                            }
                        } )
                        .addOnProgressListener( new OnProgressListener <UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress (UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage( "Uploaded " + (int) progress + "%" );
                            }
                        } );
            }

    }

}

