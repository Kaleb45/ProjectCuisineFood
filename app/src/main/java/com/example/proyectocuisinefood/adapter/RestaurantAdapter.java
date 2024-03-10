package com.example.proyectocuisinefood.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.CreateMenu;
import com.example.proyectocuisinefood.MainActivity;
import com.example.proyectocuisinefood.Menu;
import com.example.proyectocuisinefood.PaymentMethods;
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
        String photoLogo = model.getLogo();
        try{
            if(!photoLogo.equals("")){
                Picasso.get()
                        .load(photoLogo)
                        .resize(720,720)
                        .into(holder.logo);
            }
        }catch (Exception e){
            Log.d("Exception","e: "+e);
        }
        holder.buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.restaurant_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.menu_option_menu){
                    Intent intentMenu = new Intent(v.getContext(), Menu.class);
                    v.getContext().startActivity(intentMenu);
                    return true;
                } else if(id == R.id.menu_option_payment){
                    // Ir a la Activity de Métodos de Pago
                    Intent intentPayment = new Intent(v.getContext(), PaymentMethods.class);
                    v.getContext().startActivity(intentPayment);
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
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
        ImageButton buttonMenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.t_name_restaurant);
            logo = itemView.findViewById(R.id.im_logo_restaurant);
            buttonMenu = itemView.findViewById(R.id.b_menu);
        }
    }
}
