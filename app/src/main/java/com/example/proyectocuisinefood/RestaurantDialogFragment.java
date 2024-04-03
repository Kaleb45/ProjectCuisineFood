package com.example.proyectocuisinefood;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.proyectocuisinefood.adapter.IngredientsAdapter;
import com.example.proyectocuisinefood.adapter.RestaurantSelectedAdapter;
import com.example.proyectocuisinefood.model.Ingredients;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class RestaurantDialogFragment extends DialogFragment {

    SearchView searchViewRestaurant;
    Query query;
    RecyclerView recyclerViewShowRestaurant;
    RestaurantSelectedAdapter restaurantSelectedAdapter;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_restaurant_dialog, container, false);
        db = FirebaseFirestore.getInstance();

        searchViewRestaurant = v.findViewById(R.id.sv_restaurant_selected);

        recyclerViewShowRestaurant = v.findViewById(R.id.r_show_restaurant_selected);
        recyclerViewShowRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));

        query = db.collection("restaurant");

        FirestoreRecyclerOptions<Restaurant> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Restaurant>()
                .setQuery(query, Restaurant.class).build();

        restaurantSelectedAdapter = new RestaurantSelectedAdapter(firestoreRecyclerOptions, getContext());
        restaurantSelectedAdapter.notifyDataSetChanged();
        recyclerViewShowRestaurant.setAdapter(restaurantSelectedAdapter);

        searchViewRestaurant();

        return v;
    }

    private void searchViewRestaurant() {
        searchViewRestaurant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        restaurantSelectedAdapter = new RestaurantSelectedAdapter(firestoreRecyclerOptions, getContext());
        restaurantSelectedAdapter.startListening();
        recyclerViewShowRestaurant.setAdapter(restaurantSelectedAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurantSelectedAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        restaurantSelectedAdapter.stopListening();
    }
}