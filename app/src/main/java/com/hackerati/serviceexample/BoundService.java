package com.hackerati.serviceexample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BoundService extends Service {

    private IBinder localBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public class LocalBinder extends Binder {

        public BoundService getBoundServerInstance() {
            return BoundService.this;
        }
    }

    //And then you'd have the rest of your Service code here...
}