package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText nombre, username, password, telefono, email, restauranteAsignado, codigoRestaurante;
    Spinner spinTipoUser;
    Button registrarse;
    String[] tipoUsuario={
            "Administrador",
            "Cocinero",
            "Mesero",
            "Cliente",
    };
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nombre = findViewById(R.id.ed_nombre_signin);
        username = findViewById(R.id.ed_username_signin);
        password = findViewById(R.id.ed_password_signin);
        telefono = findViewById(R.id.ed_telefono_signin);
        email = findViewById(R.id.ed_email_signin);
        restauranteAsignado = findViewById(R.id.ed_restaurante_signin);
        codigoRestaurante = findViewById(R.id.ed_codigo_signin);
        registrarse = findViewById(R.id.b_registro_signin);

        spinTipoUser = findViewById(R.id.s_tipo_signin);
        spinTipoUser.setOnItemSelectedListener(this);

        ArrayAdapter adaptador_libro = new ArrayAdapter(this, android.R.layout.simple_spinner_item,tipoUsuario);
        adaptador_libro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTipoUser.setAdapter(adaptador_libro);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickSignIn();
            }
        });
    }

    private void onclickSignIn() {
        String name = nombre.getText().toString().trim();
        String nameUser = username.getText().toString().trim();
        String passwordUser = password.getText().toString().trim();
        String phoneUser = telefono.getText().toString().trim();
        String emailUser = email.getText().toString().trim();
        String restaurantAssignedUser = restauranteAsignado.getText().toString().trim();
        String codeRestaurantUser = codigoRestaurante.getText().toString().trim();
        String usertype = spinTipoUser.toString().trim();

        if(name.isEmpty() && nameUser.isEmpty() && passwordUser.isEmpty() && phoneUser.isEmpty() && emailUser.isEmpty() && usertype.isEmpty()) {
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
        }
        else {
            registerUser(name, nameUser, passwordUser, phoneUser, emailUser, usertype, restaurantAssignedUser, codeRestaurantUser);
        }
    }

    private void registerUser(String name, String nameUser, String passwordUser, String phoneUser, String emailUser, String usertype, String restaurantAssignedUser, String codeRestaurantUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Map<String, Object> map = new HashMap<>();
                String id = mAuth.getCurrentUser().getUid();
                map.put("id",id);
                map.put("name",name);
                map.put("username",nameUser);
                map.put("password",passwordUser);
                map.put("phone",phoneUser);
                map.put("email",emailUser);
                map.put("usertype",usertype);
                if(usertype == "Cliente" || usertype == "Administrador"){
                    map.put("restaurantAssigned", "");
                    map.put("codeRestaurant", "");
                }
                else{
                    map.put("restaurantAssigned", restaurantAssignedUser);
                    map.put("codeRestaurant", codeRestaurantUser);
                }

                mFirestore.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(SignIn.this, LogIn.class));
                        Toast.makeText(SignIn.this, "Usuario registrado con exito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignIn.this, "Error al guardar la información", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignIn.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//Funcion para cuando se selecciona un item
        Toast.makeText(SignIn.this, ""+parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {//Funcion para cuando no se selecciona un item

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}