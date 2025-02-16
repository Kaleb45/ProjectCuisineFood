package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.content.DialogInterface;
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

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.application.Cliente;
import com.example.proyectocuisinefood.application.Cocinero;
import com.example.proyectocuisinefood.auxiliaryclass.CuisineFood;
import com.example.proyectocuisinefood.model.Orders;
import com.example.proyectocuisinefood.notification.MyFirebaseMessagingService;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
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

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (model.getIngredientIds() != null && !model.getIngredientIds().isEmpty() && model.getIngredientIds().size() != 0) {
                    showIngredientsDialog(holder, model);
                } else {
                    // Si la lista de ingredientIds está vacía, mostrar un mensaje de que no hay ingredientes
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Ordenes");
                    builder.setMessage(model.getQuantity());
                    builder.create().show();

                    Toast.makeText(context, "Esta orden no tiene ingredientes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showIngredientsDialog(@NonNull UserOrderAdapter.ViewHolder holder, @NonNull Orders model) {

        ArrayList<String> ingredientIds = model.getIngredientIds();

        // Consultar Firestore para obtener los detalles de los ingredientes basándote en los IDs
        holder.db.collection("ingredients")
                .whereIn(FieldPath.documentId(), ingredientIds)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        StringBuilder ingredientsText = new StringBuilder();
                        StringBuilder quantityText = new StringBuilder();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Obtener el nombre del ingrediente del documento y agregarlo al texto
                            String ingredientName = documentSnapshot.getString("name");
                            ingredientsText.append("\t\t\u2022 "+ingredientName+"\n\n");
                        }
                        quantityText.append("\n\t\tOrdenes: ");

                        // Crear el cuadro de diálogo para mostrar los ingredientes
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Ingredientes");

                        // Configurar el tamaño de la letra del texto del mensaje
                        TextView messageTextView = new TextView(context);
                        messageTextView.setText(ingredientsText+quantityText.toString()+model.getQuantity());
                        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Tamaño de letra en SP, puedes ajustarlo según lo necesites
                        builder.setView(messageTextView);

                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar el error si la consulta falla
                        Log.e("OrderAdapter", "Error al obtener los ingredientes: " + e.getMessage());
                        Toast.makeText(context, "Error al obtener los ingredientes", Toast.LENGTH_SHORT).show();
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
                    notifyOrderCancelled();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error al cancelar la orden", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void notifyOrderCancelled() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(MyFirebaseMessagingService.TAG_NOTIFICATION, "Error al obtener el token de registro de FCM", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(MyFirebaseMessagingService.TAG_NOTIFICATION, token);
                        //Toast.makeText(PlaceOrders.this, msg, Toast.LENGTH_SHORT).show();
                        MyFirebaseMessagingService.sendNotificationDevice("Orden cancelada", "Ordenes", CuisineFood.token2, context);
                        MyFirebaseMessagingService.sendNotification("Orden cancelada", "Ordenes", token, context, Cliente.class);

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
