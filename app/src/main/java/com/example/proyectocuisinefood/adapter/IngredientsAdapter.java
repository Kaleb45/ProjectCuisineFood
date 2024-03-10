package com.example.proyectocuisinefood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Ingredients;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashSet;
import java.util.Set;

public class IngredientsAdapter extends FirestoreRecyclerAdapter<Ingredients, IngredientsAdapter.ViewHolder> {

    private Set<String> selectedIngredients = new HashSet<>();
    private IngredientSelectionListener listener;
    public IngredientsAdapter(@NonNull FirestoreRecyclerOptions<Ingredients> options, IngredientSelectionListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, int position, @NonNull Ingredients model) {
        holder.name.setText(model.getName());
        holder.isDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String ingredientName = model.getName();
            if (isChecked) {
                selectedIngredients.add(ingredientName);
                listener.onIngredientSelected(ingredientName);
            } else {
                selectedIngredients.remove(ingredientName);
                listener.onIngredientDeselected(ingredientName);
            }
        });
    }

    public interface IngredientSelectionListener {
        void onIngredientSelected(String ingredientName);
        void onIngredientDeselected(String ingredientName);
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ingredients,parent,false);
        return new ViewHolder(v);
    }

    public Set<String> getSelectedIngredients() {
        return selectedIngredients;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox isDefault;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.t_ingredients);
            isDefault = itemView.findViewById(R.id.c_ingredients);
        }
    }
}
