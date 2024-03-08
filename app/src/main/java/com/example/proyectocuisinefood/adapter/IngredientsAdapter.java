package com.example.proyectocuisinefood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Ingredients;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class IngredientsAdapter extends FirestoreRecyclerAdapter<Ingredients, IngredientsAdapter.ViewHolder> {

    public IngredientsAdapter(@NonNull FirestoreRecyclerOptions<Ingredients> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, int position, @NonNull Ingredients model) {
        holder.name.setText(model.getName());
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ingredients,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
