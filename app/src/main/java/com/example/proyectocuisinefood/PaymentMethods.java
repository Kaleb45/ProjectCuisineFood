package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class PaymentMethods extends AppCompatActivity {

    ImageButton paypalDrop, vmDrop, payDrop;
    EditText nameVM, numberCardVM, dateVM, cvvVM, instructionPay;
    Button continuePaymentMethods;
    LinearLayout layoutPaypal, layoutVM, layoutPay;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String restaurantId;

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


        payDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDropPaymentMethods();
            }
        });

        vmDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDropPaymentMethods();
            }
        });

        payDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDropPaymentMethods();
            }
        });
    }

    private void onClickDropPaymentMethods() {

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