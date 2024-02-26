package com.example.proyectocuisinefood;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateRestaurant extends AppCompatActivity{

    String selectedItemCategory1, selectedItemCategory2;
    ImageButton restaurantLogo, restaurantMap, restaurantImage;
    EditText restaurantName, restaurantPhone, restaurantCode;
    Button restaurantDirection, restaurantRelated, continueCreate, mondaySchedule, tuesdaySchedule, wednesdaySchedule, thursdaySchedule, fridaySchedule, saturdaySchedule, sundaySchedule;
    Switch restaurantTable, restaurantIndication, restaurantVMPay;
    Spinner spinRestaurantCategory1, spinRestaurantCategory2;

    String[] typeCategory1={
            "Comida rápida",
            "Comida Casual",
            "Comida Gourmet",
            "Étnico",
    };

    String[] typeCategory2={
            "Mariscos",
            "Parrilla/Asador",
            "Vegetariano/Vegano",
            "Sushi",
            "Fondue",
            "Postres",
            "Desyuno/Brunch"
    };

    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        restaurantLogo = findViewById(R.id.imb_logo_restaurant);
        restaurantMap = findViewById(R.id.imb_map_tables_restaurant);
        restaurantImage = findViewById(R.id.imb_images_restaurant);

        restaurantName = findViewById(R.id.ed_name_restaurant);
        restaurantPhone = findViewById(R.id.ed_phone_restaurant);
        restaurantCode = findViewById(R.id.ed_code_restaurant);

        restaurantDirection = findViewById(R.id.b_direction_restaurant);
        restaurantRelated = findViewById(R.id.b_related_restaurant);
        continueCreate = findViewById(R.id.b_continue_restaurant);

        mondaySchedule = findViewById(R.id.b_monday_schedule);
        tuesdaySchedule = findViewById(R.id.b_tuesday_schedule);
        wednesdaySchedule = findViewById(R.id.b_wednesday_schedule);
        thursdaySchedule = findViewById(R.id.b_thursday_schedule);
        fridaySchedule = findViewById(R.id.b_friday_schedule);
        saturdaySchedule = findViewById(R.id.b_saturday_schedule);
        sundaySchedule = findViewById(R.id.b_sunday_schedule);

        restaurantTable = findViewById(R.id.bs_tables1_restaurant);
        restaurantIndication = findViewById(R.id.bs_tables2_restaurant);
        restaurantVMPay = findViewById(R.id.bs_vm_pay_restaurant);

        spinRestaurantCategory1 = findViewById(R.id.s_category1_restaurant);
        spinRestaurantCategory2 = findViewById(R.id.s_category2_restaurant);

        ArrayAdapter adapterSpinCategory1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,typeCategory1);
        adapterSpinCategory1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRestaurantCategory1.setAdapter(adapterSpinCategory1);

        ArrayAdapter adapterSpinCategory2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,typeCategory2);
        adapterSpinCategory2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRestaurantCategory2.setAdapter(adapterSpinCategory2);

        spinRestaurantCategory1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CreateRestaurant.this, ""+adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                selectedItemCategory1 = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinRestaurantCategory2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CreateRestaurant.this, ""+adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                selectedItemCategory2 = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mondaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule();
            }
        });

        tuesdaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule();
            }
        });

        wednesdaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule();
            }
        });

        thursdaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule();
            }
        });

        fridaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule();
            }
        });

        saturdaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule();
            }
        });

        sundaySchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule();
            }
        });

        continueCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickContinueCreate();
            }
        });
    }

    private void onClickSchedule() {

    }

    private void onClickContinueCreate() {
        String name = restaurantName.getText().toString().trim();
        String category1 = selectedItemCategory1;
        String category2 = selectedItemCategory2;
        String phone = restaurantPhone.getText().toString().trim();
        String code = restaurantCode.getText().toString().trim();

        if(name.isEmpty() && category1.isEmpty() && category2.isEmpty() && phone.isEmpty() && code.isEmpty()){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
        }
        else{
            createRestaurant(name, category1, category2, phone, code);
        }
    }

    private void createRestaurant(String name, String category1, String category2, String phone, String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("category1",category1);
        map.put("category2",category2);
        map.put("phone",phone);
        map.put("code",code);

        db.collection("restaurant").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(CreateRestaurant.this, "Creado Exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateRestaurant.this, "Error al crear el restaurante", Toast.LENGTH_SHORT).show();
            }
        });
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
            startActivity(new Intent(CreateRestaurant.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}