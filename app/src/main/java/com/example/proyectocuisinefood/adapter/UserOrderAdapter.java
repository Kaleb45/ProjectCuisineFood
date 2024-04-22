package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.util.Log;
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

import com.example.proyectocuisinefood.BuyOrders;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserOrderAdapter extends FirestoreRecyclerAdapter<Orders, UserOrderAdapter.ViewHolder> {
    private Context context;
    private OnOrderCanceledListener onOrderCanceledListener;
    public UserOrderAdapter(@NonNull FirestoreRecyclerOptions<Orders> options, Context context) {
        super(options);
        this.context = context;
    }

    public interface OnOrderCanceledListener {
        void onOrderCanceled();
    }

    public void setOnOrderCanceledListener(OnOrderCanceledListener listener) {
        this.onOrderCanceledListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserOrderAdapter.ViewHolder holder, int position, @NonNull Orders model) {
        final int pos = position;

        if ("Cancelada".equals(model.getStatus())) {
            // Si el estado es "Cancelada", ocultar la vista del ViewHolder
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
        }

        if(model.getPaymentMethodId() != null){
            holder.deleteIcon.setVisibility(View.GONE);
        } else {
            holder.deleteIcon.setVisibility(View.VISIBLE);
        }

        String dishId = model.getDishId(); // Obtener el dishId de la orden

        // Realizar consulta para obtener los datos del platillo
        holder.db.collection("dish").document(dishId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los datos del platillo del documento
                    String dishName = documentSnapshot.getString("name");

                    holder.orderImage = documentSnapshot.getString("photo");

                    // Asignar los datos del platillo a los elementos de la vista del ViewHolder
                    holder.name.setText(dishName);
                    holder.cost.setText(model.getTotalPrice()+"$");

                    // Cargar la imagen del platillo usando Picasso
                    if (holder.orderImage != null && !holder.orderImage.isEmpty()) {
                        Picasso.get().load(holder.orderImage).resize(720, 720).into(holder.photo);
                    }

                    holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(model.getStatus().equals("En preparación")){
                                // Cancelar la orden
                                canceledOrder(holder, model.getOrderId());
                            } else {
                                Toast.makeText(context, "Ya no puede cancelar la orden", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OrderAdapter", "Error al obtener los datos del platillo: " + e.getMessage());
            }
        });
    }

    // Método para eliminar un platillo de la base de datos
    private void canceledOrder (@NonNull UserOrderAdapter.ViewHolder holder, String orderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("status","Cancelada");

        holder.db.collection("orders").document(orderId).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Orden cancelada", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Recargue en caso de que el precio no se actualice", Toast.LENGTH_SHORT).show();
                if (onOrderCanceledListener != null) {
                    onOrderCanceledListener.onOrderCanceled(); // Notificar a la actividad
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error al cancelar la orden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public UserOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_menu,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, cost;
        ImageView photo;
        ImageButton deleteIcon, editIcon;
        FirebaseFirestore db;
        FirebaseAuth mAuth;
        String userType, dishId, orderImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            name = itemView.findViewById(R.id.t_name_dish);
            cost = itemView.findViewById(R.id.t_cost_dish);
            photo = itemView.findViewById(R.id.im_photo_dish);
            deleteIcon = itemView.findViewById(R.id.imb_delete_dish);
            editIcon = itemView.findViewById(R.id.imb_edit_dish);

            photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // Crear un diálogo personalizado para mostrar la imagen en tamaño completo
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_layout_image_viewer, null);
                    ImageView imageViewDialog = dialogView.findViewById(R.id.image_view_dialog);

                    Picasso.get().load(orderImage).into(imageViewDialog);

                    builder.setView(dialogView);
                    builder.setCancelable(true);

                    // Mostrar el diálogo
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });
        }
    }
}
