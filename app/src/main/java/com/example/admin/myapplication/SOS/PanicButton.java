package com.example.admin.myapplication.SOS;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.admin.myapplication.R;

public class PanicButton extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_panic_button );
        Button button;
        button = (Button) findViewById( R.id.buttonClick2 );
        button.setBackgroundResource( R.drawable.pp2 );
        //get the Button reference
        //Button is a subclass of View
        //buttonClick if from main.xml "@+id/buttonClick"
        View btnClick = findViewById( R.id.buttonClick );
        //set event listener
        btnClick.setOnClickListener( this );
        View btnClick2 = findViewById( R.id.buttonClick2 );
        //set event listener
        btnClick2.setOnClickListener( this );
    }

    //override the OnClickListener interface method
    @Override
    public void onClick (View arg0) {
        if ( arg0.getId() == R.id.buttonClick )
            {
                //define a new Intent for the second Activity
                Intent intent = new Intent( this, SecondActivity.class );
                //start the second Activity
                this.startActivity( intent );
            } else if ( arg0.getId() == R.id.buttonClick2 )
            {
                Intent intent = new Intent( this, ThirdActivity.class );
                //start the second Activity
                this.startActivity( intent );
            }
    }

}
