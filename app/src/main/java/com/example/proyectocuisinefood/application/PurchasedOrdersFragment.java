package com.example.proyectocuisinefood.application;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.adapter.UserOrderAdapter;
import com.example.proyectocuisinefood.model.Orders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class PurchasedOrdersFragment extends Fragment {

    RecyclerView recyclerViewOrderBuy;
    UserOrderAdapter userOrderAdapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    public PurchasedOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_purchased_orders, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerViewOrderBuy = v.findViewById(R.id.r_order_list);
        recyclerViewOrderBuy.setLayoutManager(new LinearLayoutManager(getContext()));

        String userId = mAuth.getUid();

        if (userId != null) {
            // Consultar los platillos por el ID del restaurante
            Query query = db.collection("orders").whereEqualTo("userId",userId).whereNotEqualTo("paymentMethodId",null);

            FirestoreRecyclerOptions<Orders> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Orders>()
                    .setQuery(query, Orders.class).build();

            userOrderAdapter = new UserOrderAdapter(firestoreRecyclerOptions, getContext());
            userOrderAdapter.notifyDataSetChanged();
            recyclerViewOrderBuy.setAdapter(userOrderAdapter);
        }

        return v;
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