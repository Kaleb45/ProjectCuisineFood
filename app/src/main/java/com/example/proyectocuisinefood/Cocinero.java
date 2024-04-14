package com.example.proyectocuisinefood;

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
import android.widget.Toast;

import com.example.proyectocuisinefood.adapter.OrderAdapter;
import com.example.proyectocuisinefood.adapter.RestaurantAdapter;
import com.example.proyectocuisinefood.model.Orders;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Cocinero extends AppCompatActivity {

    RecyclerView orderRecyclerView;
    OrderAdapter orderAdapter;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
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

        String restaurantId = getIntent().getStringExtra("restaurantId");

        if (restaurantId != null && !restaurantId.isEmpty()) {
            ArrayList<Orders> ordersList = new ArrayList<>();
            Query query = db.collection("orders").whereEqualTo("restaurantId", restaurantId).whereEqualTo("status","En preparación");

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
            orderAdapter.notifyDataSetChanged();
            orderRecyclerView.setAdapter(orderAdapter);
        }

        // Configurar el SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Realizar la acción de recarga
                cargarDatos(); // Método que debes implementar para cargar los datos nuevamente
            }
        });
    }

    private void cargarDatos() {
        Intent intent = getIntent(); // Obtener el intent actual
        finish(); // Finalizar la actividad actual
        startActivity(intent); // Iniciar la actividad de nuevo
    }

    @Override
    protected void onStart() {
        super.onStart();
        orderAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
            startActivity(new Intent(Cocinero.this, UserProfileActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}