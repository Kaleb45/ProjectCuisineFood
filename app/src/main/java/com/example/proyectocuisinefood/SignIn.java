package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import SecureStorage.AESUtil;
import SecureStorage.KeyStoreUtil;

public class SignIn extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText nombre, username, password, telefono, email, restauranteAsignado, codigoRestaurante;
    String selectedItem;
    Spinner spinTipoUser;
    Button registrarse;
    String[] tipoUsuario={
            "Administrador",
            "Cocinero",
            "Mesero",
            "Cliente",
    };
    ToggleButton toggleButtonShowPassword;
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
        toggleButtonShowPassword = findViewById(R.id.toggleButtonShowPassword);

        spinTipoUser = findViewById(R.id.s_tipo_signin);
        spinTipoUser.setOnItemSelectedListener(this);

        ArrayAdapter adaptador_libro = new ArrayAdapter(this, android.R.layout.simple_spinner_item,tipoUsuario);
        adaptador_libro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTipoUser.setAdapter(adaptador_libro);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    onclickSignIn();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        toggleButtonShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mostrar contraseña
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // Ocultar contraseña
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                // Mover el cursor al final del texto
                password.setSelection(password.getText().length());
            }
        });
    }

    private void onclickSignIn() throws Exception {
        String name = nombre.getText().toString().trim();
        String nameUser = username.getText().toString().trim();
        String passwordUser = password.getText().toString().trim();
        String phoneUser = telefono.getText().toString().trim();
        String emailUser = email.getText().toString().trim();
        String restaurantAssignedUser = restauranteAsignado.getText().toString().trim();
        String codeRestaurantUser = codigoRestaurante.getText().toString().trim();
        String usertype = selectedItem;

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
        if (!passwordUser.matches("(?=\\S+$)" +     // no debe contener espacios en blanco
                ".{8,}$")) {             // al menos 8 caracteres de longitud
            Toast.makeText(this, "La contraseña debe contener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordUser.matches("^(?=.*[0-9])" + // al menos un dígito
                "(?=.*[a-zA-Z])" +       // al menos una letra
                "(?=.*[@#$%^&+=])" +     // al menos un carácter especial
                ".{8,}$")) {             // al menos 8 caracteres en total
            Toast.makeText(this, "La contraseña debe tener al menos una letra, un número y un carácter especial", Toast.LENGTH_SHORT).show();
            return;
        }

        /*if (!passwordUser.matches("^(?=.*[0-9])" +          // al menos un dígito
                "(?=.*[a-zA-Z])" +       // al menos una letra
                "(?=.*[@#$%^&+=!])" +    // al menos un carácter especial
                "(?=\\S+$)" +            // no debe contener espacios en blanco
                ".{8,}$")) {             // al menos 8 caracteres de longitud
            Toast.makeText(this, "La contraseña debe contener al menos una letra, un número y un carácter especial, y tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }*/

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
        checkUsernameAvailability(name, nameUser, passwordUser, phoneUser, emailUser, usertype, restaurantAssignedUser, codeRestaurantUser);
    }

    private void checkUsernameAvailability(String name, String nameUser, String passwordUser, String phoneUser, String emailUser, String usertype, String restaurantAssignedUser, String codeRestaurantUser) {
        db.collection("user")
                .whereEqualTo("username", nameUser)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // El nombre de usuario está disponible, proceder con el registro
                                try {
                                    registerUser(name, nameUser, passwordUser, phoneUser, emailUser, usertype, restaurantAssignedUser, codeRestaurantUser);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                // El nombre de usuario ya está en uso, mostrar un mensaje de error
                                Toast.makeText(SignIn.this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Error al realizar la consulta
                            Toast.makeText(SignIn.this, "Error al verificar la disponibilidad del nombre de usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser(String name, String nameUser, String passwordUser, String phoneUser, String emailUser, String usertype, String restaurantAssignedUser, String codeRestaurantUser) throws Exception {
        // Cifrar la contraseña antes de almacenarla
        String encryptedPassword = AESUtil.encrypt(passwordUser);
        // Almacenar la contraseña cifrada utilizando KeyStore de Android
        KeyStoreUtil.saveEncryptedPassword(this, encryptedPassword);
        mAuth.createUserWithEmailAndPassword(emailUser, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Map<String, Object> map = new HashMap<>();
                String id = mAuth.getCurrentUser().getUid();
                map.put("id",id);
                map.put("name",name);
                map.put("username",nameUser);
                map.put("password",encryptedPassword);
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
                        //Permiso para hacer llamadas
                        if(ActivityCompat.checkSelfPermission(SignIn.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(SignIn.this, new String[]{
                                    android.Manifest.permission.CALL_PHONE},10);
                            return;
                        }

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
                Toast.makeText(SignIn.this, "Error al registrar usuario, el correo esta en uso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {//Funcion para cuando se selecciona un item
        Toast.makeText(SignIn.this, ""+parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
        selectedItem = parent.getItemAtPosition(position).toString();

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