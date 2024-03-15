package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentMethods extends AppCompatActivity {

    ImageButton paypalDrop, vmDrop, payDrop;
    EditText nameVM, numberCardVM, dateVM, cvvVM, instructionPay;
    Button continuePaymentMethods;
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
        instructionPay = findViewById(R.id.ed_instruction_pay);
        continuePaymentMethods = findViewById(R.id.b_continue_payment_methods);
        layoutPaypal = findViewById(R.id.layout_paypal);
        layoutVM = findViewById(R.id.layout_vm);
        layoutPay = findViewById(R.id.layout_pay);

        paypalDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePaymentMethods = "paypal";
                onClickDropPaymentMethods();
            }
        });

        vmDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePaymentMethods = "vm";
                onClickDropPaymentMethods();
            }
        });

        payDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typePaymentMethods = "pay";
                onClickDropPaymentMethods();
            }
        });

        continuePaymentMethods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAssignedPaymentMethods();
            }
        });
    }

    private void onClickAssignedPaymentMethods() {
        String name = nameVM.getText().toString().trim();
        String numberCard = numberCardVM.getText().toString().trim();
        String date = dateVM.getText().toString().trim();
        String cvv = cvvVM.getText().toString().trim();

        if(name.isEmpty() || numberCard.isEmpty() || date.isEmpty() || cvv.isEmpty()){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!name.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$")) { // Verifica si el nombre contiene caracteres no válidos
            Toast.makeText(this, "El nombre del platillo solo puede contener letras", Toast.LENGTH_SHORT).show();
            return;
        }

        if(date.matches("%d/%d")){
            Toast.makeText(this, "La fecha debe tener el formato número/número", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.length() > 3){
            Toast.makeText(this, "El cvv no debe contener más de tres digitos", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void onClickDropPaymentMethods() {
        if(typePaymentMethods.equals("paypal")){
            layoutPaypal.setVisibility(View.VISIBLE);
        } else {
            layoutPaypal.setVisibility(View.GONE);
        }
        if(typePaymentMethods.equals("vm")){
            layoutVM.setVisibility(View.VISIBLE);
        } else {
            layoutVM.setVisibility(View.GONE);
        }
        if(typePaymentMethods.equals("pay")){
            layoutPay.setVisibility(View.VISIBLE);
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
        return super.onOptionsItemSelected(item);
    }
}