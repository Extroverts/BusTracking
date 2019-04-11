package com.example.admin.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.vidyo.VidyoClient.Connector.ConnectorPkg;
import com.vidyo.VidyoClient.Connector.Connector;

public class Live_feedback extends AppCompatActivity implements Connector.IConnect {
    private Connector vc;
    private FrameLayout videoFrame;
    Button start, connect, disconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_feedback2);
        ConnectorPkg.setApplicationUIContext(Live_feedback.this);
        ConnectorPkg.initialize();
        videoFrame = (FrameLayout) findViewById(R.id.videoframe);
        start = findViewById(R.id.start);
        connect = findViewById(R.id.connect);
        disconnect = findViewById(R.id.disconnect);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
                vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "cHJvdmlzaW9uAHVzZXIyQDUzNTAxYi52aWR5by5pbwA2MzcyMjE5Mjg1NgAAOGY1Y2VmMjMwMTQ5ZGZhMTUyMGIyMTJkMGMwN2Q1MjljZjUwNGVmYWNmY2VmMWYwMGE1MmJkNGI4ZjYzZmNmZTUzYWNmMjczNjVkZThjN2U2ZjhjZDE3NWY2NjM1OWQ5";
                vc.connect("prod.vidyo.io", token, "DemoUser", "DemoRoom", Live_feedback.this);
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vc.disconnect();
            }
        });
    }


    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(Connector.ConnectorFailReason connectorFailReason) {

    }

    @Override
    public void onDisconnected(Connector.ConnectorDisconnectReason connectorDisconnectReason) {

    }
}
