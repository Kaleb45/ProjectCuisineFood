package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Cocinero extends AppCompatActivity {

    RecyclerView orderRecyclerView;
    OrderAdapter orderAdapter;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocinero);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        orderRecyclerView = findViewById(R.id.r_order_cook);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        restaurantId = getIntent().getStringExtra("restaurantId");

        // Obtener el nombre de usuario del administrador actualmente autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (restaurantId != null && !restaurantId.isEmpty()) {
            String restaurantId = currentUser.getUid(); // Obtiene el UID del usuario

            Query query = db.collection("orders").whereEqualTo("restaurantId", restaurantId);

            FirestoreRecyclerOptions<Orders> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Orders>()
                    .setQuery(query, Orders.class).build();

            // Crear el adaptador y pasar el ID del restaurante
            orderAdapter = new OrderAdapter(firestoreRecyclerOptions, Cocinero.this);
            orderAdapter.notifyDataSetChanged();
            orderRecyclerView.setAdapter(orderAdapter);
        }
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
            startActivity(new Intent(Cocinero.this, LogIn.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}