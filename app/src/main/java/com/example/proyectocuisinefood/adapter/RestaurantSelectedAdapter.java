package com.example.proyectocuisinefood.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantSelectedAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantSelectedAdapter.ViewHolder> {


    public RestaurantSelectedAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantSelectedAdapter.ViewHolder holder, int position, @NonNull Restaurant model) {

    }

    @NonNull
    @Override
    public RestaurantSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
