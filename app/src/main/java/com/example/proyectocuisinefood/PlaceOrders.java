package com.example.proyectocuisinefood;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.adapter.IngredientsAdapter;
import com.example.proyectocuisinefood.model.Ingredients;
import com.example.proyectocuisinefood.model.Orders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
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
    String dishId, photoDish, mapPhoto;
    ArrayList<String> ingredientIds;
    int numberTable;
    Orders orders;
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

        recyclerViewIngredients = findViewById(R.id.r_ingredients_restaurant);
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));

        orders = new Orders();

        // Recoge el ID del restaurante del Intent
        dishId = getIntent().getStringExtra("dishId");

        if(dishId != null || !dishId.isEmpty()){
            getDish();
            orderFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickCreateOrder();
                }
            });

        }

        Query query = db.collection("ingredients");

        FirestoreRecyclerOptions<Ingredients> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ingredients>()
                .setQuery(query, Ingredients.class).build();

        ingredientsAdapter = new IngredientsAdapter(firestoreRecyclerOptions);
        ingredientsAdapter.notifyDataSetChanged();
        recyclerViewIngredients.setAdapter(ingredientsAdapter);

        dishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(photoDish);
            }
        });

        mapDistribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageDialog(mapPhoto);
            }
        });

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceOrders.this, MenuRestaurant.class);
                intent.putExtra("restaurantId",orders.getRestaurantId());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PlaceOrders.this, MenuRestaurant.class);
        intent.putExtra("restaurantId",orders.getRestaurantId());
        finish();
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

    private void onClickCreateOrder() {
        // Crear una consulta para verificar si ya existe una orden con el mismo dishId y userId
        db.collection("orders")
                .whereEqualTo("dishId", dishId)
                .whereEqualTo("userId", mAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Obtener el primer documento (debería haber solo uno)
                            DocumentSnapshot orderSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Obtener el número de mesa de la orden existente
                            String existingTable = orderSnapshot.getString("numberTable");
                            String paymentMethodId = orderSnapshot.getString("paymentMethodId");

                            // Verificar si el número de mesa es diferente
                            if (existingTable != null && !existingTable.isEmpty() && !existingTable.equals(String.valueOf(numberTable))) {
                                Toast.makeText(PlaceOrders.this, "No puedes cambiar la mesa.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Obtener los ingredientes de la orden existente
                            ArrayList<String> existingIngredientIds = (ArrayList<String>) orderSnapshot.get("ingredientIds");

                            // Obtener la cantidad actual
                            String currentQuantity = orderSnapshot.getString("quantity");

                            // Incrementar la cantidad
                            int newQuantity = Integer.parseInt(currentQuantity) + 1;

                            if(paymentMethodId == null){
                                // Verificar si los ingredientes son iguales
                                if (ingredientsAdapter.getSelectedIngredientIds().equals(existingIngredientIds)) {
                                    // Si los ingredientes son iguales, actualizar la cantidad
                                    updateOrderQuantity(orderSnapshot.getId(), newQuantity);
                                } else {
                                    // Si los ingredientes son diferentes, agregar una nueva orden
                                    addNewOrder();
                                }
                            } else {
                                addNewOrder();
                            }
                        } else {
                            // Si no existe una orden con el mismo dishId y userId, agregar una nueva orden
                            addNewOrder();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrders.this, "Error al verificar la orden existente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateOrderQuantity(String orderId, int newQuantity) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("quantity", String.valueOf(newQuantity));

        db.collection("orders").document(orderId)
                .update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(PlaceOrders.this, MenuRestaurant.class);
                        intent.putExtra("restaurantId", orders.getRestaurantId());
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrders.this, "Error al actualizar la orden existente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addNewOrder(){

        // Obtener el numero de mesa
        String numberTableString = String.valueOf(numberTable);

        // Obtener el Timestamp actual
        Timestamp timestamp = Timestamp.now();

        Map<String, Object> map = new HashMap<>();
        map.put("dishId",dishId);
        map.put("ingredientIds", ingredientsAdapter.getSelectedIngredientIds());
        map.put("numberTable", numberTableString);
        map.put("paymentMethodId",null);
        map.put("quantity","1");
        map.put("restaurantId",orders.getRestaurantId());
        map.put("status","En preparación");
        map.put("time",orders.getTime());
        map.put("timestamp", timestamp);
        map.put("totalPrice",orders.getTotalPrice());
        map.put("userId",mAuth.getUid());

        db.collection("orders").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String orderId = documentReference.getId();  // Obtener el ID del nuevo documento
                orders.setOrderId(orderId);

                // Agregar el ID del nuevo documento al mismo documento con el nombre de campo "orderId"
                documentReference.update("orderId", orderId)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getScheduleIdOrder();
                                Intent intent = new Intent(PlaceOrders.this, MenuRestaurant.class);
                                intent.putExtra("restaurantId",orders.getRestaurantId());
                                startActivity(intent);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PlaceOrders.this, "Error al actualizar el documento", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PlaceOrders.this, "Error al crear la orden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getScheduleIdOrder() {

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
                .whereEqualTo("restaurantId", orders.getRestaurantId())
                .whereEqualTo("day", dayOfWeekString)  // Filtrar por el día actual
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Obtener el primer documento (horario)
                            DocumentSnapshot scheduleSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String scheduleId = scheduleSnapshot.getId();

                            // Actualizar el scheduleId en el objeto orders
                            orders.setScheduleId(scheduleId);

                            // Actualizar el scheduleId en la base de datos
                            if (orders.getOrderId() != null && !orders.getOrderId().isEmpty()) {
                                updateScheduleIdInOrder(orders.getOrderId(), scheduleId);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrders.this, "Error al obtener los datos del horario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateScheduleIdInOrder(String orderId, String scheduleId) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("scheduleId", scheduleId);

        db.collection("orders").document(orderId)
                .update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(PlaceOrders.this, "ScheduleId actualizado con éxito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PlaceOrders.this, "Error al actualizar el ScheduleId", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getDish (){
        db.collection("dish").document(dishId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String cost = documentSnapshot.getString("cost");
                String description = documentSnapshot.getString("description");
                String time = documentSnapshot.getString("time");
                photoDish = documentSnapshot.getString("photo");

                String restaurantId = documentSnapshot.getString("restaurantId");

                ingredientIds = (ArrayList<String>) documentSnapshot.get("ingredientIds");


                Picasso.get().load(photoDish).resize(150,150).into(dishImage);
                dishName.setText(name);
                dishCost.setText(cost+"$");
                dishDescription.setText(description);
                dishTime.setText(time);

                // Marcar los ingredientes seleccionados
                ingredientsAdapter.setSelectedIngredients(ingredientIds);

                // Añadir los datos al objeto Orders
                orders.setDishId(dishId);
                orders.setRestaurantId(restaurantId);
                orders.setTime(time);
                orders.setTotalPrice(cost);

                db.collection("restaurant").document(restaurantId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String quantityTables = documentSnapshot.getString("quantityTables");
                        mapPhoto = documentSnapshot.getString("tableDistribution");

                        if(mapPhoto != null || !mapPhoto.isEmpty()){
                            mapDistribution.setVisibility(View.VISIBLE);
                            Picasso.get().load(mapPhoto).resize(150,150).into(mapDistribution);
                        } else {
                            mapDistribution.setVisibility(View.GONE);
                        }

                        // Llenar el Spinner con la cantidad de mesas
                        if (quantityTables != null && !quantityTables.isEmpty()) {
                            int tables = Integer.parseInt(quantityTables);
                            ArrayList<String> tablesList = new ArrayList<>();
                            for (int i = 1; i <= tables; i++) {
                                tablesList.add("Mesa " + i);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(PlaceOrders.this, android.R.layout.simple_spinner_item, tablesList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            table.setAdapter(adapter);
                            table.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    numberTable = position + 1;  // Guardar la mesa seleccionada
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // No hacer nada
                                }
                            });
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