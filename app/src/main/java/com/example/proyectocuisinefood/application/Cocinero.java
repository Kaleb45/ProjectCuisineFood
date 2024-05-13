package com.example.proyectocuisinefood.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.adapter.OrderAdapter;
import com.example.proyectocuisinefood.auxiliaryclass.CuisineFood;
import com.example.proyectocuisinefood.model.Orders;
import com.example.proyectocuisinefood.notification.MyFirebaseMessagingService;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Cocinero extends AppCompatActivity implements OrderAdapter.OnOrderAddedListener{

    RecyclerView orderRecyclerView;
    OrderAdapter orderAdapter;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private ArrayList<Orders> ordersList;
    private String restaurantId;
    private Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocinero);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_cook);

        orderRecyclerView = findViewById(R.id.r_order_cook);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantId = getIntent().getStringExtra("restaurantId");

        if (restaurantId != null && !restaurantId.isEmpty()) {
            ordersList = new ArrayList<>();
            updateRecyclerView();
        }

        // Configurar el SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Realizar la acción de recarga
                loadData(); // Método que debes implementar para cargar los datos nuevamente
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void loadData() {
        Intent intent = getIntent(); // Obtener el intent actual
        finish(); // Finalizar la actividad actual
        startActivity(intent); // Iniciar la actividad de nuevo
    }

    private void updateRecyclerView(){
        query = db.collection("orders").whereEqualTo("restaurantId", restaurantId).whereEqualTo("status","En preparación");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Orders order = document.toObject(Orders.class);
                    ordersList.add(order);
                }

                // Ordenar las órdenes aquí
                Collections.sort(ordersList, new Comparator<Orders>() {
                    @Override
                    public int compare(Orders o1, Orders o2) {
                        return Integer.compare(Integer.parseInt(o1.getTime()), Integer.parseInt(o2.getTime()));
                    }
                });

                orderAdapter.addOrder(ordersList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Cocinero", "Error al obtener las órdenes: " + e.getMessage());
            }
        });

        FirestoreRecyclerOptions<Orders> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class).build();

        // Crear el adaptador y pasar la lista de órdenes ordenadas
        orderAdapter = new OrderAdapter(firestoreRecyclerOptions, ordersList, Cocinero.this);
        orderAdapter.setOnOrderAddedListener(this);
        orderAdapter.notifyDataSetChanged();
        orderRecyclerView.setAdapter(orderAdapter);
    }

    @Override
    public void onOrderAdded() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(MyFirebaseMessagingService.TAG_NOTIFICATION, "Error al obtener el token de registro de FCM", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(MyFirebaseMessagingService.TAG_NOTIFICATION, token);
                        //Toast.makeText(PlaceOrders.this, msg, Toast.LENGTH_SHORT).show();
                        MyFirebaseMessagingService.sendNotificationDevice("Ordenes actualizadas", "Ordenes", token,Cocinero.this);
                        MyFirebaseMessagingService.sendNotification("Ordenes actualizadas", "Ordenes", token, Cocinero.this, Cocinero.class);

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(orderAdapter != null){
            orderAdapter.startListening();
        } else {
            loadData();
            onOrderAdded();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.i_signout){
            mAuth.signOut();
            finish();
            startActivity(new Intent(Cocinero.this, MainActivity.class));
            return true;
        }
        if(id== R.id.i_profile){
            UserProfileFragmentDialog upfd = new UserProfileFragmentDialog();
            upfd.show(getSupportFragmentManager(), "Navegar a Perfil de Usuario");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}