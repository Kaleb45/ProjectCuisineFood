package com.example.proyectocuisinefood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogIn extends AppCompatActivity {
    EditText usuario, password;
    Button ingresar;
    SharedPreferences archivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        usuario=findViewById(R.id.ed_nombre_login);
        password=findViewById(R.id.ed_password_login);
        ingresar=findViewById(R.id.b_inicio_login);

        archivo=this.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        if(archivo.contains("id")){
            Intent inicio = new Intent(this, registro.class);
            startActivity(inicio);
            finish();
        }

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick_login();
            }
        });
    }
}