package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.util.TypedValue;
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
import com.example.proyectocuisinefood.application.Cocinero;
import com.example.proyectocuisinefood.application.CreateRestaurant;
import com.example.proyectocuisinefood.application.Mesero;
import com.example.proyectocuisinefood.application.SignIn;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderAdapter extends FirestoreRecyclerAdapter<Orders, OrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Orders> sortedOrders;
    private OnOrderAddedListener onOrderAddedListener;

    public OrderAdapter(@NonNull FirestoreRecyclerOptions<Orders> options, ArrayList<Orders> sortedOrders, Context context) {
        super(options);
        this.sortedOrders = sortedOrders;
        this.context = context;
    }

    public void setOnOrderAddedListener(OnOrderAddedListener listener) {
        this.onOrderAddedListener = listener;
    }

    public interface OnOrderAddedListener {
        void onOrderAdded();
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position, @NonNull Orders model) {
        final int pos = position;

        if (model.getStatus().equals("Cancelada")) {
            notifyOrderCancelled();
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
                            if (holder.userType.equals("Cocinero") && model.getStatus().equals("En preparación")) {
                                // Mostrar solo las órdenes con estado "En preparación" para los cocineros
                                bindOrder(holder, sortedOrders.get(pos), position);
                            } else if (holder.userType.equals("Mesero") && model.getStatus().equals("En camino a la mesa")) {
                                // Mostrar solo las órdenes con estado "En camino a la mesa" para los meseros
                                bindOrder(holder, sortedOrders.get(pos), position);
                            } else {
                                holder.itemView.setVisibility(View.GONE);
                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                            }
                        }

                        if(position == getItemCount()-1){
                            if (onOrderAddedListener != null) {
                                onOrderAddedListener.onOrderAdded(); // Notificar a la actividad que se ha agregado una nueva orden
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

        // Agregar un OnCheckedChangeListener al CheckBox
        holder.isComplete.setOnCheckedChangeListener(null); // Eliminar el listener anterior para evitar llamadas múltiples

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
                            // Actualizar el estado de la orden en Firestore
                            String newStatus;
                            if (sortedOrders.get(pos).getStatus().equals("En preparación")) {
                                newStatus = "En camino a la mesa";
                            } else {
                                newStatus = "Entregado";
                            }
                            holder.updateOrderStatus(sortedOrders.get(pos).getOrderId(), newStatus);
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

    // Método para vincular los datos de la orden al ViewHolder
    private void bindOrder(@NonNull OrderAdapter.ViewHolder holder, @NonNull Orders model, int position) {
        final int pos = position;

        holder.numberTable.setText(sortedOrders.get(pos).getNumberTable());

        String dishId = sortedOrders.get(pos).getDishId(); // Obtener el dishId de la orden
        String userId = sortedOrders.get(pos).getUserId(); // Obtener el userId de la orden
        String restaurantId = sortedOrders.get(pos).getRestaurantId(); // Obtener el restaurantId de la orden

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

        holder.db.collection("restaurant").document(restaurantId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los datos del restaurante del documento

                    holder.orderMap = documentSnapshot.getString("tableDistribution");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("OrderAdapter", "Error al obtener los datos del cliente: " + e.getMessage());
            }
        });

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

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.userType.equals("Cocinero")) {
                    // Si el usuario es un cocinero, mostrar los ingredientes al hacer clic en el nombre del platillo
                    if (model.getIngredientIds() != null && !model.getIngredientIds().isEmpty() && model.getIngredientIds().size() != 0) {
                        showIngredientsDialog(holder, sortedOrders.get(pos));
                    } else {
                        // Si la lista de ingredientIds está vacía, mostrar un mensaje de que no hay ingredientes
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Ordenes");
                        builder.setMessage(model.getQuantity());
                        builder.create().show();

                        Toast.makeText(context, "Esta orden no tiene ingredientes", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showIngredientsDialog(@NonNull OrderAdapter.ViewHolder holder, @NonNull Orders model) {

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

    public void addOrder(ArrayList<Orders> order) {
        sortedOrders.addAll(order);
        notifyDataSetChanged(); // Notificar al RecyclerView que se ha agregado un nuevo elemento
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
                        MyFirebaseMessagingService.sendNotificationDevice("Orden cancelada", "Ordenes", token, context);

                        if (context instanceof Cocinero) {
                            MyFirebaseMessagingService.sendNotification("Orden cancelada", "Ordenes", token, context, Cocinero.class);
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
        String orderImage, orderMap, userType;

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

                    if(userType.equals("Cocinero")){
                        Picasso.get().load(orderImage).into(imageViewDialog); // Cargar la imagen en el ImageView del diálogo
                    } else if(userType.equals("Mesero")){
                        Picasso.get().load(orderMap).into(imageViewDialog); // Cargar el mapa de mesas en el ImageView del diálogo
                    }

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
            db.collection("orders").document(orderId)
                    .update("status", status)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Estado de la orden actualizado correctamente", Toast.LENGTH_SHORT).show();
                            Log.d("OrderAdapter", "Estado de la orden actualizado correctamente");
                            notifications();
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

        private void notifications(){
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


                            if (context instanceof Cocinero) {
                                MyFirebaseMessagingService.sendNotificationDevice("Orden completada", "Ordenes", CuisineFood.token2, context);
                                MyFirebaseMessagingService.sendNotification("Orden completada", "Ordenes", token, context, Cocinero.class);
                                ((Cocinero) context).loadData();
                            }
                            // Verificar si el contexto es una instancia de CreateRestaurant
                            else if (context instanceof Mesero) {
                                MyFirebaseMessagingService.sendNotificationDevice("Orden completada", "Ordenes", CuisineFood.token1, context);
                                MyFirebaseMessagingService.sendNotification("Orden completada", "Ordenes", token, context, Mesero.class);
                                ((Mesero) context).loadData();
                            }
                        }
                    });
        }
    }
}
