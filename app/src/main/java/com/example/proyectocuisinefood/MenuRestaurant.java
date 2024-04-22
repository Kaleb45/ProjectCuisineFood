package com.example.proyectocuisinefood;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.adapter.MenuAdapter;
import com.example.proyectocuisinefood.model.Dish;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MenuRestaurant extends AppCompatActivity {

    Button shoppingCart;
    RecyclerView menuRecyclerView;
    MenuAdapter menuAdapter;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_restaurant);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shoppingCart = findViewById(R.id.b_shopping_cart);

        menuRecyclerView = findViewById(R.id.r_menu_restaurant);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        shoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickShoppingCart();
            }
        });

        // Recibir el ID del restaurante del intent
        restaurantId = getIntent().getStringExtra("restaurantId");

        if (restaurantId != null && !restaurantId.isEmpty()) {
            // Consultar los platillos por el ID del restaurante
            Query query = db.collection("dish").whereEqualTo("restaurantId", restaurantId);

            FirestoreRecyclerOptions<Dish> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Dish>()
                    .setQuery(query, Dish.class).build();

            menuAdapter = new MenuAdapter(firestoreRecyclerOptions, this);
            menuAdapter.notifyDataSetChanged();
            menuRecyclerView.setAdapter(menuAdapter);

        }

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuRestaurant.this, RestaurantProfile.class);
                intent.putExtra("restaurantId",restaurantId);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MenuRestaurant.this, RestaurantProfile.class);
        intent.putExtra("restaurantId",restaurantId);
        startActivity(intent);
        finish();
    }

    private void onClickShoppingCart() {
        Intent intent = new Intent(MenuRestaurant.this, BuyOrders.class);
        intent.putExtra("restaurantId",restaurantId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        menuAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        menuAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.i_signout){
            mAuth.signOut();
            finish();
            startActivity(new Intent(MenuRestaurant.this, MainActivity.class));
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