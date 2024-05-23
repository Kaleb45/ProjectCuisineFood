package com.example.proyectocuisinefood.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.icu.math.BigDecimal;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.auxiliaryclass.CuisineFood;
import com.example.proyectocuisinefood.notification.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.payclip.authentication.client.LogoutListener;
import com.payclip.common.StatusCode;
import com.payclip.payments.models.transaction.ClipTransaction;
import com.payclip.payments.services.responses.RemotePaymentInfo;
import com.payclip.paymentui.client.ClipApi;
import com.payclip.paymentui.client.LoginListener;
import com.payclip.paymentui.models.ClipPayment;


import org.jetbrains.annotations.NotNull;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import SecureStorage.AESUtil;
import SecureStorage.KeyStoreUtil;

public class PaymentMethods extends AppCompatActivity implements LoginListener, LogoutListener {

    ImageButton paypalDrop, vmDrop, payDrop;

    TextView instructionPayTV;
    EditText nameVM, numberCardVM, dateVM, cvvVM, instructionPay;
    Button continuePaymentMethods;
    ToggleButton toggleButtonShowNumberCard, toggleButtonShowDate, toggleButtonShowCVV;
    LinearLayout layoutPaypal, layoutVM, layoutPay;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId, typePaymentMethods, userType, price, paymentMethodId, nameCustomer, clipEmail, clipPassword, clipPlus;

    public static final int REQUEST_CODE_PAYMENT_RESULT = 1234;
    private static final int REQUEST_CODE_REMOTE_PAYMENT_RESULT = 9876;
    private static final int REQUEST_CODE_SETTINGS_RESULT = 3245;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        instructionPayTV = findViewById(R.id.t_instruction_pay);
        continuePaymentMethods = findViewById(R.id.b_continue_payment_methods);
        layoutPaypal = findViewById(R.id.layout_paypal);
        layoutVM = findViewById(R.id.layout_vm);
        layoutPay = findViewById(R.id.layout_pay);

        // Recoge el ID del restaurante del Intent
        restaurantId = getIntent().getStringExtra("restaurantId");
        price = getIntent().getStringExtra("totalPrice");

