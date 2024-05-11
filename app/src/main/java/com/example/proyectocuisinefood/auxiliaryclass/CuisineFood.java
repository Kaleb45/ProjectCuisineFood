package com.example.proyectocuisinefood.auxiliaryclass;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.payclip.dspread.ClipPlusApi;
import com.payclip.paymentui.client.ClipApi;

public class CuisineFood extends Application {

    private final String FCM_CHANNEL_ID = "FCM_CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();

        askNotificationChannel();

        ClipApi.init(this, new ClipPlusApi());
    }

    private void askNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(FCM_CHANNEL_ID, "FCM Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
