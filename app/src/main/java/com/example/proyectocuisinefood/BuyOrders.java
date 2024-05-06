package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectocuisinefood.adapter.MenuAdapter;
import com.example.proyectocuisinefood.adapter.UserOrderAdapter;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Orders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class BuyOrders extends AppCompatActivity implements UserOrderAdapter.OnOrderCanceledListener{

    TextView totalPrice;
    Button continueShopping;
    RecyclerView recyclerViewOrderBuy;
    UserOrderAdapter userOrderAdapter;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId, price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_orders);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_buy_order);

        totalPrice = findViewById(R.id.t_total_price_orders);
        continueShopping = findViewById(R.id.b_finish_order_buy);

        recyclerViewOrderBuy = findViewById(R.id.r_order_buy);
        recyclerViewOrderBuy.setLayoutManager(new LinearLayoutManager(this));

        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickContinueShopping();
            }
        });

        // Recibir el ID del restaurante del intent
        restaurantId = getIntent().getStringExtra("restaurantId");

        String userId = mAuth.getUid();

        if (restaurantId != null && !restaurantId.isEmpty() && userId != null) {
            // Consultar los platillos por el ID del restaurante
            Query query = db.collection("orders").whereEqualTo("restaurantId", restaurantId).whereEqualTo("userId",userId).whereEqualTo("paymentMethodId",null);

            FirestoreRecyclerOptions<Orders> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Orders>()
                    .setQuery(query, Orders.class).build();

            userOrderAdapter = new UserOrderAdapter(firestoreRecyclerOptions, this);
            userOrderAdapter.setOnOrderCanceledListener(this);
            userOrderAdapter.notifyDataSetChanged();
            recyclerViewOrderBuy.setAdapter(userOrderAdapter);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Realizar la acción de recarga
                loadData();

            }
        });

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyOrders.this, MenuRestaurant.class);
                intent.putExtra("restaurantId",restaurantId);
                startActivity(intent);
                finish();
            }
        });

        // Calcular y mostrar el costo total
        calculateTotalPrice();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BuyOrders.this, MenuRestaurant.class);
        intent.putExtra("restaurantId",restaurantId);
        startActivity(intent);
        finish();
    }

    private void loadData() {
        Intent intent = getIntent(); // Obtener el intent actual
        finish(); // Finalizar la actividad actual
        startActivity(intent); // Iniciar la actividad de nuevo
    }

    @Override
    public void onOrderCanceled() {
        // Volver a calcular el precio total
        calculateTotalPrice();
    }

    private void onClickContinueShopping() {
        if(!totalPrice.getText().equals("0.00$")){
            Intent intent = new Intent(BuyOrders.this, PaymentMethods.class);
            intent.putExtra("totalPrice",price);
            intent.putExtra("restaurantId",restaurantId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "El carro esta vacío", Toast.LENGTH_SHORT).show();
        }

    }

    private void calculateTotalPrice() {
        totalPrice.setText("0.00$");
        db.collection("orders")
                .whereEqualTo("restaurantId", restaurantId)
                .whereEqualTo("userId", mAuth.getUid())
                .whereEqualTo("paymentMethodId", null)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        double total = 0;
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Orders order = document.toObject(Orders.class);
                            if (order != null && !"Cancelada".equals(order.getStatus())) {
                                total += Double.parseDouble(order.getTotalPrice());
                            } else {
                                Toast.makeText(BuyOrders.this, "Actualmente no tiene ordenes", Toast.LENGTH_SHORT).show();
                            }
                        }
                        totalPrice.setText(String.format("%.2f", total)+"$");  // Formatear el total a dos decimales
                        price = String.format("%.2f", total);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        totalPrice.setText("0.00$");
                        Toast.makeText(BuyOrders.this, "Ocurrio un error en el precio total", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userOrderAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userOrderAdapter.stopListening();
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
            startActivity(new Intent(BuyOrders.this, MainActivity.class));
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