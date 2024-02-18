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
import com.google.firebase.firestore.QuerySnapshot;

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
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        db = FirebaseFirestore.getInstance();
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

        // Validar que no haya campos vacíos
        if (name.isEmpty() || nameUser.isEmpty() || passwordUser.isEmpty() || phoneUser.isEmpty() || emailUser.isEmpty() || usertype.isEmpty()) {
            Toast.makeText(this, "No puede dejar espacios vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        // RQNF2: Validar que el nombre solo contenga letras mayúsculas o minúsculas
        if (!name.matches("[a-zA-Z]+")) {
            Toast.makeText(this, "El nombre solo puede contener letras", Toast.LENGTH_SHORT).show();
            return;
        }

        // RQNF3: Validar que el nombre de usuario contenga caracteres alfanuméricos
        if (!nameUser.matches("[a-zA-Z0-9]+")) {
            Toast.makeText(this, "El nombre de usuario solo puede contener letras y números", Toast.LENGTH_SHORT).show();
            return;
        }

        // RQNF4: Validar que la contraseña contenga caracteres alfanuméricos y no alfanuméricos
        if (!passwordUser.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")) {
            Toast.makeText(this, "La contraseña debe contener al menos una letra, un número y un carácter especial, y tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // RQNF5: Validar que la contraseña tenga al menos 8 caracteres
        if (passwordUser.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // RQNF6: Validar que el número de teléfono contenga solo caracteres numéricos
        if (!phoneUser.matches("[0-9]+")) {
            Toast.makeText(this, "El número de teléfono solo puede contener números", Toast.LENGTH_SHORT).show();
            return;
        }

        // RQNF7: Validar que el número de teléfono tenga una longitud de 10 dígitos
        if (phoneUser.length() != 10) {
            Toast.makeText(this, "El número de teléfono debe tener 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        // RQNF10: Validar que el correo electrónico contenga el carácter '@'
        if (!emailUser.contains("@")) {
            Toast.makeText(this, "El correo electrónico debe contener el carácter '@'", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si el usertype es Cocinero o Mesero, validar que los campos restaurantAssignedUser y codeRestaurantUser no estén vacíos
        if (usertype.equals("Cocinero") || usertype.equals("Mesero")) {
            if (restaurantAssignedUser.isEmpty() || codeRestaurantUser.isEmpty()) {
                Toast.makeText(this, "Los campos restaurante asignado y código de restaurante son obligatorios para los roles de Cocinero y Mesero", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        checkUsernameAndEmailUniqueness(nameUser, emailUser, name, passwordUser, phoneUser, usertype, restaurantAssignedUser, codeRestaurantUser);
    }

    private void checkUsernameAndEmailUniqueness(String username, String email, String name, String password, String phone, String usertype, String restaurantAssignedUser, String codeRestaurantUser) {
        // Verificar la unicidad del nombre de usuario
        db.collection("user").whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        // El nombre de usuario ya está en uso
                        Toast.makeText(SignIn.this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
                    } else {
                        // Verificar la unicidad del correo electrónico
                        db.collection("user").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // El correo electrónico ya está en uso
                                        Toast.makeText(SignIn.this, "El correo electrónico ya está en uso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Ambos nombre de usuario y correo electrónico son únicos, proceder con el registro
                                        registerUser(name, username, password, phone, email, usertype, restaurantAssignedUser, codeRestaurantUser);
                                    }
                                } else {
                                    Toast.makeText(SignIn.this, "Error al verificar la unicidad del correo electrónico", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(SignIn.this, "Error al verificar la unicidad del nombre de usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                if(usertype.equals("Cliente") || usertype.equals("Administrador")){
                    map.put("restaurantAssigned", "");
                    map.put("codeRestaurant", "");
                }
                else{
                    map.put("restaurantAssigned", restaurantAssignedUser);
                    map.put("codeRestaurant", codeRestaurantUser);
                }

                db.collection("user").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        String selectedItem = parent.getItemAtPosition(position).toString();

        // Verificar si el tipo de usuario seleccionado es "Cocinero" o "Mesero"
        if (selectedItem.equals("Cocinero") || selectedItem.equals("Mesero")) {
            // Si es "Cocinero" o "Mesero", hacer visibles los campos restaurantAssignedUser y codeRestaurantUser
            restauranteAsignado.setVisibility(View.VISIBLE);
            codigoRestaurante.setVisibility(View.VISIBLE);
        } else {
            // Si no es "Cocinero" o "Mesero", ocultar los campos restaurantAssignedUser y codeRestaurantUser
            restauranteAsignado.setVisibility(View.GONE);
            codigoRestaurante.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {//Funcion para cuando no se selecciona un item

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}