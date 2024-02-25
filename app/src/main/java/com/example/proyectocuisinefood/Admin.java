package com.example.proyectocuisinefood;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.adapter.RestaurantAdapter;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Admin extends AppCompatActivity {

    Button createRestaurant;
    RecyclerView restaurantRecyclerView;
    RestaurantAdapter restaurantAdapter;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createRestaurant = findViewById(R.id.b_create_restaurant);

        restaurantRecyclerView = findViewById(R.id.r_restaurant);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = db.collection("restaurant");

        FirestoreRecyclerOptions<Restaurant> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Restaurant>().setQuery(query, Restaurant.class).build();

        restaurantAdapter = new RestaurantAdapter(firestoreRecyclerOptions);
        restaurantAdapter.notifyDataSetChanged(); // Va a notificar cada uno de los cambios

        restaurantRecyclerView.setAdapter(restaurantAdapter);

        createRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateRestaurant();
            }
        });
    }

    private void onClickCreateRestaurant() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        restaurantAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        restaurantAdapter.stopListening();
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
            startActivity(new Intent(Admin.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}