package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoRestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, PhotoRestaurantAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> photo;

    public PhotoRestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Restaurant model) {
        final int pos = position;

        photo = model.getPhoto();
        String photoUrl = photo.get(pos);
        if(photoUrl != null || !photoUrl.isEmpty()){
            Picasso.get().load(photoUrl).resize(400, 400).centerCrop().into(holder.photo);
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

            photo = itemView.findViewById(R.id.imb_photo_restaurant_profile);
        }
    }
}
