package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CreateMenu extends AppCompatActivity {

    ImageButton dishImage;
    EditText dishName, dishCost, dishDescription, dishTime;
    Button dishIngredients, dishCreate;
    RecyclerView recyclerViewIngredients;

    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;
    String storagePath = "restaurant/menu/*";
    private Uri imageUrl;
    String photo = "photo";
    ProgressDialog progressDialog;
    String downloadUri;

    private static final int PERMISSION_REQUEST_CODE = 300;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dishImage = findViewById(R.id.imb_photo_dish);
        dishName = findViewById(R.id.ed_name_dish);
        dishCost = findViewById(R.id.ed_cost_dish);
        dishDescription = findViewById(R.id.ed_description_dish);
        dishTime = findViewById(R.id.ed_time_dish);
        dishIngredients = findViewById(R.id.b_ingredients_dish);
        dishCreate = findViewById(R.id.b_create_dish);
        recyclerViewIngredients = findViewById(R.id.r_ingredients);

        dishImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });

        dishIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateIngredient();
            }
        });

        dishCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCreateDish();
            }
        });
    }

    private void onClickCreateIngredient() {

    }

    private void onClickCreateDish() {
        String name = dishName.getText().toString().trim();
        String cost = dishCost.getText().toString().trim();
        String description = dishDescription.getText().toString().trim();
        String time = dishTime.getText().toString().trim();

        if(name.isEmpty() || cost.isEmpty() || description.isEmpty() || time.isEmpty()){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            createDish(name, cost, description, time);
        }
    }

    private void createDish(String name, String cost, String description, String time) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("cost",cost);
        map.put("description",description);
        map.put("time",time);

        // Añadir los datos del restaurante...
        map.put("userId", mAuth.getCurrentUser().getUid()); // Usa el UID del usuario autenticado

        db.collection("dish").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String restaurantId = documentReference.getId(); // Aquí obtienes el ID del restaurante
                Toast.makeText(CreateMenu.this, "Creado Exitosamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateMenu.this, "Error al crear el restaurante", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermissions() {
        // Verificar si ya se tienen los permisos
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen los permisos, solicitarlos al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Si ya se tienen los permisos, abrir el selector de imágenes
            openImageSelector();
        }
    }

    // Método para abrir el selector de imágenes
    private void openImageSelector() {
        // Crear un diálogo para que el usuario elija entre la galería de fotos y la cámara
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción")
                .setItems(new String[]{"Galería de fotos", "Cámara"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Abrir la galería de fotos
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                                break;
                            case 1:
                                // Abrir la cámara fotográfica
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    // Método para manejar el resultado de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        openImageSelector();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                // Si el usuario elige una imagen de la galería de fotos, obtener la URI de la imagen
                imageUrl = data.getData();
                // Verificar qué ImageButton fue seleccionado y establecer la imagen en consecuencia
                loadImageIntoButton(dishImage, imageUrl.toString());

                sendPhoto(imageUrl);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                // Si el usuario toma una foto con la cámara, obtener la imagen
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUrl = data.getData();
                loadImageIntoButton(dishImage, imageUrl.toString());

                sendPhoto(imageUrl);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Método para obtener la URI de la imagen capturada con la cámara
    private Uri getImageUriFromCamera(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    // Método para establecer la imagen en el ImageButton correspondiente
    private void loadImageIntoButton(ImageButton imageButton, String imageUrl) {
        // Utilizar Picasso para cargar la imagen en el ImageButton
        Picasso.get().load(imageUrl).resize(150,150).into(imageButton);
    }


    private void sendPhoto(Uri imageUrl) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        String ruteStoragePhoto = storagePath+""+photo+""+mAuth.getUid()+"dishImage";
        StorageReference reference = storageReference.child(ruteStoragePhoto);
        reference.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl(); // Toma la url que se le asignara a la imagen
                while(!uriTask.isSuccessful());
                if(uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUri= uri.toString();
                            Toast.makeText(CreateMenu.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateMenu.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.i_signout){
            mAuth.signOut();
            finish();
            startActivity(new Intent(CreateMenu.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}