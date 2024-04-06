package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    TextView userName;
    ImageButton profileImage;
    FloatingActionButton editUserProfile;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    private static final int PERMISSION_REQUEST_CODE = 300;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    StorageReference storageReference;
    private Uri imageUrl;
    ProgressDialog progressDialog;
    String loadProfileImage, currentUserId, userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        userName = findViewById(R.id.t_user_name);
        profileImage = findViewById(R.id.imb_profile_image);
        editUserProfile = findViewById(R.id.fb_edit_user);

        getUserData();

        profileImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showProfileImage();
                return true;
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserProfileActivity.this, "Actualmente no esta disponible esta función", Toast.LENGTH_SHORT).show();
            }
        });

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (userType){
                    case "Administrador":
                        startActivity(new Intent(UserProfileActivity.this, Admin.class));
                        finish();
                        break;
                    case "Cocinero":
                        startActivity(new Intent(UserProfileActivity.this, Cocinero.class));
                        finish();
                        break;
                    case "Mesero":
                        startActivity(new Intent(UserProfileActivity.this, Mesero.class));
                        finish();
                        break;
                    case "Cliente":
                        startActivity(new Intent(UserProfileActivity.this, Cliente.class));
                        finish();
                        break;
                }
            }
        });
    }

    private void uploadImage() {
        // Crear un diálogo de opciones para seleccionar entre la galería y la cámara
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar fuente de imagen");
        builder.setItems(new CharSequence[]{"Galería", "Cámara"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        // Abrir la galería
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                        break;
                    case 1:
                        // Verificar si la aplicación tiene permiso para acceder a la cámara
                        if (ContextCompat.checkSelfPermission(UserProfileActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            // Abrir la cámara
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        } else {
                            // Solicitar permiso de cámara
                            ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                        }
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                // Imagen seleccionada desde la galería
                if (data != null) {
                    imageUrl = data.getData();
                    loadProfileImage = imageUrl.toString();
                    profileImage.setBackgroundResource(android.R.color.transparent);
                    Picasso.get().load(loadProfileImage).resize(400,400).centerCrop().into(profileImage);
                    sendPhoto(imageUrl);
                }
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                // Imagen capturada desde la cámara
                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    if (imageBitmap != null) {
                        imageUrl = getImageUri(this, imageBitmap);
                        loadProfileImage = imageUrl.toString();
                        profileImage.setBackgroundResource(android.R.color.transparent);
                        Picasso.get().load(loadProfileImage).resize(400,400).centerCrop().into(profileImage);
                        sendPhoto(imageUrl);
                    }
                }
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void saveImage() {
        Map<String, Object> map = new HashMap<>();
        map.put("photo", loadProfileImage);

        db.collection("user").document(currentUserId).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(UserProfileActivity.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this, "Error al actualizar el perfil de usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileImage() {

        // Crear un diálogo personalizado para mostrar la imagen en tamaño completo
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_layout_image_viewer, null);
        ImageView imageViewDialog = dialogView.findViewById(R.id.image_view_dialog);
        Picasso.get().load(loadProfileImage).into(imageViewDialog); // Cargar la imagen en el ImageView del diálogo
        builder.setView(dialogView);
        builder.setCancelable(true);

        // Mostrar el diálogo
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendPhoto(Uri imageUrl) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        String ruteStoragePhoto = "user/profile/"+mAuth.getUid();
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
                            Toast.makeText(UserProfileActivity.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            saveImage();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();

            db.collection("user").document(currentUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        userType = documentSnapshot.getString("usertype");
                        String name = documentSnapshot.getString("username");
                        userName.setText(name);

                        loadProfileImage = documentSnapshot.getString("photo");
                        if(loadProfileImage != null){
                            profileImage.setBackgroundResource(android.R.color.transparent);
                            Picasso.get().load(loadProfileImage).resize(350,350).centerCrop().into(profileImage);
                        } else {
                            Toast.makeText(UserProfileActivity.this, "Puede añadir una foto de perfil", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
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
            startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
            return true;
        }
        if(id== R.id.i_profile){
            startActivity(new Intent(UserProfileActivity.this, UserProfileActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}