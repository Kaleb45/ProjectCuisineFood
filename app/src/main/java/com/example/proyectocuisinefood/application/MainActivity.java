package com.example.proyectocuisinefood.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectocuisinefood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    TextView message;
    Button logIn, signIn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        message = findViewById(R.id.t_message_progress_bar);
        logIn = findViewById(R.id.log_in);
        signIn = findViewById(R.id.sign_in);

        progressBar = findViewById(R.id.progress_bar);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_login();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick_signin();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void onclick_signin() {
        Toast.makeText(MainActivity.this, "Registrarse", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(MainActivity.this, SignIn.class);
        startActivity(i);
    }

    private void onclick_login() {
        Toast.makeText(MainActivity.this, "Inicio de Sesión", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(MainActivity.this, LogIn.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            logIn.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
            // Si hay un usuario autenticado, redirigirlo a la actividad correspondiente según su tipo de usuario
            String userId = user.getUid();
            db.collection("user").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userType = document.getString("usertype");
                            if (userType != null) {
                                switch (userType) {
                                    case "Administrador":
                                        startActivity(new Intent(MainActivity.this, Admin.class));
                                        finish();
                                        return;
                                    case "Cocinero":
                                        // Consultar el documento del usuario para obtener el ID del restaurante asignado
                                        db.collection("user").document(userId).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            // Obtener el ID del restaurante asignado al usuario cocinero
                                                            String currentRestaurantId = documentSnapshot.getString("restaurantAssigned");

                                                            // Verificar que se haya obtenido el ID del restaurante
                                                            if (currentRestaurantId != null && !currentRestaurantId.isEmpty()) {
                                                                Intent intent = new Intent(MainActivity.this, Cocinero.class);
                                                                intent.putExtra("restaurantId", currentRestaurantId);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(MainActivity.this, "El restaurante no existe", Toast.LENGTH_SHORT).show();
                                                                Log.e("Cocinero", "ID del restaurante no encontrado para el usuario");
                                                            }
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "El cocinero no esta asignado a ningun restaurante", Toast.LENGTH_SHORT).show();
                                                            Log.e("Cocinero", "El documento del usuario no existe");
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Error al obtener el ID del restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.e("Cocinero", "Error al obtener el ID del restaurante: " + e.getMessage());
                                                    }
                                                });
                                        return;
                                    case "Mesero":
                                        // Consultar el documento del usuario para obtener el ID del restaurante asignado
                                        db.collection("user").document(userId).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            // Obtener el ID del restaurante asignado al usuario cocinero
                                                            String currentRestaurantId = documentSnapshot.getString("restaurantAssigned");

                                                            // Verificar que se haya obtenido el ID del restaurante
                                                            if (currentRestaurantId != null && !currentRestaurantId.isEmpty()) {
                                                                Intent intent = new Intent(MainActivity.this, Mesero.class);
                                                                intent.putExtra("restaurantId", currentRestaurantId);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(MainActivity.this, "El restaurante no existe", Toast.LENGTH_SHORT).show();
                                                                Log.e("Mesero", "ID del restaurante no encontrado para el usuario");
                                                            }
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "El mesero no esta asignado a ningun restaurante", Toast.LENGTH_SHORT).show();
                                                            Log.e("Mesero", "El documento del usuario no existe");
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(MainActivity.this, "Error al obtener el ID del restaurante: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.e("Mesero", "Error al obtener el ID del restaurante: " + e.getMessage());
                                                    }
                                                });
                                        return;
                                    case "Cliente":
                                        startActivity(new Intent(MainActivity.this, Cliente.class));
                                        finish();
                                        return;
                                    default:
                                        // Tipo de usuario no reconocido
                                        Toast.makeText(MainActivity.this, "Tipo de usuario no reconocido", Toast.LENGTH_SHORT).show();
                                        return;
                                }
                            } else {
                                // Tipo de usuario no especificado en la base de datos
                                Toast.makeText(MainActivity.this, "Tipo de usuario no especificado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Documento de usuario no encontrado en la base de datos
                            Toast.makeText(MainActivity.this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error al obtener el documento de usuario
                        Toast.makeText(MainActivity.this, "Error al obtener información de usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}