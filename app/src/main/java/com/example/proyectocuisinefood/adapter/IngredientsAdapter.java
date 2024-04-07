package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Ingredients;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class IngredientsAdapter extends FirestoreRecyclerAdapter<Ingredients, IngredientsAdapter.ViewHolder> {

    private ArrayList<String> selectedIngredientIds = new ArrayList<>();
    private ArrayList<String> ingredientIds = new ArrayList<>();
    private Context context;
    public IngredientsAdapter(@NonNull FirestoreRecyclerOptions<Ingredients> options, Context context, ArrayList<String> ingredientIds) {
        super(options);
        this.context = context;
        this.ingredientIds = ingredientIds;
    }

    // Método para obtener la lista de ingredientes seleccionados
    public ArrayList<String> getSelectedIngredientIds() {
        return selectedIngredientIds;
    }

    @Override
    protected void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, int position, @NonNull Ingredients model) {
        final int pos = position;
        holder.name.setText(model.getName());

        // Configurar el listener del CheckBox para todas las vistas
        holder.isDefault.setOnCheckedChangeListener(null);

        // Verificar si el ingrediente está seleccionado y establecer el CheckBox en consecuencia
        if (selectedIngredientIds.contains(getSnapshots().getSnapshot(pos).getId())) {
            holder.isDefault.setChecked(true);
        } else {
            holder.isDefault.setChecked(false);
        }

        // Establecer el listener del CheckBox para agregar o eliminar el ingrediente seleccionado
        holder.isDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Ingredients ingredient = getItem(pos);
                String ingredientId = getSnapshots().getSnapshot(pos).getId();
                if (isChecked) {
                    ingredientIds.add(ingredientId);
                } else {
                    ingredientIds.remove(ingredientId);
                }
            }
        });
    }

    // Método para establecer los ingredientes seleccionados
    public void setSelectedIngredients(ArrayList<String> ingredientIds) {
        if (!selectedIngredientIds.equals(ingredientIds)) { // Verificar si los ingredientes seleccionados han cambiado
            selectedIngredientIds.clear();
            selectedIngredientIds.addAll(ingredientIds);
            notifyDataSetChanged(); // Solo llamar a notifyDataSetChanged() si los ingredientes seleccionados han cambiado
        }
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ingredients,parent,false);
        return new ViewHolder(v);
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
