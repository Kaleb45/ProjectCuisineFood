package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.proyectocuisinefood.adapter.OrderAdapter;
import com.example.proyectocuisinefood.model.Orders;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    Button logIn, signIn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        logIn = findViewById(R.id.log_in);
        signIn = findViewById(R.id.sign_in);

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
                                        startActivity(new Intent(MainActivity.this, Cocinero.class));
                                        finish();
                                        return;
                                    case "Mesero":
                                        startActivity(new Intent(MainActivity.this, Mesero.class));
                                        finish();
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