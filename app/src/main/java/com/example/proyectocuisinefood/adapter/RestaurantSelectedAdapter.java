package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.SignIn;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class RestaurantSelectedAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantSelectedAdapter.ViewHolder> {

    private Context context;

    public RestaurantSelectedAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantSelectedAdapter.ViewHolder holder, int position, @NonNull Restaurant model) {
        holder.name.setText(model.getName());
        holder.direction.setText(model.getDirection());
        String photoLogo = model.getLogo();
        try{
            if(!photoLogo.equals("")){
                Picasso.get()
                        .load(photoLogo)
                        .resize(400, 400)
                        .into(holder.logo);
            }
        }catch (Exception e){
            Log.d("Exception","e: "+e);
        }

        // Configurar OnClickListener en el TextView del nombre del restaurante
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre y el ID del documento del restaurante
                String restaurantName = model.getName();
                String restaurantId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();

                // Enviar el nombre del restaurante al SearchView de SignIn
                ((SignIn)context).setRestaurantName(restaurantName);

                // Enviar el ID del restaurante a la variable restauranteAsigando
                ((SignIn)context).setRestaurantAssigned(restaurantId);
            }
        });
    }

    @NonNull
    @Override
    public RestaurantSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_restaurant_selected,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, direction;
        ImageView logo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.t_name_restaurant_selected);
            direction = itemView.findViewById(R.id.t_direction_restaurant_selected);
            logo = itemView.findViewById(R.id.im_logo_restaurant_selected);
        }
    }
}
