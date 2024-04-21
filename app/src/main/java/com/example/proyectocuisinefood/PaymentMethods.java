package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class PaymentMethods extends AppCompatActivity {

    ImageButton paypalDrop, vmDrop, payDrop;
    EditText nameVM, numberCardVM, dateVM, cvvVM, instructionPay;
    Button continuePaymentMethods;
    ToggleButton toggleButtonShowNumberCard, toggleButtonShowDate, toggleButtonShowCVV;
    LinearLayout layoutPaypal, layoutVM, layoutPay;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId, typePaymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Recoge el ID del restaurante del Intent
        restaurantId = getIntent().getStringExtra("restaurantId");

        paypalDrop = findViewById(R.id.imb_paypal_payment_methods);
        vmDrop = findViewById(R.id.imb_vm_payment_methods);
        payDrop = findViewById(R.id.imb_pay_payment_methods);
        nameVM = findViewById(R.id.ed_name_payment_methods);
        numberCardVM = findViewById(R.id.ed_number_payment_methods);
        dateVM = findViewById(R.id.ed_date_payment_methods);
        cvvVM = findViewById(R.id.ed_cvv_payment_methods);
        toggleButtonShowNumberCard = findViewById(R.id.tb_number_card);
        toggleButtonShowDate = findViewById(R.id.tb_date);
        toggleButtonShowCVV = findViewById(R.id.tb_cvv);
        instructionPay = findViewById(R.id.ed_instruction_pay);
        continuePaymentMethods = findViewById(R.id.b_continue_payment_methods);
        layoutPaypal = findViewById(R.id.layout_paypal);
        layoutVM = findViewById(R.id.layout_vm);
        layoutPay = findViewById(R.id.layout_pay);

        if(restaurantId == null || restaurantId.isEmpty()){
            continuePaymentMethods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickAssignedPaymentMethods();
                }
            });
        } else {
            getPaymentMethods(restaurantId);
            continuePaymentMethods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickAssignedPaymentMethods();
                }
            });
        }

        toggleButtonShowNumberCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mostrar contraseña
                    numberCardVM.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    // Ocultar contraseña
                    numberCardVM.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                }
                // Mover el cursor al final del texto
                numberCardVM.setSelection(numberCardVM.getText().length());
            }
        });

        toggleButtonShowDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mostrar contraseña
                    dateVM.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    // Ocultar contraseña
                    dateVM.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                }
                // Mover el cursor al final del texto
                dateVM.setSelection(dateVM.getText().length());
            }
        });

        toggleButtonShowCVV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Mostrar contraseña
                    cvvVM.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    // Ocultar contraseña
                    cvvVM.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                }
                // Mover el cursor al final del texto
                cvvVM.setSelection(cvvVM.getText().length());
            }
        });

        paypalDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePaymentMethods = "PayPal";
                onClickDropPaymentMethods();
            }
        });

        vmDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePaymentMethods = "Visa/Mastercard";
                onClickDropPaymentMethods();
            }
        });

        payDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePaymentMethods = "Pago Efectivo";
                onClickDropPaymentMethods();
            }
        });

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentMethods.this, Admin.class);
                startActivity(intent);
                finish();
            }
        });

        dateVM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario implementar nada aquí
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario implementar nada aquí
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.length() == 3 && text.charAt(2) != '/') {
                    // Si se ingresaron dos números y no se agregó el "/", lo agregamos automáticamente
                    text = text.substring(0, 2) + "/" + text.substring(2);
                    dateVM.setText(text);
                    dateVM.setSelection(text.length());
                } else if (text.length() == 6 && text.charAt(5) != '/') {
                    // Si se ingresaron cinco números y no se agregó el "/", lo agregamos automáticamente
                    text = text.substring(0, 5) + "/" + text.substring(5);
                    dateVM.setText(text);
                    dateVM.setSelection(text.length());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PaymentMethods.this, Admin.class);
        startActivity(intent);
        finish();
    }

    private void onClickAssignedPaymentMethods() {
        String name = nameVM.getText().toString().trim();
        String numberCard = numberCardVM.getText().toString().trim().replaceAll("\\s+", ""); // Eliminar espacios en blanco
        String formattedNumberCard = formatCardNumber(numberCard); // Formatear número de tarjeta
        String date = dateVM.getText().toString().trim();
        String cvv = cvvVM.getText().toString().trim();

        if(name.isEmpty() || numberCard.isEmpty() || date.isEmpty() || cvv.isEmpty()){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!name.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$")) { // Verifica si el nombre contiene caracteres no válidos
            Toast.makeText(this, "El nombre del propietario solo puede contener letras", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!date.matches("\\d{2}/\\d{2}")) { // Verifica si la fecha tiene el formato correcto
            Toast.makeText(this, "La fecha debe tener el formato número/número", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.length() != 3){
            Toast.makeText(this, "El cvv debe contener exactamente tres dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(formattedNumberCard.contains(" ")){
            if (formattedNumberCard.length() > 20) { // Verifica si el número de tarjeta tiene más de 18 caracteres mas los espacios
                Toast.makeText(this, "El número de tarjeta no puede tener más de 18 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (formattedNumberCard.length() > 18) { // Verifica si el número de tarjeta tiene más de 18 caracteres
                Toast.makeText(this, "El número de tarjeta no puede tener más de 18 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        assignedPaymentMethodsRestaurant(name, formattedNumberCard, date, cvv);
    }

    // Método para formatear el número de tarjeta agregando un espacio cada 4 dígitos
    private String formatCardNumber(String numberCard) {
        StringBuilder formattedNumber = new StringBuilder();
        int count = 0;
        for (char digit : numberCard.toCharArray()) {
            if (count == 4) {
                formattedNumber.append(" "); // Agregar espacio cada 4 dígitos
                count = 0; // Reiniciar contador
            }
            formattedNumber.append(digit);
            count++;
        }
        return formattedNumber.toString();
    }

    private void assignedPaymentMethodsRestaurant(String name, String numberCard, String date, String cvv) {
        // Consultar la colección "restaurant" para obtener el ID de paymentMethods
        db.collection("restaurant")
                .document(restaurantId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String paymentMethodsId = documentSnapshot.getString("paymentMethodId");

                            // Actualizar el documento correspondiente en la colección "paymentMethods"
                            if (paymentMethodsId != null) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", name);
                                map.put("cardNumber", numberCard);
                                map.put("date", date);
                                map.put("cvv", cvv);
                                map.put("type", typePaymentMethods);

                                db.collection("paymentMethods").document(paymentMethodsId)
                                        .update(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PaymentMethods.this, "Asignación Exitosa", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(PaymentMethods.this, Admin.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PaymentMethods.this, "Error al asignar las credenciales", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(PaymentMethods.this, "No se encontró un ID de métodos de pago asociado al restaurante", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PaymentMethods.this, "No se encontró el restaurante", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentMethods.this, "Error al consultar el restaurante", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getPaymentMethods(String restaurantId) {
        // Consultar la colección "restaurant" para obtener el ID de paymentMethods
        db.collection("restaurant")
                .document(restaurantId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String paymentMethodsId = documentSnapshot.getString("paymentMethodId");

                            if (paymentMethodsId != null) {
                                // Consultar el documento correspondiente en la colección "paymentMethods"
                                db.collection("paymentMethods")
                                        .document(paymentMethodsId)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    String name = documentSnapshot.getString("name");
                                                    String numberCard = documentSnapshot.getString("cardNumber");
                                                    String date = documentSnapshot.getString("date");
                                                    String cvv = documentSnapshot.getString("cvv");
                                                    typePaymentMethods = documentSnapshot.getString("type");

                                                    nameVM.setText(name);
                                                    numberCardVM.setText(numberCard);
                                                    dateVM.setText(date);
                                                    cvvVM.setText(cvv);
                                                } else {
                                                    Toast.makeText(PaymentMethods.this, "No se encontraron métodos de pago asociados al restaurante", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PaymentMethods.this, "Error al obtener los datos de los métodos de pago", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(PaymentMethods.this, "El restaurante no tiene métodos de pago asignados", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PaymentMethods.this, "No se encontró el restaurante", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentMethods.this, "Error al consultar el restaurante", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onClickDropPaymentMethods() {
        if(typePaymentMethods.equals("PayPal")){
            layoutPaypal.setVisibility(View.GONE);
            Toast.makeText(this, "Método de pago no disponible actualmente", Toast.LENGTH_SHORT).show();
        } else {
            layoutPaypal.setVisibility(View.GONE);
        }
        if(typePaymentMethods.equals("Visa/Mastercard")){
            layoutVM.setVisibility(View.VISIBLE);
        } else {
            layoutVM.setVisibility(View.GONE);
        }
        if(typePaymentMethods.equals("Pago Efectivo")){
            layoutPay.setVisibility(View.GONE);
            Toast.makeText(this, "Método de pago no disponible actualmente", Toast.LENGTH_SHORT).show();
        } else {
            layoutPay.setVisibility(View.GONE);
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
            startActivity(new Intent(PaymentMethods.this, MainActivity.class));
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