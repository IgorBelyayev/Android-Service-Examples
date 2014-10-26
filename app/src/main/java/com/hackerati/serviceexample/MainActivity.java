package com.hackerati.serviceexample;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.hackerati.serviceexample.BoundService.LocalBinder;


public class MainActivity extends Activity {

    private BoundService boundService;

    private Button startBoundServiceButton;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        //Only gets called when connection is broken from outside this Activity, and NOT when we call unbindService(...)
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            startBoundServiceButton.setText("Bind to BoundService");
            boundService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "BoundService is connected",
                           Toast.LENGTH_SHORT).show();
            startBoundServiceButton.setText("Disconnect from BoundService");

            LocalBinder localBinder = (LocalBinder) service;
            boundService = localBinder.getBoundServerInstance();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startOverlayButton = (Button) findViewById(R.id.start_overlay_button);
        startOverlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                startService(new Intent(MainActivity.this, OverlayService.class));
            }
        });

        Button startIntentServiceButton = (Button) findViewById(R.id.start_intent_service_button);
        startIntentServiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                startService(new Intent(MainActivity.this, MyIPIntentService.class));
            }
        });

        startBoundServiceButton = (Button) findViewById(R.id.start_bound_service_button);
        startBoundServiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(MainActivity.this, BoundService.class);
                if (boundService == null) {
                    bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                } else {
                    unbindService(serviceConnection);
                    startBoundServiceButton.setText("Bind to BoundService");
                    boundService = null;
                }
            }
        });

        Button finishActivityButton = (Button) findViewById(R.id.finish_activity_button);
        finishActivityButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (boundService != null) {
            unbindService(serviceConnection);
            boundService = null;
        }
    }
}