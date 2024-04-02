package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.CreateMenu;
import com.example.proyectocuisinefood.CreateRestaurant;
import com.example.proyectocuisinefood.Menu;
import com.example.proyectocuisinefood.PaymentMethods;
import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantAdapter.ViewHolder> {

    private Context context;

    public RestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Restaurant model) {
        final int pos = position;

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
                showPopupMenu(v, holder);
            }
        });

        // Agregar un OnClickListener al botón de eliminar
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID del platillo actual
                String restaurantId = getSnapshots().getSnapshot(pos).getId();

                // Eliminar el platillo de la base de datos
                deleteDish(restaurantId);
            }
        });

        holder.editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener el ID del platillo actual
                String restaurantId = getSnapshots().getSnapshot(pos).getId();

                // Iniciar la actividad para actualizar los datos del restaurante
                Intent intent = new Intent(context, CreateRestaurant.class);
                intent.putExtra("restaurantId", restaurantId);
                context.startActivity(intent);
            }
        });
    }

    // Método para eliminar un platillo de la base de datos
    private void deleteDish(String restaurantId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurant").document(restaurantId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Restaurante eliminado con exito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error al eliminar el restaurante", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showPopupMenu(View v, ViewHolder holder) {
        // Obtener el DocumentSnapshot del restaurante en la posición actual
        DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.restaurant_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.menu_option_menu){
                    Intent intentMenu = new Intent(v.getContext(), Menu.class);
                    intentMenu.putExtra("restaurantId", snapshot.getId());
                    v.getContext().startActivity(intentMenu);
                    return true;
                } else if(id == R.id.menu_option_payment){
                    // Ir a la Activity de Métodos de Pago
                    Intent intentPayment = new Intent(v.getContext(), PaymentMethods.class);
                    intentPayment.putExtra("restaurantId", snapshot.getId());
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
        ImageButton buttonMenu, deleteIcon, editIcon;
        int originalBackgroundColor;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.t_name_restaurant);
            logo = itemView.findViewById(R.id.im_logo_restaurant);
            buttonMenu = itemView.findViewById(R.id.b_menu);
            deleteIcon = itemView.findViewById(R.id.imb_delete_restaurant);
            editIcon = itemView.findViewById(R.id.imb_edit_restaurant);

            logo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Obtener la URL de la imagen
                    String imageUrl = getItem(getAdapterPosition()).getLogo();

                    // Crear un diálogo personalizado para mostrar la imagen en tamaño completo
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_layout_image_viewer, null);
                    ImageView imageViewDialog = dialogView.findViewById(R.id.image_view_dialog);
                    Picasso.get().load(imageUrl).into(imageViewDialog); // Cargar la imagen en el ImageView del diálogo
                    builder.setView(dialogView);
                    builder.setCancelable(true);

                    // Mostrar el diálogo
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });

            // Agregar LongClickListener al TextView del nombre del platillo
            name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Guardar el color original del fondo del layout
                    originalBackgroundColor = itemView.getSolidColor();

                    // Cambiar el fondo del layout al opuesto del tema actual
                    int themeBackground = getThemeBackgroundColor(itemView.getContext());
                    int oppositeThemeBackground = getOppositeThemeBackground(themeBackground);
                    itemView.setBackgroundColor(oppositeThemeBackground);

                    // Mostrar los iconos adicionales (ic_delete y ic_edit)
                    showAdditionalIcons(itemView);

                    return true;
                }
            });

            // Agregar un ClickListener normal al TextView del nombre del platillo
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Restaurar el color original del fondo del layout
                    itemView.setBackgroundColor(originalBackgroundColor);

                    // Agregar el borde al fondo del layout
                    itemView.setBackgroundResource(R.drawable.border);

                    // Ocultar los iconos adicionales (ic_delete y ic_edit)
                    hideAdditionalIcons(itemView);
                }
            });
        }

        // Método para obtener el color de fondo del tema actual
        private int getThemeBackgroundColor(Context context) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.windowBackground, typedValue, true);
            return typedValue.data;
        }

        // Método para obtener el color opuesto al proporcionado
        private int getOppositeThemeBackground(int backgroundColor) {
            return backgroundColor == Color.WHITE ? Color.BLACK : Color.WHITE;
        }

        // Método para mostrar los iconos adicionales (ic_delete y ic_edit)
        private void showAdditionalIcons(View itemView) {

            // Hacer los iconos visibles
            deleteIcon.setVisibility(View.VISIBLE);
            editIcon.setVisibility(View.VISIBLE);
        }

        // Método para ocultar los iconos adicionales (ic_delete y ic_edit)
        private void hideAdditionalIcons(View itemView) {

            // Ocultar los iconos
            deleteIcon.setVisibility(View.GONE);
            editIcon.setVisibility(View.GONE);
        }
    }
}
