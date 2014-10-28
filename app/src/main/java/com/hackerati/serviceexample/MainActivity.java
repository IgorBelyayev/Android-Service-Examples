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

/**
 * This Activity contains buttons for starting MyIPIntentService and OverlayService. This Activity
 * contains a button for binding to BoundService.
 */
public class MainActivity extends Activity {

    private BoundService boundService;

    private Button startBoundServiceButton;

    /**
     * Callback for when this Activity binds to boundService
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        //This only gets called when connection is broken from outside this Activity, and NOT when we call unbindService(...)
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
            startBoundServiceButton.setText("Bind to BoundService");
            boundService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "BoundService is connected",
                           Toast.LENGTH_SHORT).show();
            startBoundServiceButton.setText("Disconnect from BoundService");

            //IBinder is the interface used to communicate with a bound Service
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
                //if we are not bound to boundService, then start it (UI modification done in
                //serviceConnection.onServiceConnected(...)
                if (boundService == null) {
                    bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                    //if we are bound to boundService, then unbind from it and modify button UI
                } else {
                    unbindService(serviceConnection);
                    startBoundServiceButton.setText("Bind to BoundService");
                    boundService = null;
                }
            }
        });

        Button finishActivityButton = (Button) findViewById(R.id.finish_activity_button);
        //Button for finishing the this Activity so that we can observe a Service running without
        //an instance of this Activity
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
        //Don't forget to unbind from boundService if you are bound to it
        if (boundService != null) {
            unbindService(serviceConnection);
            boundService = null;
        }
    }
}