package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectocuisinefood.adapter.MenuAdapter;
import com.example.proyectocuisinefood.adapter.PhotoRestaurantAdapter;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class RestaurantProfile extends AppCompatActivity {

    ImageView logoRestaurant, mapDistributionRestaurant, photoDish;
    TextView nameRestaurant, directionRestaurant, openRestaurant, closeRestaurant, nameDish, costDish, tableIndicationRestaurant;
    Button seeMoreDish, phoneRestaurant;
    RecyclerView recyclerViewPhotoRestaurant;
    PhotoRestaurantAdapter photoRestaurantAdapter;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String logo, tableDistribution, dishPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_profile);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logoRestaurant = findViewById(R.id.im_logo_restaurant_profile);
        mapDistributionRestaurant = findViewById(R.id.im_map_tables_restaurant_profile);
        photoDish = findViewById(R.id.im_photo_dish_restaurant_profile);
        nameRestaurant = findViewById(R.id.t_name_restaurant_profile);
        phoneRestaurant = findViewById(R.id.b_phone_restaurant_profile);
        directionRestaurant = findViewById(R.id.t_direction_restaurant_profile);
        openRestaurant = findViewById(R.id.t_open_restaurant_profile);
        closeRestaurant = findViewById(R.id.t_close_restaurant_profile);
        nameDish = findViewById(R.id.t_name_dish_restaurant_profile);
        costDish = findViewById(R.id.t_cost_dish_restaurant_profile);
        tableIndicationRestaurant = findViewById(R.id.t_indication_table_restaurant_profile);
        seeMoreDish = findViewById(R.id.b_dish_restaurant_profile);

        recyclerViewPhotoRestaurant = findViewById(R.id.r_photo_restaurant_profile);
        recyclerViewPhotoRestaurant.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String restaurantId = getIntent().getStringExtra("restaurantId");

        if(restaurantId != null || !restaurantId.isEmpty()){
            photoRestaurantAdapter.setNewRestaurant("Modificación");
            getRestaurant(restaurantId);
            Query query = db.collection("restaurant").whereEqualTo(FieldPath.documentId(), restaurantId);

            FirestoreRecyclerOptions<Restaurant> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Restaurant>()
                    .setQuery(query, Restaurant.class).build();

            photoRestaurantAdapter = new PhotoRestaurantAdapter(firestoreRecyclerOptions, this);
            photoRestaurantAdapter.notifyDataSetChanged();
            recyclerViewPhotoRestaurant.setAdapter(photoRestaurantAdapter);
        }

        logoRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(logo);
            }
        });

        mapDistributionRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(tableDistribution);
            }
        });

        photoDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(dishPhoto);
            }
        });

        phoneRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneRestaurant.getText().toString().trim();
                if (!phoneNumber.isEmpty()) {
                    makePhoneCall(phoneNumber);
                } else {
                    Toast.makeText(RestaurantProfile.this, "Número de teléfono no válido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seeMoreDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantProfile.this, MenuRestaurant.class);
                intent.putExtra("restaurantId",restaurantId);
                startActivity(intent);
                finish();
            }
        });

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantProfile.this, Cliente.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RestaurantProfile.this, Cliente.class);
        startActivity(intent);
        finish();
    }

    private void makePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no están concedidos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            return;
        }
        startActivity(intent);
    }

    private void showImageDialog(String imageUrl) {
        // Obtener la vista de la imagen
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_layout_image_viewer, null);
        ImageView dialogImage = dialogView.findViewById(R.id.image_view_dialog);

        Picasso.get().load(imageUrl).into(dialogImage);

        builder.setView(dialogView).show();
    }

    private void getRestaurant (String id){
        db.collection("restaurant").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String phone = documentSnapshot.getString("phone");
                String direction = documentSnapshot.getString("direction");
                logo = documentSnapshot.getString("logo");
                tableDistribution = documentSnapshot.getString("tableDistribution");
                String tableIndication = documentSnapshot.getString("tableIndication");

                if(!logo.isEmpty() || logo != null){
                    Picasso.get().load(logo).resize(150,150).into(logoRestaurant);
                }

                if(!tableDistribution.isEmpty() || tableDistribution != null){
                    mapDistributionRestaurant.setVisibility(View.VISIBLE);
                    Picasso.get().load(tableDistribution).resize(150,150).into(mapDistributionRestaurant);
                } else {
                    mapDistributionRestaurant.setVisibility(View.GONE);
                }

                if(tableIndication != null){
                    tableIndicationRestaurant.setVisibility(View.VISIBLE);
                } else {
                    tableIndicationRestaurant.setVisibility(View.GONE);
                }

                nameRestaurant.setText(name);
                phoneRestaurant.setText(phone);
                directionRestaurant.setText(direction);

                db.collection("dish").whereEqualTo("restaurantId",id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Obtener el primer documento (platillo)
                            DocumentSnapshot dishSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            String dishName = dishSnapshot.getString("name");
                            String dishCost = dishSnapshot.getString("cost");
                            dishPhoto = dishSnapshot.getString("photo");

                            nameDish.setText(dishName);
                            costDish.setText(dishCost);

                            if (dishPhoto != null && !dishPhoto.isEmpty()) {
                                Picasso.get().load(dishPhoto).resize(150, 150).into(photoDish);
                            }
                        } else {
                            // Si no hay platillos, puedes ocultar o mostrar un mensaje
                            nameDish.setText("No hay platillos disponibles");
                            costDish.setText("");
                            photoDish.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RestaurantProfile.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                });

                // Obtener el día actual
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                // Mapear el valor del día de la semana al nombre del día
                String dayOfWeekString;
                switch (dayOfWeek) {
                    case Calendar.SUNDAY:
                        dayOfWeekString = "Domingo";
                        break;
                    case Calendar.MONDAY:
                        dayOfWeekString = "Lunes";
                        break;
                    case Calendar.TUESDAY:
                        dayOfWeekString = "Martes";
                        break;
                    case Calendar.WEDNESDAY:
                        dayOfWeekString = "Miércoles";
                        break;
                    case Calendar.THURSDAY:
                        dayOfWeekString = "Jueves";
                        break;
                    case Calendar.FRIDAY:
                        dayOfWeekString = "Viernes";
                        break;
                    case Calendar.SATURDAY:
                        dayOfWeekString = "Sábado";
                        break;
                    default:
                        dayOfWeekString = "";
                }

                // Usar el día actual para obtener el horario correspondiente
                db.collection("schedules")
                        .whereEqualTo("restaurantId", id)
                        .whereEqualTo("day", dayOfWeekString)  // Filtrar por el día actual
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // Obtener el primer documento (horario)
                                    DocumentSnapshot scheduleSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                    // Obtener los datos del horario
                                    String openTime = scheduleSnapshot.getString("openTime");
                                    String closeTime = scheduleSnapshot.getString("closeTime");

                                    // Asignar los horarios correspondientes al día actual
                                    openRestaurant.setText(openTime);
                                    closeRestaurant.setText(closeTime);
                                } else {
                                    // Si no hay horario para el día actual, puedes ocultar o mostrar un mensaje
                                    openRestaurant.setText("No hay horario definido");
                                    closeRestaurant.setText("");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RestaurantProfile.this, "Error al obtener los datos del horario", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RestaurantProfile.this, "Error al obtner los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        photoRestaurantAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        photoRestaurantAdapter.stopListening();
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
            startActivity(new Intent(RestaurantProfile.this, MainActivity.class));
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