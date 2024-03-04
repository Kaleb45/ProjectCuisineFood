package com.example.proyectocuisinefood.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantAdapter.ViewHolder> {

    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Restaurant model) {
        holder.name.setText(model.getName());
        String photoLogo = model.getPhoto();
        try{
            if(!photoLogo.equals("")){
                Picasso.get()
                        .load(photoLogo)
                        .resize(150,150)
                        .into(holder.logo);
            }
        }catch (Exception e){
            Log.d("Exception","e: "+e);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // Vinculacion con los elementos del View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_restaurant,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView logo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.t_name_restaurant);
            logo = itemView.findViewById(R.id.im_logo_restaurant);
        }
    }
}
