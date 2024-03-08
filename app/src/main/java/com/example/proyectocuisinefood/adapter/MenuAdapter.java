package com.example.proyectocuisinefood.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.Menu;
import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class MenuAdapter extends FirestoreRecyclerAdapter<Dish, MenuAdapter.ViewHolder> {

    public MenuAdapter(@NonNull FirestoreRecyclerOptions<Dish> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position, @NonNull Dish model) {
        holder.name.setText(model.getName());
        holder.cost.setText(model.getCost());
        String photoDish = model.getPhoto();
        try{
            if(!photoDish.equals("")){
                Picasso.get()
                        .load(photoDish)
                        .resize(720,720)
                        .into(holder.photo);
            }
        }catch (Exception e){
            Log.d("Exception","e: "+e);
        }
    }

    @NonNull
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_menu,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, cost;
        ImageView photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.t_name_dish);
            cost = itemView.findViewById(R.id.t_cost_dish);
            photo = itemView.findViewById(R.id.im_photo_dish);
        }
    }
}
