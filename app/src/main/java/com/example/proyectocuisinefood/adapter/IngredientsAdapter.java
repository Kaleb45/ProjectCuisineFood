package com.example.proyectocuisinefood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.Global.info;
import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Ingredients;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashSet;
import java.util.Set;

public class IngredientsAdapter extends FirestoreRecyclerAdapter<Ingredients, IngredientsAdapter.ViewHolder> {

    public IngredientsAdapter(@NonNull FirestoreRecyclerOptions<Ingredients> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, int position, @NonNull Ingredients model) {
        final int pos = position;
        holder.name.setText(model.getName());
        /*holder.isDefault.setChecked(false);
        holder.isDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckBox)view).isChecked()){
                    info.ListAddIngredients.add(info.ListAddIngredients.get(pos));
                }else if(!((CheckBox)view).isChecked()){
                    info.ListDeleteIngredients.remove(info.ListDeleteIngredients.get(pos));
                }else{
                    info.ListAddIngredients.remove(info.ListAddIngredients.get(pos));
                }
            }
        });*/
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ingredients,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        //CheckBox isDefault;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.t_ingredients);
            //isDefault = itemView.findViewById(R.id.c_ingredients);
        }
    }
}
