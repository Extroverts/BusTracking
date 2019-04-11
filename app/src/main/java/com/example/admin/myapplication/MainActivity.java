package com.example.admin.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.Payment.PaymentActivity;
import com.example.admin.myapplication.SOS.PanicButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Modules.DirectionFinder;
import Modules.DirectionFinderListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {



    private static final String TAG ="" ;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth;
    String dm;
    LatLng location, current;
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    TextView username,user_email;
    Double lat,lng;

    private GoogleMap mMap;
    CircleOptions circleOptions = new CircleOptions();
    FirebaseAuth.AuthStateListener authListener;
    DocumentReference docRef;
    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick ( ) {
                    mMap.setMinZoomPreference( 200 );
                    return false;
                }
            };


    @Override
    public void onBackPressed ( ) {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if ( drawer.isDrawerOpen( GravityCompat.START ) )
            {
                drawer.closeDrawer( GravityCompat.START );
            } else
            {
                super.onBackPressed();
            }
    }

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick (@NonNull Location location) {

                    mMap.setMinZoomPreference( 5 );


                    circleOptions.center( new LatLng( location.getLatitude(), location.getLongitude() ) );

                    current = new LatLng( location.getLatitude(), location.getLongitude() );

                    circleOptions.radius( 200 );
                    circleOptions.fillColor( Color.RED );
                    circleOptions.strokeWidth( 6 );

                    mMap.addCircle( circleOptions );
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        auth = FirebaseAuth.getInstance();
        dm = auth.getCurrentUser().getEmail();
        authListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged (@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if ( user == null )
                    {
                        startActivity( new Intent( MainActivity.this, login_registration.class ) );
                        finish();
                    }
            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        user_email=header.findViewById(R.id.user_email);
        username=header.findViewById(R.id.username);
        navigationView.setNavigationItemSelectedListener(this);

        docRef = db.collection( "student_data" ).document( dm );
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        username.setText(document.getData().get("name").toString());
                        user_email.setText(document.getData().get("email_id").toString());

                    } else {
                        Toast.makeText( getApplicationContext(), "No Such Document", Toast.LENGTH_SHORT ).show();
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        // Toast.makeText(getApplicationContext(),dm,Toast.LENGTH_SHORT).show();
    }

    private void subscribeToUpdates ( ) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "location" );
        ref.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded (DataSnapshot dataSnapshot, String previousChildName) {
                setMarker( dataSnapshot );
            }

            @Override
            public void onChildChanged (DataSnapshot dataSnapshot, String previousChildName) {
                setMarker( dataSnapshot );
                Log.d( TAG, dataSnapshot.getValue().toString() );
            }

            @Override
            public void onChildMoved (DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError) {

            }

            @Override
            public void onChildRemoved (DataSnapshot dataSnapshot) {
            }

        } );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if ( id == R.id.directions )
            {
                //Direction APi
                startActivity( new Intent( MainActivity.this, MapsActivity.class ) );

            } else if ( id == R.id.panic )
            {
                //Panic Button Code
                startActivity( new Intent( MainActivity.this, PanicButton.class ) );

            } else if ( id == R.id.payment )
            {
                //Payment Options

                startActivity( new Intent( MainActivity.this, PaymentActivity.class ) );

            }
        if ( id == R.id.profile )
            {
                startActivity( new Intent( MainActivity.this, Profile.class ) );

            } else if ( id == R.id.fee_Status )
            {
                startActivity( new Intent( MainActivity.this, Fees.class ) );

            } else if ( id == R.id.speed )
            {

                startActivity( new Intent( MainActivity.this, SpeedDetection.class ) );
            } else if ( id == R.id.nav_share) {
            startActivity( new Intent( MainActivity.this, LiveFeedback.class ) );

        } else if ( id == R.id.logout )
            {
                signOut();
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        mMap.setMaxZoomPreference(18);
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocationIfPermitted();
        subscribeToUpdates();
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        lat = Double.parseDouble(value.get("latitude").toString());
        lng = Double.parseDouble(value.get("longitude").toString());
        location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }

    // logout click button
    private void signOut() {
        auth.signOut();
        startActivity( new Intent( MainActivity.this, login_registration.class ) );
    }

    private void enableMyLocationIfPermitted() {
        if ( ContextCompat.checkSelfPermission( this,
                Manifest.permission.ACCESS_FINE_LOCATION)
             != PackageManager.PERMISSION_GRANTED )
            {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                             "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED )
                    {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.attendance )
            {
                //Toast.makeText( getApplicationContext(),"My Locat"+location+"   another"+current,Toast.LENGTH_SHORT ).show();
                final LatLng[] latLngs = new LatLng[2];
                latLngs[0] = location;
                latLngs[1] = current;
                if ( latLngs[0].equals( latLngs[1] ) )
                    {
                        Toast.makeText( getApplicationContext(), "Not Marked, Yor are not in Bus", Toast.LENGTH_SHORT ).show();
                    } else
                    {

                        Map <String, Object> data = new HashMap <>();
                        data.put( "email", dm );
                        data.put( "date", new Timestamp( new Date() ) );
                        data.put( "stautus", "present" );
                        db.collection( "Attendance" ).add( data ).addOnSuccessListener( new OnSuccessListener <DocumentReference>() {
                            @Override
                            public void onSuccess (DocumentReference documentReference) {
                                Toast.makeText( getApplicationContext(), "Attendance Marked", Toast.LENGTH_SHORT ).show();
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure (@NonNull Exception e) {
                                Toast.makeText( getApplicationContext(), "Error is " + e, Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                return true;

            }
        return super.onOptionsItemSelected( item );
    }

    }
