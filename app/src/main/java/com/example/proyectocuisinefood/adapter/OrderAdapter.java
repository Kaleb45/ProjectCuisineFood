package com.example.proyectocuisinefood.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Order;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class OrderAdapter extends FirestoreRecyclerAdapter<Order, OrderAdapter.ViewHolder> {

    private Context context;

    public OrderAdapter(@NonNull FirestoreRecyclerOptions<Order> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position, @NonNull Order model) {
        final int pos = position;
        holder.name.setText(model.getOrderName());
        holder.description.setText(model.getOrderDescription()+"$");
        String photoDish = model.getOrderImage();
        try{
            if(!photoDish.equals("")){
                Picasso.get()
                        .load(photoDish)
                        .resize(720,720)
                        .into(holder.photo);
            }
        }catch (Exception e){
            Log.d("Exception","e: "+e);
        }
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_order,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView numberTable, name, description;
        ImageView photo;
        CheckBox isComplete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTable = itemView.findViewById(R.id.t_table_number);
            name = itemView.findViewById(R.id.t_name_order);
            description = itemView.findViewById(R.id.t_description_order);
            photo = itemView.findViewById(R.id.im_photo_order);
            isComplete = itemView.findViewById(R.id.c_complete_order);

            // Agregar LongClickListener al ImageView
            photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Obtener la URL de la imagen
                    String imageUrl = getItem(getAdapterPosition()).getOrderImage();

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
        }
    }
}
