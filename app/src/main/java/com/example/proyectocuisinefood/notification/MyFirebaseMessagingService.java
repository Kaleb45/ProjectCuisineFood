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
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.application.Admin;
import com.example.proyectocuisinefood.application.Cliente;
import com.example.proyectocuisinefood.application.Cocinero;
import com.example.proyectocuisinefood.application.MainActivity;
import com.example.proyectocuisinefood.application.Mesero;
import com.example.proyectocuisinefood.application.PlaceOrders;
import com.example.proyectocuisinefood.application.SplashScreen;
import com.example.proyectocuisinefood.auxiliaryclass.CuisineFood;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG_NOTIFICATION = "MyFirebaseMessagingService";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        // Verifica si el mensaje contiene datos
        if (remoteMessage.getData().size() > 0) {
            // Procesa y muestra la notificación utilizando los datos recibidos
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("body");
            // Aquí puedes mostrar la notificación en la barra de notificaciones o hacer lo que desees con el mensaje recibido

            sendNotificationForeground(title, message);
        }
    }

    private void sendNotificationToast(String title, String body) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyFirebaseMessagingService.this, title +": "+body, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendNotificationForeground (String title, String messageBody) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        if(userId != null){
            FirebaseFirestore.getInstance().collection("user").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String userType = documentSnapshot.getString("usertype");

                    if(userType != null){
                        Intent intent = null;
                        switch (userType){
                            case "Administrador":
                                intent = new Intent(MyFirebaseMessagingService.this, Admin.class);
                                break;
                            case "Cliente":
                                intent = new Intent(MyFirebaseMessagingService.this, Cliente.class);
                                break;
                            case "Cocinero":
                            case "Mesero":
                                intent = new Intent();
                                break;
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0 /* Request code */, intent,
                                PendingIntent.FLAG_IMMUTABLE);

                        String channelId = "FCM_CHANNEL_ID";
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(MyFirebaseMessagingService.this, channelId)
                                        .setSmallIcon(R.drawable.logo_blanco)
                                        .setContentTitle(title)
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

                        notificationManager.notify(new Random().nextInt(9999), notificationBuilder.build());
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

    public static void sendNotification(String messageBody, String title, String token, Context context, Class classApp) {
        Intent intent = new Intent(context, classApp);
        if( context instanceof Cocinero || context instanceof Mesero){
            intent = new Intent();
        }

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

        notificationManager.notify(new Random().nextInt(9999), notificationBuilder.build());
    }

    public static void sendNotificationDevice(String messageBody, String title, String token, Context context) {

        /*RemoteMessage notificationMessage = new RemoteMessage.Builder(token + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(new Random().nextInt(9999))) // Asigna un ID de mensaje único
                .addData("title", title)
                .addData("body", messageBody)
                .build();

        // Envía la notificación a través de Firebase Messaging
        FirebaseMessaging.getInstance().send(notificationMessage);*/

        // Construye el mensaje de la notificación
        /*
        RequestQueue myRequest = Volley.newRequestQueue(context);
        JSONObject json = new JSONObject();

        try{
            json.put("to",token);
            JSONObject notification = new JSONObject();
            notification.put("title",title);
            notification.put("body",messageBody);

            json.put("data",notification);
            String url = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,json,null,null){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type","application/json; UTF-8");
                    header.put("Authorization","key="+ CURRENT_KEY);

                    return header;
                }
            };
            myRequest.add(request);

        }catch (JsonIOException e){
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
         */

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        String url = "https://fcm.googleapis.com/fcm/send";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("to", token);
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", messageBody);
            jsonBody.put("notification", notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Manejar la respuesta del servidor (si es necesario)
                        Log.d("VolleyTAG", "Notificación enviada correctamente");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar el error de la solicitud (si es necesario)
                        Log.e("VolleyTAG", "Error al enviar la notificación: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + CuisineFood.SERVER_KEY);
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
