package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.application.CreateMenu;
import com.example.proyectocuisinefood.application.PlaceOrders;
import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Dish;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MenuAdapter extends FirestoreRecyclerAdapter<Dish, MenuAdapter.ViewHolder> {
    private Context context;

    public MenuAdapter(@NonNull FirestoreRecyclerOptions<Dish> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position, @NonNull Dish model) {
        final int pos = position;
        holder.name.setText(model.getName());
        holder.cost.setText(model.getCost()+"$");
        String photoDish = model.getPhoto();
        try{
            if(!photoDish.equals("")){
                Picasso.get()
                        .load(photoDish)
                        .resize(720,720)
                        .centerCrop()
                        .into(holder.photo);
            }
        }catch (Exception e){
            Log.d("Exception","e: "+e);
        }

        // Obtener el tipo de usuario actual
        FirebaseUser currentUser = holder.mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Verificar el tipo de usuario
            holder.db.collection("user").document(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        holder.userType = documentSnapshot.getString("usertype");

                        // Filtrar según el tipo de usuario
                        if (holder.userType != null) {
                            if (holder.userType.equals("Administrador")) {
                                // Agregar un OnClickListener al botón de eliminar
                                holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Obtener el ID del platillo actual
                                        String dishId = getSnapshots().getSnapshot(pos).getId();

                                        // Eliminar el platillo de la base de datos
                                        deleteDish(dishId);
                                    }
                                });

                                holder.editIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Obtener el ID del platillo actual
                                        String dishId = getSnapshots().getSnapshot(pos).getId();

                                        // Iniciar la actividad para actualizar los datos del restaurante
                                        Intent intent = new Intent(context, CreateMenu.class);
                                        intent.putExtra("dishId", dishId);
                                        context.startActivity(intent);
                                    }
                                });
                            } else if(holder.userType.equals("Cliente")){
                                holder.dishId = getSnapshots().getSnapshot(pos).getId();

                                holder.name.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, PlaceOrders.class);
                                        intent.putExtra("dishId", holder.dishId);
                                        context.startActivity(intent);
                                    }
                                });
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("OrderAdapter", "Error al obtener el tipo de usuario: " + e.getMessage());
                }
            });
        }
    }

    // Método para eliminar un platillo de la base de datos
    private void deleteDish(String dishId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("dish").document(dishId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Platillo eliminado con exito", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error al eliminar el platillo", Toast.LENGTH_SHORT).show();
                    }
                });
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
        ImageButton deleteIcon, editIcon;
        int originalBackgroundColor;
        FirebaseFirestore db;
        FirebaseAuth mAuth;
        String userType, dishId;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            name = itemView.findViewById(R.id.t_name_dish);
            cost = itemView.findViewById(R.id.t_cost_dish);
            photo = itemView.findViewById(R.id.im_photo_dish);
            deleteIcon = itemView.findViewById(R.id.imb_delete_dish);
            editIcon = itemView.findViewById(R.id.imb_edit_dish);

            // Agregar LongClickListener al ImageView
            photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Obtener la URL de la imagen
                    String imageUrl = getItem(getAdapterPosition()).getPhoto();

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
                    if(userType.equals("Administrador")){
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
                    return false;
                }
            });

            // Agregar un ClickListener normal al TextView del nombre del platillo
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userType.equals("Administrador")){
                        // Restaurar el color original del fondo del layout
                        itemView.setBackgroundColor(originalBackgroundColor);

                        // Agregar el borde al fondo del layout
                        itemView.setBackgroundResource(R.drawable.border);

                        // Ocultar los iconos adicionales (ic_delete y ic_edit)
                        hideAdditionalIcons(itemView);
                    }
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
