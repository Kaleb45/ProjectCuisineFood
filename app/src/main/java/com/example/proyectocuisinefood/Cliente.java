package com.example.proyectocuisinefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Cliente extends AppCompatActivity {

    QrScanFragment qrScanFragment = new QrScanFragment();
    FindRestaurantFragment findRestaurantFragment = new FindRestaurantFragment();
    FollowUpOrderFragment followUpOrderFragment = new FollowUpOrderFragment();
    UserProfileFragment userProfileFragment = new UserProfileFragment();
    Toolbar toolbar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        mAuth = FirebaseAuth.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(myOnNavigationItemSelectedListener);

        // Establecer el segundo elemento como seleccionado por defecto
        navigationView.setSelectedItemId(R.id.option_find);

        // Cargar automáticamente el FindFragment al iniciar la actividad
        loadFragment(findRestaurantFragment);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener myOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if(id == R.id.option_qr){
                //loadFragment(qrScanFragment);
                Toast.makeText(Cliente.this, "Actualmente no esta disponible el escaneo Qr", Toast.LENGTH_SHORT).show();
                return true;
            }
            if(id == R.id.option_find){
                loadFragment(findRestaurantFragment);
                return true;
            }
            if(id == R.id.option_follow_up){
                //loadFragment(followUpOrderFragment);
                Toast.makeText(Cliente.this, "Actualmente no esta disponible el seguimiento de pedidos", Toast.LENGTH_SHORT).show();
                return true;
            }
            if(id == R.id.option_user){
                loadFragment(userProfileFragment);
                return true;
            }

            return false;
        }
    };

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
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
            startActivity(new Intent(Cliente.this, MainActivity.class));
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