package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class PhotoRestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, PhotoRestaurantAdapter.ViewHolder> {

    private Context context;

    public PhotoRestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Restaurant model) {
        if(model.getPhoto() != null || !model.getPhoto().isEmpty()){
            Picasso.get().load(model.getPhoto()).centerCrop().resize(400, 400).into(holder.photo);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_photo_restaurant,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.im_photo_restaurant_profile);
        }
    }
}
