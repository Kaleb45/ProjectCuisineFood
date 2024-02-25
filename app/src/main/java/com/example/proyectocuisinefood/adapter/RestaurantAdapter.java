package com.example.proyectocuisinefood.adapter;

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

public class RestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantAdapter.ViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Restaurant model) {
        holder.restaurantName.setText(model.getNameRestaurant());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // Vinculacion con los elementos del View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_restaurant,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantName;
        ImageView restaurantLogo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.t_name_restaurant);
            restaurantLogo = itemView.findViewById(R.id.im_logo_restaurant);
        }
    }
}
