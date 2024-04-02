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
    String currentRestaurantId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocinero);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        orderRecyclerView = findViewById(R.id.r_restaurant);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener el nombre de usuario del administrador actualmente autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid(); // Obtiene el UID del usuario

            // Consultar el documento del usuario para obtener el ID del restaurante asignado
            db.collection("users").document(currentUserId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Obtener el ID del restaurante asignado al usuario cocinero
                                currentRestaurantId = documentSnapshot.getString("restaurantAssigned");

                                // Verificar que se haya obtenido el ID del restaurante
                                if (currentRestaurantId != null && !currentRestaurantId.isEmpty()) {
                                    // Consultar las órdenes del restaurante actualmente asignado al cocinero
                                    Query query = db.collection("orders").whereEqualTo("restaurantId", currentRestaurantId);

                                    FirestoreRecyclerOptions<Orders> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Orders>()
                                            .setQuery(query, Orders.class).build();

                                    // Crear el adaptador y pasar el ID del restaurante
                                    orderAdapter = new OrderAdapter(firestoreRecyclerOptions, Cocinero.this, currentRestaurantId);
                                    orderAdapter.notifyDataSetChanged();
                                    orderRecyclerView.setAdapter(orderAdapter);
                                } else {
                                    Toast.makeText(Cocinero.this, "El restaurante no existe", Toast.LENGTH_SHORT).show();
                                    Log.e("Cocinero", "ID del restaurante no encontrado para el usuario");
                                }
                            } else {
                                Toast.makeText(Cocinero.this, "El cocinero no esta asignado a ningun restaurante", Toast.LENGTH_SHORT).show();
                                Log.e("Cocinero", "El documento del usuario no existe");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Cocinero.this, "El restaurante no existe", Toast.LENGTH_SHORT).show();
                            Log.e("Cocinero", "Error al obtener el ID del restaurante: " + e.getMessage());
                        }
                    });
        }
    }

    private void onClickCreateRestaurant() {
        startActivity(new Intent(Cocinero.this, CreateRestaurant.class));
        finish();
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