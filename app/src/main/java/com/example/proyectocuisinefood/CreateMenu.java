package com.example.proyectocuisinefood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateMenu extends AppCompatActivity {

    ImageButton dishImage;
    EditText dishName, dishCost, dishDescription, dishTime;
    Button dishIngredients, createDish;
    RecyclerView recyclerViewIngredients;

    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;
    String storagePath = "restaurant/menu/*";
    private Uri imageUrl;
    String photo = "photo";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);



        dishImage = findViewById(R.id.imb_photo_dish);
        dishName = findViewById(R.id.ed_name_dish);
        dishCost = findViewById(R.id.ed_cost_dish);
        dishDescription = findViewById(R.id.ed_description_dish);
        dishTime = findViewById(R.id.ed_time_dish);
        dishIngredients = findViewById(R.id.b_ingredients_dish);
        createDish = findViewById(R.id.b_create_dish);
        recyclerViewIngredients = findViewById(R.id.r_ingredients);


    }
}