        if(restaurantId != null && !restaurantId.isEmpty() && price != null && !price.isEmpty()){
            getPaymentMethodsCustomer();
            getClip();
            continuePaymentMethods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickAssignedPaymentMethods();
                }
            });
        } else if(restaurantId == null && restaurantId.isEmpty() && price != null && !price.isEmpty()){
            getPaymentMethodsCustomer();
            getClip();
            continuePaymentMethods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickAssignedPaymentMethods();
                }
            });
        } else if(restaurantId == null && restaurantId.isEmpty()) {
            continuePaymentMethods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickAssignedPaymentMethods();
                }
            });
        } else if(restaurantId != null && price == null){
            getPaymentMethodsAdmin(restaurantId);
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
                if(userType.equals("Administrador")){
                    Intent intent = new Intent(PaymentMethods.this, Admin.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(PaymentMethods.this, Cliente.class);
                    startActivity(intent);
                    finish();
                }
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

        getUserType();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(userType.equals("Administrador")){
            Intent intent = new Intent(PaymentMethods.this, Admin.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(PaymentMethods.this, Cliente.class);
            startActivity(intent);
            finish();
        }
    }

    private void getUserType(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("user").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userType = documentSnapshot.getString("usertype");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PaymentMethods.this, "Error al obtener el tipo de usuario", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
            Toast.makeText(this, "La fecha debe tener el formato mes/día", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que la fecha sea válida
        if (!isValidDate(date)) {
            Toast.makeText(this, "La fecha ingresada no es válida", Toast.LENGTH_SHORT).show();
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


        //Toast.makeText(PaymentMethods.this, ""+paymentMethodId, Toast.LENGTH_SHORT).show();
        if(userType.equals("Administrador")){
            assignedPaymentMethodsRestaurant(name, formattedNumberCard, date, cvv);
        } else {
            if(paymentMethodId != null){
                assignedPaymentMethodsCustomer(name, formattedNumberCard, date, cvv);
            } else {
                createPaymentMethodsRestaurant(name, formattedNumberCard, date, cvv);
            }
        }
    }

    // Método para validar si la fecha es válida
    private boolean isValidDate(String date) {
        try {
            String[] parts = date.split("/");
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            // Validar que el mes esté entre 1 y 12 y que el día esté entre 1 y 31
            return (month >= 1 && month <= 12) && (day >= 1 && day <= 31);
        } catch (NumberFormatException e) {
            return false;
        }
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

    private void createPaymentMethodsRestaurant(String name, String numberCard, String date, String cvv) {
        Key secretKey;
        try {
            secretKey = AESUtil.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            name = AESUtil.encrypt(name, secretKey);
            numberCard = AESUtil.encrypt(numberCard, secretKey);
            date = AESUtil.encrypt(date, secretKey);
            cvv = AESUtil.encrypt(cvv, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String keyString = AESUtil.keyToString(secretKey);
        KeyStoreUtil.saveEncryptedPassword(this, keyString);

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("cardNumber", numberCard);
        map.put("date", date);
        map.put("cvv", cvv);
        map.put("type", typePaymentMethods);
        map.put("key", keyString);
        map.put("emailClip", null);
        map.put("passwordClip", null);
        map.put("clipPlus", null);

        db.collection("paymentMethods").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                paymentMethodId = documentReference.getId();

                // Actualizar las órdenes del usuario con el ID del método de pago
                configPayWithCard(paymentMethodId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentMethods.this, "Error al asignar las credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configPayWithCard (String id){
        // Crear un cuadro de diálogo con tres opciones
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(R.layout.dialog_configure_pay_with_car);
        builder.setTitle("Pago con Tarjeta");

        // Mostrar el cuadro de diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

        // Inicializar vistas del layout personalizado
        RadioGroup radioGroup = dialog.findViewById(R.id.radio_group);
        LinearLayout clipLogIn = dialog.findViewById(R.id.l_log_in_clip);

        TextView clipPlusText = dialog.findViewById(R.id.t_clip_plus);
        EditText emailClip = dialog.findViewById(R.id.e_clip_email);
        EditText passwordClip = dialog.findViewById(R.id.e_clip_password);
        ToggleButton passwordClipShow = dialog.findViewById(R.id.tb_clip_password);
        RadioButton option1 = dialog.findViewById(R.id.r_option_1);
        RadioButton option2 = dialog.findViewById(R.id.r_option_2);
        RadioButton option3 = dialog.findViewById(R.id.r_option_3);
        Switch clipPlusSwitch = dialog.findViewById(R.id.bs_clip_plus);
        Button accept = dialog.findViewById(R.id.b_accept);

        if(userType.equals("Administrador")){

            if(clipEmail != null || clipPassword != null){
                radioGroup.setVisibility(View.VISIBLE);
                clipPlusSwitch.setVisibility(View.VISIBLE);
                clipPlusText.setVisibility(View.VISIBLE);
                clipLogIn.setVisibility(View.GONE);
                option1.setVisibility(View.GONE);
                option2.setVisibility(View.GONE);

                

                if(clipPlus.equals("Si")){
                    clipPlusSwitch.setChecked(true);
                } else {
                    clipPlusSwitch.setChecked(false);
                }

                clipPlusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked){
                            clipPlus = "Si";
                            setClipPlus();
                        } else {
                            clipPlus = "No";
                            setClipPlus();
                        }
                    }
                });

                // Manejar la selección de opciones
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Reiniciar la selección de botones de opción
                        option3.setChecked(false);

                        // Marcar el botón de opción seleccionado
                        RadioButton selectedRadioButton = group.findViewById(checkedId);
                        selectedRadioButton.setChecked(true);
                        String selectedType = selectedRadioButton.getText().toString();
                        //Toast.makeText(PaymentMethods.this, selectedText, Toast.LENGTH_SHORT).show();

                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                initClipPaymentMethods(id, selectedType);
                                dialog.dismiss();
                            }
                        });
                    }
                });

            } else {
                radioGroup.setVisibility(View.GONE);
                clipLogIn.setVisibility(View.VISIBLE);
                clipPlusSwitch.setVisibility(View.GONE);
                clipPlusText.setVisibility(View.GONE);
                passwordClipShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // Mostrar contraseña
                            passwordClip.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            // Ocultar contraseña
                            passwordClip.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        // Mover el cursor al final del texto
                        passwordClip.setSelection(passwordClip.getText().length());
                    }
                });

                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickLogInClip(emailClip, passwordClip);
                        dialog.dismiss();
                    }
                });
            }

        } else {
            radioGroup.setVisibility(View.VISIBLE);
            clipLogIn.setVisibility(View.GONE);
            option3.setVisibility(View.GONE);
            clipPlusSwitch.setVisibility(View.GONE);
            clipPlusText.setVisibility(View.GONE);

            if(clipPlus.equals("Si")){
                option1.setVisibility(View.VISIBLE);
            } else {
                option1.setVisibility(View.GONE);
            }

            // Manejar la selección de opciones
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // Reiniciar la selección de botones de opción
                    option1.setChecked(false);
                    option2.setChecked(false);

                    // Marcar el botón de opción seleccionado
                    RadioButton selectedRadioButton = group.findViewById(checkedId);
                    selectedRadioButton.setChecked(true);
                    String selectedType = selectedRadioButton.getText().toString();
                    //Toast.makeText(PaymentMethods.this, selectedText, Toast.LENGTH_SHORT).show();

                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            initClipPaymentMethods(id, selectedType);
                            dialog.dismiss();
                        }
                    });
                }
            });
        }

    }

    private void onClickLogInClip(EditText emailClip, EditText passwordClip) {
        String email = emailClip.getText().toString().trim();
        String password = passwordClip.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "No puede haber espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipApi.login(email, password, new LoginListener() {
            @Override
            public void onLoginSuccess() {
                Toast.makeText(PaymentMethods.this, "Inicio de Sesión exitoso", Toast.LENGTH_SHORT).show();
                logInClip(email, password);
            }

            @Override
            public void onLoginFailed(@NonNull StatusCode.ClipError clipError) {
                Toast.makeText(PaymentMethods.this, "Inicio de Sesión fallido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logInClip(String email, String password) {
        String finalEmail = email;
        String finalPassword = password;

        Key secretKey;
        try {
            secretKey = AESUtil.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            email = AESUtil.encrypt(email, secretKey);
            password = AESUtil.encrypt(password, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String keyString = AESUtil.keyToString(secretKey);

        Map<String, Object> map = new HashMap<>();
        map.put("emailClip", email);
        map.put("passwordClip", password);
        map.put("keyClip", keyString);

        db.collection("paymentMethods").document(paymentMethodId).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                String content = "Ahora cuando seleccione en continuar puede configurar su Clip Plus";
                showStatusDialog("Aviso", content);
                clipEmail = finalEmail;
                clipPassword = finalPassword;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentMethods.this, "Error al asignar las credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getClip() {

        db.collection("restaurant").document(restaurantId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String paymentMethodIdRestaurant = documentSnapshot.getString("paymentMethodId");

                db.collection("paymentMethods").document(paymentMethodIdRestaurant).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String email = documentSnapshot.getString("emailClip");
                        String password = documentSnapshot.getString("passwordClip");
                        String keyString = documentSnapshot.getString("keyClip");

                        Key key = null;
                        if(keyString!= null){
                            key = AESUtil.stringToKey(keyString);
                        }


                        try {
                            email = AESUtil.decrypt(email, key);
                            password = AESUtil.decrypt(password, key);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        clipPlus = documentSnapshot.getString("clipPlus");
                        clipEmail = email;
                        clipPassword = password;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentMethods.this, "Error al obtener la información", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentMethods.this, "Error al obtener la información", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClipPlus(){
        Map<String, Object> map = new HashMap<>();
        map.put("clipPlus", clipPlus);
        db.collection("paymentMethods").document(paymentMethodId).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                String content = "Ahora los usuarios podrán pagar por medio del Clip Plus";
                showStatusDialog("Aviso", content);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentMethods.this, "Error al asignar las credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initClipPaymentMethods(String id, String selectedType) {
        BigDecimal amount = null;
        if(price != null){
            amount = BigDecimal.valueOf(Double.parseDouble(price));
        }

        // Crear un CountDownTimer que espere 5 segundos
        BigDecimal finalAmount = amount;

        ClipApi.login(clipEmail, clipPassword, PaymentMethods.this);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                switch (selectedType) {
                    case "Pago con Clip":
                        Toast.makeText(PaymentMethods.this, "Pago con Clip", Toast.LENGTH_SHORT).show();

                        ClipPayment clipPayment = new ClipPayment.Builder() // Construir el método de pago
                                .amount(finalAmount.toBigDecimal()) // Cantidad de la transacción
                                .enableContactless(true) // Tecnología sin contacto
                                .customTransactionId(id) // Añadirle un identificador a la transacción
                                .enableTips(true) // Permite mostrar la pantalla de selección de propina en el flujo de pago
                                .roundTips(true) // Permite redondear los decimales de la cantidad de la propina
                                .build();

                        notificationPay("Mesero");

                        ClipApi.launchPaymentActivity(PaymentMethods.this, clipPayment, REQUEST_CODE_PAYMENT_RESULT);
                        break;
                    case "Pago a distancia con Clip":
                        Toast.makeText(PaymentMethods.this, "Pago a distancia con Clip", Toast.LENGTH_SHORT).show();

                        ClipApi.launchRemotePaymentActivity(PaymentMethods.this, finalAmount.toBigDecimal(), REQUEST_CODE_REMOTE_PAYMENT_RESULT);

                        break;
                    case "Configurar Clip Plus":
                        Toast.makeText(PaymentMethods.this, "Configurar Clip Plus", Toast.LENGTH_SHORT).show();
                        ClipApi.showSettingsActivity(PaymentMethods.this, true, true, REQUEST_CODE_SETTINGS_RESULT);
                        break;
                }
            }
        }.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        String content;
        switch (requestCode) {
            case REQUEST_CODE_PAYMENT_RESULT:
                assert data != null;
                switch (data.getIntExtra(StatusCode.RESULT_CODE, StatusCode.FAILURE)) {
                    case StatusCode.SUCCESSFUL:
                        ClipTransaction transactionResult = data.getParcelableExtra(StatusCode.RESULT_PAYMENT_DATA);

                        //content = "" + transactionResult;
                        content = "Su compra de ordenes fue exitosa: \nTotal comprado: "+price+"$"+"\nA nombre de: "+nameCustomer;
                        showStatusDialog("La transacción fue exitoso: ", content);
                        updateOrdersWithPaymentMethod(paymentMethodId);
                        ClipApi.logout(this);
                        break;
                    case StatusCode.FAILURE:
                        int errorCode = data.getIntExtra(StatusCode.RESULT_ERROR, -1);
                        String errorCodeDesc = data.getStringExtra(StatusCode.RESULT_ERROR_DESC);
                        String messageError = data.getStringExtra(StatusCode.RESULT_ERROR_MESSAGE);

                        String _messageError = "";
                        if (messageError != null && !messageError.equals("")) {
                            _messageError = messageError;
                        }

                        //content = "Error code: " + errorCode + "\nError description: " + errorCodeDesc + _messageError;
                        content = "No fue posible realizar la transacción";

                        showStatusDialog("A ocurrido un error ", content);
                        ClipApi.logout(this);
                        break;
                }
                break;
            case REQUEST_CODE_REMOTE_PAYMENT_RESULT:
                assert data != null;
                switch (data.getIntExtra(StatusCode.RESULT_CODE, StatusCode.FAILURE)) {
                    case StatusCode.SUCCESSFUL:
                        RemotePaymentInfo remotePaymentResult = data.getParcelableExtra(StatusCode.RESULT_REMOTE_PAYMENT_DATA);

                        //content = "" + remotePaymentResult;
                        content = "Su compra de ordenes fue exitosa: \nTotal comprado: "+price+"$"+"\nA nombre de: "+nameCustomer;
                        showStatusDialog("El pago a distancia fue exitoso:", content);
                        updateOrdersWithPaymentMethod(paymentMethodId);
                        ClipApi.logout(this);
                        break;
                    case StatusCode.FAILURE:
                        int errorCode = data.getIntExtra(StatusCode.RESULT_ERROR, -1);
                        String errorCodeDesc = data.getStringExtra(StatusCode.RESULT_ERROR_DESC);
                        String messageError = data.getStringExtra(StatusCode.RESULT_ERROR_MESSAGE);

                        String _messageError = "";
                        if (messageError != null && !messageError.equals("")) {
                            _messageError = messageError;
                        }

                        //content = "Error code: " + errorCode + "\nError description: " + errorCodeDesc + _messageError;
                        content = "No fue posible realizar la el pago a distancia";

                        showStatusDialog("A ocurrido un error ", content);
                        ClipApi.logout(this);
                        break;
                }
                break;
            case REQUEST_CODE_SETTINGS_RESULT:
                Toast.makeText(this, "Configuración terminada", Toast.LENGTH_SHORT).show();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showStatusDialog(String title, String content) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("OK", (dialogInterface, i) -> { })
                .show();
    }

    private void updateOrdersWithPaymentMethod(String id) {
        db.collection("orders").whereEqualTo("userId", mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String orderId = documentSnapshot.getId();
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("paymentMethodId", id);

                    db.collection("orders").document(orderId).update(updateMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(PaymentMethods.this, "Ordenes pagadas", Toast.LENGTH_SHORT).show();
                                    notificationPay("Pagado");
                                    Intent intent = new Intent(PaymentMethods.this, MenuRestaurant.class);
                                    intent.putExtra("restaurantId",restaurantId);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PaymentMethods.this, "Error al actualizar las órdenes", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentMethods.this, "Error al obtener las órdenes del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notificationPay(String condition) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(MyFirebaseMessagingService.TAG_NOTIFICATION, "Error al obtener el token de registro de FCM", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d(MyFirebaseMessagingService.TAG_NOTIFICATION, token);
                        //Toast.makeText(PlaceOrders.this, msg, Toast.LENGTH_SHORT).show();

                        if(condition.equals("Pagado")){
                            MyFirebaseMessagingService.sendNotificationDevice("El usuario "+nameCustomer+" pago mediante Clip", "Ordenes", CuisineFood.token2, PaymentMethods.this);
                            MyFirebaseMessagingService.sendNotification("Se ha realizado el cobro por medio de Clip", "Ordenes", token, PaymentMethods.this, Cliente.class);
                        } else {
                            MyFirebaseMessagingService.sendNotificationDevice("El usuario "+nameCustomer+" solicita un pago mediante Clip", "Ordenes", CuisineFood.token2, PaymentMethods.this);
                            MyFirebaseMessagingService.sendNotification("Espere a que un mesero llegue a su mesa", "Ordenes", token, PaymentMethods.this, Mesero.class);
                        }

                    }
                });
    }

    private void assignedPaymentMethodsCustomer(String name, String numberCard, String date, String cvv){
        Key secretKey;
        try {
            secretKey = AESUtil.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            name = AESUtil.encrypt(name, secretKey);
            numberCard = AESUtil.encrypt(numberCard, secretKey);
            date = AESUtil.encrypt(date, secretKey);
            cvv = AESUtil.encrypt(cvv, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String keyString = AESUtil.keyToString(secretKey);
        KeyStoreUtil.saveEncryptedPassword(this, keyString);

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("cardNumber", numberCard);
        map.put("date", date);
        map.put("cvv", cvv);
        map.put("type", typePaymentMethods);
        map.put("key", keyString);
        db.collection("paymentMethods").document(paymentMethodId).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                configPayWithCard(paymentMethodId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentMethods.this, "Error al asignar las credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignedPaymentMethodsRestaurant(String name, String numberCard, String date, String cvv) {
        Key secretKey;
        try {
            secretKey = AESUtil.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            name = AESUtil.encrypt(name, secretKey);
            numberCard = AESUtil.encrypt(numberCard, secretKey);
            date = AESUtil.encrypt(date, secretKey);
            cvv = AESUtil.encrypt(cvv, secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String keyString = AESUtil.keyToString(secretKey);
        KeyStoreUtil.saveEncryptedPassword(this, keyString);

        // Consultar la colección "restaurant" para obtener el ID de paymentMethods
        String finalName = name;
        String finalNumberCard = numberCard;
        String finalDate = date;
        String finalCvv = cvv;
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
                                paymentMethodId = paymentMethodsId;

                                Map<String, Object> map = new HashMap<>();
                                map.put("name", finalName);
                                map.put("cardNumber", finalNumberCard);
                                map.put("date", finalDate);
                                map.put("cvv", finalCvv);
                                map.put("type", typePaymentMethods);
                                map.put("key", keyString);

                                db.collection("paymentMethods").document(paymentMethodsId)
                                        .update(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(PaymentMethods.this, "Asignación Exitosa", Toast.LENGTH_SHORT).show();
                                                configPayWithCard(paymentMethodId);
                                                /*Intent intent = new Intent(PaymentMethods.this, Admin.class);
                                                startActivity(intent);
                                                finish();*/
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

    private void getPaymentMethodsCustomer () {
        db.collection("orders").whereEqualTo("userId",mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String paymentMethodIds = documentSnapshot.getString("paymentMethodId");
                    //Toast.makeText(PaymentMethods.this, ""+paymentMethodId, Toast.LENGTH_SHORT).show();

                    if (paymentMethodIds != null) {
                        paymentMethodId = paymentMethodIds;
                        restaurantId = documentSnapshot.getString("restaurantId");
                        // Consultar el documento correspondiente en la colección "paymentMethods"
                        db.collection("paymentMethods")
                                .document(paymentMethodId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String name = documentSnapshot.getString("name");
                                            String numberCard = documentSnapshot.getString("cardNumber");
                                            String date = documentSnapshot.getString("date");
                                            String cvv = documentSnapshot.getString("cvv");
                                            String keyString = documentSnapshot.getString("key");
                                            typePaymentMethods = documentSnapshot.getString("type");

                                            Key key = null;
                                            if(keyString!= null){
                                                key = AESUtil.stringToKey(keyString);
                                                try {
                                                    name = AESUtil.decrypt(name, key);
                                                    numberCard = AESUtil.decrypt(numberCard, key);
                                                    date = AESUtil.decrypt(date, key);
                                                    cvv = AESUtil.decrypt(cvv, key);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }

                                            nameCustomer = name;

                                            nameVM.setText(nameCustomer);
                                            numberCardVM.setText(numberCard);
                                            dateVM.setText(date);
                                            cvvVM.setText(cvv);
                                        } else {
                                            Toast.makeText(PaymentMethods.this, "No se encontraron métodos de pago asociados al usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PaymentMethods.this, "Error al obtener los datos de los métodos de pago", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PaymentMethods.this, "Error al obtener las credenciales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPaymentMethodsAdmin(String restaurantId) {
        // Consultar la colección "restaurant" para obtener el ID de paymentMethods
        db.collection("restaurant")
                .document(restaurantId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            paymentMethodId = documentSnapshot.getString("paymentMethodId");

                            if (paymentMethodId != null) {
                                // Consultar el documento correspondiente en la colección "paymentMethods"
                                db.collection("paymentMethods")
                                        .document(paymentMethodId)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    String name = documentSnapshot.getString("name");
                                                    String numberCard = documentSnapshot.getString("cardNumber");
                                                    String date = documentSnapshot.getString("date");
                                                    String cvv = documentSnapshot.getString("cvv");
                                                    String email = documentSnapshot.getString("emailClip");
                                                    String password = documentSnapshot.getString("passwordClip");
                                                    String keyString = documentSnapshot.getString("key");
                                                    String keyClipString = documentSnapshot.getString("keyClip");

                                                    Key key = null;
                                                    if(keyString!= null){
                                                        key = AESUtil.stringToKey(keyString);
                                                        try {
                                                            name = AESUtil.decrypt(name, key);
                                                            numberCard = AESUtil.decrypt(numberCard, key);
                                                            date = AESUtil.decrypt(date, key);
                                                            cvv = AESUtil.decrypt(cvv, key);
                                                        } catch (Exception e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }

                                                    Key keyClip = null;
                                                    if(keyClipString!= null){
                                                        keyClip = AESUtil.stringToKey(keyClipString);
                                                        try {
                                                            email = AESUtil.decrypt(email, keyClip);
                                                            password = AESUtil.decrypt(password, keyClip);
                                                        } catch (Exception e) {
                                                            throw new RuntimeException(e);
                                                        }

                                                        clipEmail = email;
                                                        clipPassword = password;
                                                    }



                                                    typePaymentMethods = documentSnapshot.getString("type");
                                                    clipPlus = documentSnapshot.getString("clipPlus");

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
    public void onLoginSuccess() {
        Toast.makeText(this, "Inicio de Sesión exitoso", Toast.LENGTH_SHORT).show();
        //Snackbar.make(btnPayment, "You have successfully logged in", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoginFailed(@NotNull StatusCode.ClipError statusCode) {
        Toast.makeText(this, "Inicio de Sesión fallido", Toast.LENGTH_SHORT).show();
        //Snackbar.make(btnPayment, "There was an error logging in: " + statusCode + " -- " + statusCode.getFullName(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutSuccess() {
        Toast.makeText(this, "Cerrar sesión exitoso", Toast.LENGTH_SHORT).show();
        //Snackbar.make(btnPayment, "You have successfully logged out", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutError(@NotNull StatusCode.ClipError errorCode) {
        Toast.makeText(this, "Cerrar sesión fallido", Toast.LENGTH_SHORT).show();
        //Snackbar.make(btnPayment, "There was an error logging in: " + errorCode + " -- " + errorCode.getFullName(), Snackbar.LENGTH_LONG).show();
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