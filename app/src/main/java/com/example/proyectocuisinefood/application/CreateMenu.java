package com.example.proyectocuisinefood.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.adapter.IngredientsAdapter;
import com.example.proyectocuisinefood.model.Ingredients;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateMenu extends AppCompatActivity {

    ImageButton dishImage;
    EditText dishName, dishCost, dishDescription, dishTime;
    Button dishIngredients, dishCreate;
    RecyclerView recyclerViewIngredients;
    IngredientsAdapter ingredientsAdapter;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageReference;
    Uri imageUrl;
    ProgressDialog progressDialog;
    String restaurantId, photoDish;
    ArrayList<String> ingredientIds;
    private static final int PERMISSION_REQUEST_CODE = 300;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;

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
        recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));

        // Recoge el ID del restaurante del Intent
        restaurantId = getIntent().getStringExtra("restaurantId");
        String dishId = getIntent().getStringExtra("dishId");

        if(dishId == null || dishId.isEmpty()){
            dishCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickCreateDish();
                }
            });
        } else {
            dishCreate.setText("Actualizar Platillo");
            getDish(dishId);
            dishCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickUpdateDish(dishId);
                }
            });
        }

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

        Query query = db.collection("ingredients");

        FirestoreRecyclerOptions<Ingredients> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ingredients>()
                .setQuery(query, Ingredients.class).build();

        ingredientsAdapter = new IngredientsAdapter(firestoreRecyclerOptions);
        ingredientsAdapter.notifyDataSetChanged();
        recyclerViewIngredients.setAdapter(ingredientsAdapter);

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateMenu.this, Admin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CreateMenu.this, Admin.class);
        startActivity(intent);
        finish();
    }

    private void onClickUpdateDish(String dishId) {
        String name = dishName.getText().toString().trim();
        String cost = dishCost.getText().toString().trim();
        String description = dishDescription.getText().toString().trim();
        String time = dishTime.getText().toString().trim();
        String type = "";

        if(name.isEmpty() || cost.isEmpty() || description.isEmpty() || time.isEmpty()){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        } else if (!name.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$")) { // Verifica si el nombre contiene caracteres no válidos
            Toast.makeText(this, "El nombre del platillo solo puede contener letras", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(description.contains("Bebida") || description.contains("bebida")){
                type = "Bebida";
            } else {
                type = "Plato";
            }
            updateDish(name, cost, description, time, type, dishId);
        }
    }

    private void updateDish(String name, String cost, String description, String time, String type, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("cost",cost);
        map.put("description",description);
        map.put("time",time);
        map.put("type",type);
        map.put("restaurantId", restaurantId);
        map.put("photo", photoDish);

        map.put("ingredientIds", ingredientsAdapter.getSelectedIngredientIds());

        db.collection("dish").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CreateMenu.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateMenu.this, Admin.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateMenu.this, "Error al actualizar el platillo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDish (String id){
        db.collection("dish").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String cost = documentSnapshot.getString("cost");
                String description = documentSnapshot.getString("description");
                String time = documentSnapshot.getString("time");
                photoDish = documentSnapshot.getString("photo");
                restaurantId = documentSnapshot.getString("restaurantId");

                ingredientIds = (ArrayList<String>) documentSnapshot.get("ingredientIds");

                if(photoDish != null || !photoDish.isEmpty()){
                    Picasso.get().load(photoDish).resize(300,300).centerCrop().into(dishImage);
                    dishImage.setBackground(new ColorDrawable(Color.TRANSPARENT));
                }

                dishName.setText(name);
                dishCost.setText(cost);
                dishDescription.setText(description);
                dishTime.setText(time);

                // Marcar los ingredientes seleccionados
                ingredientsAdapter.setSelectedIngredients(ingredientIds);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateMenu.this, "Error al obtner los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickCreateIngredient() {
        CreateIngredients fm = new CreateIngredients();
        fm.show(getSupportFragmentManager(), "Navegar a ingredientes");
    }

    private void onClickCreateDish() {
        String name = dishName.getText().toString().trim();
        String cost = dishCost.getText().toString().trim();
        String description = dishDescription.getText().toString().trim();
        String time = dishTime.getText().toString().trim();
        String type = "";

        if(name.isEmpty() || cost.isEmpty() || description.isEmpty() || time.isEmpty()){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        } else if (!name.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$")) { // Verifica si el nombre contiene caracteres no válidos
            Toast.makeText(this, "El nombre del platillo solo puede contener letras", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(description.contains("Bebida") || description.contains("bebida")){
                type = "Bebida";
            } else {
                type = "Plato";
            }
            createDish(name, cost, description, time, type);
        }
    }


    private void createDish(String name, String cost, String description, String time, String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("cost",cost);
        map.put("description",description);
        map.put("time",time);
        map.put("type",type);
        map.put("restaurantId", restaurantId);
        map.put("photo", photoDish);

        map.put("ingredientIds", ingredientsAdapter.getSelectedIngredientIds());

        db.collection("dish").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String restaurantId = documentReference.getId(); // Aquí obtienes el ID del restaurante
                Toast.makeText(CreateMenu.this, "Creado Exitosamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateMenu.this, Admin.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateMenu.this, "Error al crear el platillo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para solicitar permisos
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
            if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                imageUrl = data.getData();
                loadImageIntoButton(dishImage, imageUrl);
                getIngredientIds();
                sendPhoto(imageUrl);
            } else if (requestCode == CAMERA_REQUEST_CODE && data != null) {
                // Si el usuario toma una foto con la cámara, obtener la URI de la imagen desde los datos extras
                imageUrl = getImageUriFromCamera(data);
                // Verificar qué ImageButton fue seleccionado y establecer la imagen en consecuencia
                loadImageIntoButton(dishImage, imageUrl);
                getIngredientIds();
                sendPhoto(imageUrl);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getIngredientIds(){
        ingredientsAdapter.notifyDataSetChanged();
        recyclerViewIngredients.setAdapter(ingredientsAdapter);
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
    private void loadImageIntoButton(ImageButton imageButton, Uri imageUrl) {
        // Utilizar Picasso para cargar la imagen en el ImageButton
        try {
            // Utilizar Picasso para cargar la imagen en el ImageButton
            Picasso.get().load(imageUrl.toString()).resize(300, 300).centerCrop().into(imageButton);
            imageButton.setBackground(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            // Manejar cualquier excepción que ocurra durante la carga de la imagen
            Log.e("LoadImageError", "Error loading image: " + e.getMessage());
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendPhoto(Uri imageUrl) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();

        // Obtener una referencia al documento del restaurante
        DocumentReference restaurantRef = db.collection("restaurant").document(restaurantId);

        // Obtener los datos del documento del restaurante
        restaurantRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Obtener el nombre del restaurante
                    String restaurantName = documentSnapshot.getString("name");

                    // Verificar si el nombre del restaurante no es nulo
                    if (restaurantName != null) {
                        // Obtener la fecha y hora actual en milisegundos
                        String timestamp = String.valueOf(System.currentTimeMillis());

                        // Construir el nombre de archivo único para la imagen
                        String fileName = "dishImage_" + timestamp+".jpg";

                        // Construir la ruta de almacenamiento para la imagen
                        String storagePath = "restaurant/" + restaurantName + "/menu/photo" + fileName;

                        // Obtener una referencia al StorageReference
                        StorageReference reference = storageReference.child(storagePath);

                        reference.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl(); // Toma la url que se le asignara a la imagen
                                while (!uriTask.isSuccessful()) ;
                                if (uriTask.isSuccessful()) {
                                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            photoDish = uri.toString();
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
                    } else {
                        // Si el nombre del restaurante es nulo, mostrar un mensaje de error
                        Toast.makeText(CreateMenu.this, "Error: El nombre del restaurante no está disponible", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                } else {
                    // Si no existe un documento con el ID proporcionado, mostrar un mensaje de error
                    Toast.makeText(CreateMenu.this, "Error: No se encontró el restaurante correspondiente", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Si ocurre un error al obtener los datos del documento, mostrar un mensaje de error
                Toast.makeText(CreateMenu.this, "Error: No se pudo obtener los datos del restaurante", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ingredientsAdapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ingredientsAdapter.stopListening();
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
        if(id== R.id.i_profile){
            UserProfileFragmentDialog upfd = new UserProfileFragmentDialog();
            upfd.show(getSupportFragmentManager(), "Navegar a Perfil de Usuario");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}