package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    Button logIn, signIn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}