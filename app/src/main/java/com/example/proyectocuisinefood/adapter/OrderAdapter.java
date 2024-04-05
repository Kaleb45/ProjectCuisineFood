package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.CreateIngredients;
import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.auxiliaryclass.SortedOrders;
import com.example.proyectocuisinefood.model.Orders;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrderAdapter extends FirestoreRecyclerAdapter<Orders, OrderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<SortedOrders> sortedOrders = new ArrayList<>();

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
                        holder.userType = documentSnapshot.getString("usertype");

                        // Filtrar según el tipo de usuario
                        if (holder.userType != null) {
                            if (holder.userType.equals("Cocinero") && model.getStatus().equals("En preparación")) {
                                // Mostrar solo las órdenes con estado "En preparación" para los cocineros
                                bindOrder(holder, model, position);
                            } else if (holder.userType.equals("Mesero") && model.getStatus().equals("En camino a la mesa")) {
                                // Mostrar solo las órdenes con estado "En camino a la mesa" para los meseros
                                bindOrder(holder, model, position);
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
    private void bindOrder(@NonNull OrderAdapter.ViewHolder holder, @NonNull Orders model, int position) {
        final int pos = position;

        holder.numberTable.setText(model.getNumberTable());

        String dishId = model.getDishId(); // Obtener el dishId de la orden
        String userId = model.getUserId(); // Obtener el userId de la orden
        String restaurantId = model.getRestaurantId(); // Obtener el restaurantId de la orden

        // Realizar consulta para obtener los datos del platillo
        holder.db.collection("dish").document(dishId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener los datos del platillo del documento
                    String dishName = documentSnapshot.getString("name");
                    String dishDescription = documentSnapshot.getString("description");
                    String dishTime = documentSnapshot.getString("time");
                    Integer time = Integer.valueOf(dishTime);
                    Toast.makeText(context, time, Toast.LENGTH_SHORT).show();
                    holder.orderImage = documentSnapshot.getString("photo");

                    // Asignar los datos del platillo a los elementos de la vista del ViewHolder
                    holder.name.setText(dishName);
                    holder.description.setText(dishDescription);

                    // Cargar la imagen del platillo usando Picasso
                    if (holder.orderImage != null && !holder.orderImage.isEmpty()) {
                        Picasso.get().load(holder.orderImage).resize(720, 720).into(holder.photo);
                    }

                    // Añadir la orden a la lista de órdenes ordenadas con su tiempo de preparación
                    //sortedOrders.add(new SortedOrders(model, dishTime));

                    // Ordenar la lista de órdenes según el tiempo de preparación del platillo
                    Collections.sort(sortedOrders, new Comparator<SortedOrders>() {
                        @Override
                        public int compare(SortedOrders o1, SortedOrders o2) {
                            // Ordenar en orden ascendente (menor tiempo de preparación primero)
                            return Integer.compare(o1.getTime(), o2.getTime());
                        }
                    });

                    // Llamar a bindOrder con la orden ordenada
                    //bindOrderSorted(holder, sortedOrders.get(pos).getOrder(), pos);

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
                            if (model.getStatus().equals("En preparación")) {
                                newStatus = "En camino a la mesa";
                            } else {
                                newStatus = "Entregado";
                            }
                            holder.updateOrderStatus(getSnapshots().getSnapshot(pos).getId(), newStatus);
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

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.userType.equals("Cocinero")) {
                    // Si el usuario es un cocinero, mostrar los ingredientes al hacer clic en el nombre del platillo
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
            }
        });
    }

    private void bindOrderSorted(ViewHolder holder, Orders order, int pos) {

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

        // Método para actualizar la vista de la orden con los datos de la orden ordenada según el tiempo de preparación
        public void updateOrderView(Orders model, int position) {
            // Obtener la orden de la lista ordenada en la posición especificada
            Orders orderedOrder = sortedOrders.get(position).getOrder();

            // Asignar los datos de la orden ordenada a los elementos de la vista del ViewHolder
            numberTable.setText(orderedOrder.getNumberTable());

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
