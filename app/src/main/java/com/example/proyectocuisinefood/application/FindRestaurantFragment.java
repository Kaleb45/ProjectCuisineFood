package com.example.proyectocuisinefood.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.adapter.IngredientsAdapter;
import com.example.proyectocuisinefood.adapter.RestaurantAdapter;
import com.example.proyectocuisinefood.adapter.RestaurantSelectedAdapter;
import com.example.proyectocuisinefood.model.Ingredients;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FindRestaurantFragment extends Fragment {

    SearchView findRestaurant;
    RecyclerView restaurantRecyclerView;
    RestaurantAdapter restaurantAdapter;
    Query query;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public FindRestaurantFragment() {
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
        View v = inflater.inflate(R.layout.fragment_find, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        findRestaurant = v.findViewById(R.id.sv_find_restaurant);

        restaurantRecyclerView = v.findViewById(R.id.r_find_restaurant);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        query = db.collection("restaurant");

        FirestoreRecyclerOptions<Restaurant> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class).build();

        restaurantAdapter = new RestaurantAdapter(firestoreRecyclerOptions, v.getContext());
        restaurantAdapter.notifyDataSetChanged();
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        searchViewRestaurant();

        return v;
    }

    private void searchViewRestaurant() {
        findRestaurant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });
    }

    private void textSearch(String s) {
        FirestoreRecyclerOptions<Restaurant> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query.orderBy("name").startAt(s).endAt(s+"~"), Restaurant.class).build();

        restaurantAdapter = new RestaurantAdapter(firestoreRecyclerOptions, this.getContext());
        restaurantAdapter.startListening();
        restaurantRecyclerView.setAdapter(restaurantAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurantAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        restaurantAdapter.stopListening();
    }
}