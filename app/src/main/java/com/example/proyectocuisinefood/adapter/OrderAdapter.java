package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Orders;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class OrderAdapter extends FirestoreRecyclerAdapter<Orders, OrderAdapter.ViewHolder> {

    private Context context;

    public OrderAdapter(@NonNull FirestoreRecyclerOptions<Orders> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position, @NonNull Orders model) {
        final int pos = position;

        // Obtener el tipo de usuario actual
        FirebaseUser currentUser = holder.mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Verificar el tipo de usuario
            holder.db.collection("user").document(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String userType = documentSnapshot.getString("usertype");

                        // Filtrar según el tipo de usuario
                        if (userType != null) {
                            if (userType.equals("Cocinero") && model.getStatus().equals("En preparación")) {
                                // Mostrar solo las órdenes con estado "En preparación" para los cocineros
                                bindOrder(holder, model, pos);
                            } else if (userType.equals("Mesero") && model.getStatus().equals("En camino a la mesa")) {
                                // Mostrar solo las órdenes con estado "En camino a la mesa" para los meseros
                                bindOrder(holder, model, pos);
                            } else {
                                // Ocultar la vista del ViewHolder si no coincide con el tipo de usuario
                                holder.itemView.setVisibility(View.GONE);
                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
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

    // Método para vincular los datos de la orden al ViewHolder
    private void bindOrder(@NonNull OrderAdapter.ViewHolder holder, @NonNull Orders model, int pos) {
        holder.numberTable.setText(model.getNumberTable());

        String dishId = model.getDishId(); // Obtener el dishId de la orden
        String userId = model.getUserId();

        // Realizar consulta para obtener los datos del platillo
        holder.db.collection("dish").document(dishId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los datos del platillo del documento
                    String dishName = documentSnapshot.getString("name");
                    String dishDescription = documentSnapshot.getString("description");
                    holder.orderImage = documentSnapshot.getString("photo");

                    // Asignar los datos del platillo a los elementos de la vista del ViewHolder
                    holder.name.setText(dishName);
                    holder.description.setText(dishDescription);

                    // Cargar la imagen del platillo usando Picasso
                    if (holder.orderImage != null && !holder.orderImage.isEmpty()) {
                        Picasso.get().load(holder.orderImage).resize(720, 720).into(holder.photo);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OrderAdapter", "Error al obtener los datos del platillo: " + e.getMessage());
            }
        });

        holder.db.collection("user").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los datos del platillo del documento
                    String userName = documentSnapshot.getString("name");

                    holder.user.setText(userName);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OrderAdapter", "Error al obtener los datos del cliente: " + e.getMessage());
            }
        });

        // Agregar un OnCheckedChangeListener al CheckBox
        holder.isComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mostrar un cuadro de diálogo para confirmar si el platillo ha sido completado
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmar completado");
                    builder.setMessage("¿El platillo ha sido completado?");
                    builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Actualizar el estado de la orden a "completado" en Firestore
                            holder.updateOrderStatus(getSnapshots().getSnapshot(pos).getId(), "En camino a la mesa");
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Desmarcar el CheckBox
                            holder.isComplete.setChecked(false);
                        }
                    });
                    builder.create().show();
                }
            }
        });
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_order,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView numberTable, name, description, user;
        ImageView photo;
        CheckBox isComplete;
        FirebaseFirestore db;
        FirebaseAuth mAuth;
        String orderImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            numberTable = itemView.findViewById(R.id.t_table_number);
            name = itemView.findViewById(R.id.t_name_order);
            description = itemView.findViewById(R.id.t_description_order);
            user = itemView.findViewById(R.id.t_user_order);
            photo = itemView.findViewById(R.id.im_photo_order);
            isComplete = itemView.findViewById(R.id.c_complete_order);

            // Agregar LongClickListener al ImageView
            photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // Crear un diálogo personalizado para mostrar la imagen en tamaño completo
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_layout_image_viewer, null);
                    ImageView imageViewDialog = dialogView.findViewById(R.id.image_view_dialog);
                    Picasso.get().load(orderImage).into(imageViewDialog); // Cargar la imagen en el ImageView del diálogo
                    builder.setView(dialogView);
                    builder.setCancelable(true);

                    // Mostrar el diálogo
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });
        }

        // Método para actualizar el estado de la orden en Firestore
        private void updateOrderStatus(String orderId, String status) {
            FirebaseFirestore.getInstance().collection("orders").document(orderId)
                    .update("status", status)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Estado de la orden actualizado correctamente", Toast.LENGTH_SHORT).show();
                            Log.d("OrderAdapter", "Estado de la orden actualizado correctamente");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error al actualizar el estado de la orden", Toast.LENGTH_SHORT).show();
                            Log.e("OrderAdapter", "Error al actualizar el estado de la orden: " + e.getMessage());
                        }
                    });
        }
    }
}
