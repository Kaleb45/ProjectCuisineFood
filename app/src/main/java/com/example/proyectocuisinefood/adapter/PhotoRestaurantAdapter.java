package com.example.proyectocuisinefood.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotoRestaurantAdapter extends FirestoreRecyclerAdapter<Restaurant, PhotoRestaurantAdapter.ViewHolder> {

    private Context context;
    private String userType = "";
    private OnUploadImage onUploadImage;

    public PhotoRestaurantAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options, Context context) {
        super(options);
        this.context = context;

        getCurrentUserType();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Restaurant model) {
        final int pos = position;
        ArrayList<String> photoUrls = model.getPhoto();

        // Asegurarse de que la lista de URLs no sea nula y contenga al menos una URL
        if (photoUrls != null && !photoUrls.isEmpty()) {
            for(int i = 0; i < photoUrls.size(); i++){
                if(photoUrls.get(i) != null || !photoUrls.get(i).isEmpty()){
                    Picasso.get().load(photoUrls.get(i)).resize(400, 400).centerCrop().into(holder.photo[i]);

                    if(userType.equals("Cliente")){
                        int finalI = i;
                        holder.photo[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showImageDialog(photoUrls.get(finalI));
                            }
                        });
                    } else if(userType.equals("Administrador")){
                        int finalI = i;
                        holder.photo[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onUploadImage.uploadImage(photoUrls.get(finalI));
                            }
                        });
                    }
                }
            }
        }
    }

    public interface OnUploadImage{
        void uploadImage(String s);
    }

    private void showImageDialog(String imageUrl) {
        // Obtener la vista de la imagen
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_layout_image_viewer, null);
        ImageView dialogImage = dialogView.findViewById(R.id.image_view_dialog);

        Picasso.get().load(imageUrl).into(dialogImage);

        builder.setView(dialogView).show();
    }

    private void getCurrentUserType() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("user").document(currentUserId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            userType = documentSnapshot.getString("usertype");
                            notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_photo_restaurant,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageButton[] photo = new ImageButton[10];
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            photo[0] = itemView.findViewById(R.id.imb_photo_restaurant_profile_1);
            photo[1] = itemView.findViewById(R.id.imb_photo_restaurant_profile_2);
            photo[2] = itemView.findViewById(R.id.imb_photo_restaurant_profile_3);
            photo[3] = itemView.findViewById(R.id.imb_photo_restaurant_profile_4);
            photo[4] = itemView.findViewById(R.id.imb_photo_restaurant_profile_5);
            photo[5] = itemView.findViewById(R.id.imb_photo_restaurant_profile_6);
            photo[6] = itemView.findViewById(R.id.imb_photo_restaurant_profile_7);
            photo[7] = itemView.findViewById(R.id.imb_photo_restaurant_profile_8);
            photo[8] = itemView.findViewById(R.id.imb_photo_restaurant_profile_9);
            photo[9] = itemView.findViewById(R.id.imb_photo_restaurant_profile_10);

        }
    }
}
