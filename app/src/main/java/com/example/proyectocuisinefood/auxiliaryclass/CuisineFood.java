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
    public static final String SERVER_KEY = "AAAA7k4jIWM:APA91bEWA2auKRNqgLwQBXUiIeFDEj5yHuGnBK22C8D5KsPkkUwukfErmjodHt1m2Ojb2Eb0nRQEkJYqVXjgkU5BqntP_S5uGEGOwcMe6CXjE3PSNnlGmUFwV_GWOT7Xx8jCMcAXH7_r";

    public static final String token1 = "dzy1hgVSThGaf-c-Ncm_1h:APA91bH-3AmMnmyA-DgTF5lkLmOqZe0J7AOZjhXJmj6Gn-0exJTTiKySQ-8EvmVeHXXnO-qM68pHKBgWx6JqWsktCIiw0RW-VpF_o0a1cd4JOSyrMVWMJukpR3Vc6hTWkDnDl98Dd_P6";
    public static final String token2 = "dQfPd2eWSI6SEq8JNVcHLM:APA91bFVZngySXLVZgPnTsOkYD5xb0IaL-V8VJ5Qsl03Qt9m8A1WEVDvqMGsBhZoxhvUbTth7-NR-qyOyK8pcUuvgFNbtdMdfD23EHULwgp1zVHRYs10w05Gt6cFLLbQiiA0txx0P7u9";

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
