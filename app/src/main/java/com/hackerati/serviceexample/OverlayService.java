package com.hackerati.serviceexample;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class OverlayService extends Service {

    private View overlayView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater
                inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.overlay, new FrameLayout(this));

        Button button = (Button) overlayView.findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                OverlayService.this.stopSelf();
            }
        });

        windowManager.addView(overlayView, createLayoutParams());
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams
                layoutParams =
            new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                                               WindowManager.LayoutParams.WRAP_CONTENT,
                                               WindowManager.LayoutParams.TYPE_PHONE,
                                               WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                               | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                               | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                               PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.CENTER;
        return layoutParams;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).removeView(overlayView);
        Toast.makeText(this, "Service onDestroy()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
