package com.example.proyectocuisinefood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.adapter.IngredientsAdapter;
import com.example.proyectocuisinefood.model.Ingredients;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceOrders extends AppCompatActivity {

    ImageView dishImage, mapDistribution;
    TextView dishName, dishCost, dishDescription, dishTime;
    Button orderFinish;
    Spinner table;
    RecyclerView recyclerViewIngredients;
    IngredientsAdapter ingredientsAdapter;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId, dishId, photoDish, name, cost, description, time, mapPhoto;
    ArrayList<String> ingredientIds;
    int numberTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_orders);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dishImage = findViewById(R.id.im_photo_dish_restaurant);
        mapDistribution = findViewById(R.id.im_map_distribution_restaurant);
        dishName = findViewById(R.id.ed_name_dish_restaurant);
        dishCost = findViewById(R.id.ed_cost_dish_restaurant);
        dishDescription = findViewById(R.id.ed_description_dish_restaurant);
        dishTime = findViewById(R.id.ed_time_dish_restaurant);
        orderFinish = findViewById(R.id.b_finish_order_restaurant);

        table = findViewById(R.id.s_number_table_restaurant);

        /*ArrayAdapter adapterSpinCategory1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,typeCategory1);
        adapterSpinCategory1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        table.setAdapter(adapterSpinCategory1);*/

        recyclerViewIngredients = findViewById(R.id.r_ingredients_restaurant);
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));

        // Recoge el ID del restaurante del Intent
        dishId = getIntent().getStringExtra("dishId");

        if(dishId != null || !dishId.isEmpty()){
            getDish(dishId);
            orderFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickCreateOrder(dishId);
                }
            });

        }

        Query query = db.collection("ingredients");

        FirestoreRecyclerOptions<Ingredients> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ingredients>()
                .setQuery(query, Ingredients.class).build();

        ingredientsAdapter = new IngredientsAdapter(firestoreRecyclerOptions);
        ingredientsAdapter.notifyDataSetChanged();
        recyclerViewIngredients.setAdapter(ingredientsAdapter);

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceOrders.this, Cliente.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PlaceOrders.this, Cliente.class);
        startActivity(intent);
        finish();
    }

    private void onClickCreateOrder(String dishId) {
        Intent intent = new Intent(PlaceOrders.this, Cliente.class);
        intent.putExtra("dishId",dishId);
        startActivity(intent);
        finish();
    }

    private void getDish (String id){
        db.collection("dish").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name = documentSnapshot.getString("name");
                cost = documentSnapshot.getString("cost");
                description = documentSnapshot.getString("description");
                time = documentSnapshot.getString("time");
                photoDish = documentSnapshot.getString("photo");
                restaurantId = documentSnapshot.getString("restaurantId");

                ingredientIds = (ArrayList<String>) documentSnapshot.get("ingredientIds");

                Picasso.get().load(photoDish).resize(150,150).into(dishImage);
                dishName.setText(name);
                dishCost.setText(cost);
                dishDescription.setText(description);
                dishTime.setText(time);

                // Marcar los ingredientes seleccionados
                ingredientsAdapter.setSelectedIngredients(ingredientIds);

                db.collection("restaurant").document(restaurantId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mapPhoto = documentSnapshot.getString("tableDistribution");
                        if(mapPhoto != null || !mapPhoto.isEmpty()){
                            mapDistribution.setVisibility(View.VISIBLE);
                            Picasso.get().load(mapPhoto).resize(150,150).into(mapDistribution);
                        } else {
                            mapDistribution.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrders.this, "Error al obtner los datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlaceOrders.this, "Error al obtner los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ingredientsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ingredientsAdapter.stopListening();
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
            startActivity(new Intent(PlaceOrders.this, MainActivity.class));
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