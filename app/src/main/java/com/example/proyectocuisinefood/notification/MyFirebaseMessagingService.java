package com.example.proyectocuisinefood.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.application.Admin;
import com.example.proyectocuisinefood.application.Cliente;
import com.example.proyectocuisinefood.application.Cocinero;
import com.example.proyectocuisinefood.application.MainActivity;
import com.example.proyectocuisinefood.application.Mesero;
import com.example.proyectocuisinefood.application.PlaceOrders;
import com.example.proyectocuisinefood.application.SplashScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG_NOTIFICATION = "MyFirebaseMessagingService";
    private Class classApplication;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        if(message.getNotification() != null) {
            sendNotificationForeground(message.getNotification().getBody());
        }

        //super.onMessageReceived(message);
    }

    private void sendNotificationToast(String from, String body) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyFirebaseMessagingService.this, from +": "+body, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendNotificationForeground (String messageBody) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        if(userId != null){
            FirebaseFirestore.getInstance().collection("user").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String userType = documentSnapshot.getString("usertype");

                    if(userType != null){
                        switch (userType){
                            case "Administrador":
                                classApplication = Admin.class;
                                break;
                            case "Cliente":
                                classApplication = Cliente.class;
                                break;
                            case "Cocinero":
                                classApplication = Cocinero.class;
                                break;
                            case "Mesero":
                                classApplication = Mesero.class;
                                break;
                        }
                        Intent intent = new Intent(MyFirebaseMessagingService.this, classApplication);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_IMMUTABLE);

                        String channelId = "FCM_CHANNEL_ID";
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(MyFirebaseMessagingService.this, channelId)
                                        .setSmallIcon(R.drawable.logo_blanco)
                                        .setContentTitle("Cuisine Food")
                                        .setContentText(messageBody)
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        // Since android Oreo notification channel is needed.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel(channelId,
                                    "FCM Channel",
                                    NotificationManager.IMPORTANCE_DEFAULT);
                            notificationManager.createNotificationChannel(channel);
                        }

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MyFirebaseMessagingService.this, "Error al obtener la notificación", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    public void sendNotification(String messageBody, String title, String token, Context context, Class classApp) {
        Intent intent = new Intent(context, classApp);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "FCM_CHANNEL_ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.logo_blanco)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "FCM Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification*/, notificationBuilder.build());
    }

    public void sendNotificationDevice(String messageBody, String title, String token, PlaceOrders context, Class classApp) {
        String channelId = "FCM_CHANNEL_ID";
        // Construye el mensaje de la notificación

        // Envía el mensaje al dispositivo con el token especificado
        /*
        FirebaseMessaging.getInstance().send()
                .addOnCompleteListener(new OnCompleteListener<SendResponse>() {
                    @Override
                    public void onComplete(Task<SendResponse> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Notificación enviada exitosamente al dispositivo con token: " + token);
                        } else {
                            System.out.println("Error al enviar la notificación al dispositivo con token: " + token);
                        }
                    }
                });
                */
    }
}
