package com.example.admin.myapplication;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SpeedDetection extends AppCompatActivity {

    String channel_id;
    int notificationchannel = 1;
    TextView tv;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_speed_detection );
        tv = findViewById( R.id.speed )
        ;        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this
                .getSystemService( Context.LOCATION_SERVICE );

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged (Location location) {
                location.getLatitude();
                //Toast.makeText( getApplicationContext(), "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT ).show();
                tv.setText( "Current Speed of Vehicle is " + location.getSpeed() );
                if ( location.getSpeed() > 60 )
                    {
                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from( SpeedDetection.this );
                        NotificationCompat.Builder builder = new NotificationCompat.Builder( SpeedDetection.this, channel_id )
                                .setSmallIcon( R.mipmap.ic_launcher )
                                .setContentTitle( "Speed Limit Alert" )
                                .setContentText( "Speed Limit Exceeds Please Check" )
                                .setStyle( new NotificationCompat.BigTextStyle()
                                        .bigText( "Speed Limit Alert" ) )
                                .setPriority( NotificationCompat.PRIORITY_DEFAULT )
                                .setAutoCancel( true );

                        notificationManagerCompat.notify( notificationchannel, builder.build() );
                    }

            }

            public void onStatusChanged (String provider, int status,
                                         Bundle extras) {
            }

            public void onProviderEnabled (String provider) {
            }

            public void onProviderDisabled (String provider) {
            }
        };
        if ( ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
            {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0,
                0, locationListener );


    }

}
