package com.example.proyectocuisinefood.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.adapter.UserOrderAdapter;
import com.example.proyectocuisinefood.model.Orders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class OrderShoppingCartFragment extends Fragment implements UserOrderAdapter.OnOrderCanceledListener{

    TextView totalPrice;
    Button continueShopping;
    RecyclerView recyclerViewOrderBuy;
    UserOrderAdapter userOrderAdapter;
    Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId, price;
    public OrderShoppingCartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_order_shopping_cart, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        totalPrice = v.findViewById(R.id.t_total_price_orders_shopping_cart);
        continueShopping = v.findViewById(R.id.b_finish_shopping_cart);

        recyclerViewOrderBuy = v.findViewById(R.id.r_order_buy_shopping_cart);
        recyclerViewOrderBuy.setLayoutManager(new LinearLayoutManager(getContext()));

        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickContinueShopping();
            }
        });

        String userId = mAuth.getUid();

        if (userId != null) {
            // Consultar los platillos por el ID del restaurante
            Query query = db.collection("orders").whereEqualTo("userId",userId).whereEqualTo("paymentMethodId",null);

            FirestoreRecyclerOptions<Orders> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Orders>()
                    .setQuery(query, Orders.class).build();

            userOrderAdapter = new UserOrderAdapter(firestoreRecyclerOptions, getContext());
            userOrderAdapter.setOnOrderCanceledListener(this);
            userOrderAdapter.notifyDataSetChanged();
            recyclerViewOrderBuy.setAdapter(userOrderAdapter);
        }

        // Calcular y mostrar el costo total
        calculateTotalPrice();

        // Obtener id del restaurante
        getRestaurantIdOrders();

        return v;
    }

    @Override
    public void onOrderCanceled() {
        // Volver a calcular el precio total
        calculateTotalPrice();
    }

    private void onClickContinueShopping() {
        if(!totalPrice.getText().equals("0.00$")){
            Intent intent = new Intent(getContext(), PaymentMethods.class);
            intent.putExtra("totalPrice",price);
            intent.putExtra("restaurantId",restaurantId);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "El carro esta vacío", Toast.LENGTH_SHORT).show();
        }

    }

    private void getRestaurantIdOrders() {
        db.collection("orders")
                .whereEqualTo("userId", mAuth.getUid())
                .whereEqualTo("paymentMethodId", null)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String firstRestaurantId = null;
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            if (firstRestaurantId == null) {
                                firstRestaurantId = document.getString("restaurantId");
                            } else {
                                String currentRestaurantId = document.getString("restaurantId");
                                if (!firstRestaurantId.equals(currentRestaurantId)) {
                                    // Mostrar mensaje de error y salir del método
                                    Toast.makeText(getContext(), "Todas las órdenes deben ser del mismo restaurante", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }
                        // Si todas las órdenes son del mismo restaurante, asigna el ID al atributo restaurantId
                        restaurantId = firstRestaurantId;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        totalPrice.setText("0.00$");
                        Toast.makeText(getContext(), "Ocurrió un error al obtener el ID del restaurante", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void calculateTotalPrice() {
        totalPrice.setText("0.00$");
        db.collection("orders")
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
                                total += Double.parseDouble(order.getTotalPrice())*Double.parseDouble(order.getQuantity());
                            } 
                        }
                        totalPrice.setText(String.format("%.2f", total)+"$");  // Formatear el total a dos decimales
                        price = String.format("%.2f", total);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        totalPrice.setText("0.00$");
                        Toast.makeText(getContext(), "Ocurrio un error en el precio total", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        userOrderAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userOrderAdapter.stopListening();
    }
}