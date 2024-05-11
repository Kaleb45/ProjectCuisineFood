package com.example.proyectocuisinefood.application;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyectocuisinefood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class FollowUpOrderFragment extends Fragment {

    OrderShoppingCartFragment orderShoppingCartFragment = new OrderShoppingCartFragment();
    PurchasedOrdersFragment purchasedOrdersFragment = new PurchasedOrdersFragment();
    ShowOrderTrackingFragment showOrderTrackingFragment = new ShowOrderTrackingFragment();
    BottomNavigationView navigationView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    public FollowUpOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_follow_up, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        navigationView = v.findViewById(R.id.bottom_navigation_follow);
        navigationView.setOnNavigationItemSelectedListener(myOnNavigationItemSelectedListener);

        // Establecer el segundo elemento como seleccionado por defecto
        navigationView.setSelectedItemId(R.id.option_find);

        // Cargar automáticamente el FindFragment al iniciar la actividad
        loadFragment(purchasedOrdersFragment);

        return v;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener myOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if(id == R.id.option_list){
                loadFragment(purchasedOrdersFragment);
                return true;
            }
            if(id == R.id.option_shopping_cart){
                loadFragment(orderShoppingCartFragment);
                return true;
            }
            if(id == R.id.option_show_tracking){
                //loadFragment(showOrderTrackingFragment);
                Toast.makeText(getContext(), "Opción no disponible actualmente", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        }
    };

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container_follow, fragment);
        transaction.commit();
    }

}