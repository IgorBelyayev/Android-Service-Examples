package com.hackerati.serviceexample;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MyIPIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 96;
    private static final String URL = "http://ip.jsontest.com";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private String myIpAddress;

    private String getStringFromTheNet(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void showNotification(String title, String text) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Notification.Builder builder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    public MyIPIntentService() {
        super(MyIPIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        try {
            String ipJsonString = getStringFromTheNet(URL);
            IPObject ipObject = gson.fromJson(ipJsonString, IPObject.class);
            myIpAddress = ipObject.getIpAddress();
        } catch (Exception e) {
            myIpAddress = "couldn't get IP";
        }
        showNotification("My IP Address: ", myIpAddress);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "IntentService is being destroyed", Toast.LENGTH_LONG).show();
    }

    /**
     * Simple Object that we will serialize the JSON response into
     */
    public static class IPObject {

        @SerializedName("ip")
        private String ipAddress;

        public String getIpAddress() {
            return ipAddress;
        }
    }
}
