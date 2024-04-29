package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.proyectocuisinefood.adapter.MenuAdapter;
import com.example.proyectocuisinefood.adapter.RestaurantAdapter;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Menu extends AppCompatActivity {

    Button createMenu;
    RecyclerView menuRecyclerView;
    MenuAdapter menuAdapter;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    FloatingActionButton fabMenu;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabMenu = findViewById(R.id.fb_create_menu);

        createMenu = findViewById(R.id.b_create_menu);

        menuRecyclerView = findViewById(R.id.r_menu);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        createMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateMenu();
            }
        });

        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateMenu();
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
                Intent intent = new Intent(Menu.this, Admin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Menu.this, Admin.class);
        startActivity(intent);
        finish();
    }

    private void onClickCreateMenu() {
        Intent intent = new Intent(Menu.this, CreateMenu.class);
        intent.putExtra("restaurantId", restaurantId); // "restaurantId" es una clave y restaurantId es el valor que quieres pasar.
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        menuAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            startActivity(new Intent(Menu.this, MainActivity.class));
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