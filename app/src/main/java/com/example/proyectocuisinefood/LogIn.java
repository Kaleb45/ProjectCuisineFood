package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import SecureStorage.AESUtil;

public class LogIn extends AppCompatActivity {
    EditText usuario, password;
    Button ingresar;
    ToggleButton toggleButtonShowPassword;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        usuario=findViewById(R.id.ed_nombre_login);
        password=findViewById(R.id.ed_password_login);

        ingresar=findViewById(R.id.b_inicio_login);
        toggleButtonShowPassword = findViewById(R.id.toggleButtonShowPassword);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickLogIn();
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

    private void onclickLogIn() {
        String user = usuario.getText().toString().trim();
        String passwordUser = password.getText().toString().trim();

        if (user.isEmpty() || passwordUser.isEmpty()) {
            Toast.makeText(this, "No puede dejar espacios vacíos", Toast.LENGTH_SHORT).show();
        } else {
            // Verificar si el usuario ingresó un correo electrónico o un nombre de usuario
            if (isEmail(user)) {
                // Si el usuario ingresó un correo electrónico, iniciar sesión con correo electrónico
                loginUserWithEmail(user, passwordUser);
            } else {
                // Si el usuario ingresó un nombre de usuario, iniciar sesión con nombre de usuario
                loginUserWithUsername(user, passwordUser);
            }
        }
    }

    // Método para verificar si la cadena ingresada es un correo electrónico
    private boolean isEmail(String text) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    private void loginUserWithEmail(String email, String passwordUser) {
        mAuth.signInWithEmailAndPassword(email, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
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
                                                    startActivity(new Intent(LogIn.this, Admin.class));
                                                    finish();
                                                    break;
                                                case "Cocinero":
                                                    startActivity(new Intent(LogIn.this, Cocinero.class));
                                                    finish();
                                                    break;
                                                case "Mesero":
                                                    startActivity(new Intent(LogIn.this, Mesero.class));
                                                    finish();
                                                    break;
                                                case "Cliente":
                                                    startActivity(new Intent(LogIn.this, Cliente.class));
                                                    finish();
                                                    break;
                                                default:
                                                    // Tipo de usuario no reconocido
                                                    Toast.makeText(LogIn.this, "Tipo de usuario no reconocido", Toast.LENGTH_SHORT).show();
                                                    break;
                                            }
                                        } else {
                                            // Tipo de usuario no especificado en la base de datos
                                            Toast.makeText(LogIn.this, "Tipo de usuario no especificado", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        // Documento de usuario no encontrado en la base de datos
                                        Toast.makeText(LogIn.this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Error al obtener el documento de usuario
                                    Toast.makeText(LogIn.this, "Error al obtener información de usuario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(LogIn.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LogIn.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUserWithUsername(String username, String passwordUser) {
        CollectionReference usersRef = db.collection("user");

        // Realizar una consulta para buscar un documento que tenga el nombre de usuario proporcionado
        usersRef.whereEqualTo("username", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Documento encontrado con el nombre de usuario proporcionado
                                // Obtener el correo electrónico asociado al nombre de usuario
                                String email = document.getString("email");
                                if (email != null) {
                                    // Realizar inicio de sesión con el correo electrónico y la contraseña proporcionados
                                    mAuth.signInWithEmailAndPassword(email, passwordUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // Inicio de sesión exitoso
                                                        FirebaseUser currentUser = mAuth.getCurrentUser();
                                                        if (currentUser != null) {
                                                            // Obtener el tipo de usuario
                                                            String userType = document.getString("usertype");
                                                            if (userType != null) {
                                                                switch (userType) {
                                                                    case "Administrador":
                                                                        startActivity(new Intent(LogIn.this, Admin.class));
                                                                        finish();
                                                                        break;
                                                                    case "Cocinero":
                                                                        startActivity(new Intent(LogIn.this, Cocinero.class));
                                                                        finish();
                                                                        break;
                                                                    case "Mesero":
                                                                        startActivity(new Intent(LogIn.this, Mesero.class));
                                                                        finish();
                                                                        break;
                                                                    case "Cliente":
                                                                        startActivity(new Intent(LogIn.this, Cliente.class));
                                                                        finish();
                                                                        break;
                                                                    default:
                                                                        // Tipo de usuario no reconocido
                                                                        Toast.makeText(LogIn.this, "Tipo de usuario no reconocido", Toast.LENGTH_SHORT).show();
                                                                        break;
                                                                }
                                                            } else {
                                                                // Tipo de usuario no especificado en la base de datos
                                                                Toast.makeText(LogIn.this, "Tipo de usuario no especificado", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    } else {
                                                        // Error al iniciar sesión
                                                        Toast.makeText(LogIn.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Correo electrónico no encontrado en la base de datos
                                    Toast.makeText(LogIn.this, "Correo electrónico no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                                }
                            }

                            Toast.makeText(LogIn.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                        } else {
                            // Error al realizar la consulta
                            Toast.makeText(LogIn.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
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
                                        startActivity(new Intent(LogIn.this, Admin.class));
                                        finish();
                                        return;
                                    case "Cocinero":
                                        startActivity(new Intent(LogIn.this, Cocinero.class));
                                        finish();
                                        return;
                                    case "Mesero":
                                        startActivity(new Intent(LogIn.this, Mesero.class));
                                        finish();
                                        return;
                                    case "Cliente":
                                        startActivity(new Intent(LogIn.this, Cliente.class));
                                        finish();
                                        return;
                                    default:
                                        // Tipo de usuario no reconocido
                                        Toast.makeText(LogIn.this, "Tipo de usuario no reconocido", Toast.LENGTH_SHORT).show();
                                        return;
                                }
                            } else {
                                // Tipo de usuario no especificado en la base de datos
                                Toast.makeText(LogIn.this, "Tipo de usuario no especificado", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Documento de usuario no encontrado en la base de datos
                            Toast.makeText(LogIn.this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error al obtener el documento de usuario
                        Toast.makeText(LogIn.this, "Error al obtener información de usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    */
}