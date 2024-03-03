package com.example.proyectocuisinefood;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateRestaurant extends AppCompatActivity{

    String selectedItemCategory1, selectedItemCategory2;
    int hour, minute;
    ImageButton restaurantLogo, restaurantMap, restaurantImage;
    EditText restaurantName, restaurantPhone, restaurantCode;
    Button restaurantDirection, restaurantRelated, continueCreate,
            mondayOpenSchedule, tuesdayOpenSchedule, wednesdayOpenSchedule, thursdayOpenSchedule, fridayOpenSchedule, saturdayOpenSchedule, sundayOpenSchedule,
            mondayCloseSchedule, tuesdayCloseSchedule, wednesdayCloseSchedule, thursdayCloseSchedule, fridayCloseSchedule, saturdayCloseSchedule, sundayCloseSchedule;
    Switch restaurantTable, restaurantIndication, restaurantVMPay;
    Spinner spinRestaurantCategory1, spinRestaurantCategory2;

    String[] typeCategory1={
            "Comida rápida",
            "Comida Casual",
            "Comida Gourmet",
            "Étnico",
    };

    String[] typeCategory2={
            "Mariscos",
            "Parrilla/Asador",
            "Vegetariano/Vegano",
            "Sushi",
            "Fondue",
            "Postres",
            "Desyuno/Brunch"
    };

    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        restaurantLogo = findViewById(R.id.imb_logo_restaurant);
        restaurantMap = findViewById(R.id.imb_map_tables_restaurant);
        restaurantImage = findViewById(R.id.imb_images_restaurant);

        restaurantName = findViewById(R.id.ed_name_restaurant);
        restaurantPhone = findViewById(R.id.ed_phone_restaurant);
        restaurantCode = findViewById(R.id.ed_code_restaurant);

        restaurantDirection = findViewById(R.id.b_direction_restaurant);
        restaurantRelated = findViewById(R.id.b_related_restaurant);
        continueCreate = findViewById(R.id.b_continue_restaurant);

        mondayOpenSchedule = findViewById(R.id.b_monday_oschedule);
        tuesdayOpenSchedule = findViewById(R.id.b_tuesday_oschedule);
        wednesdayOpenSchedule = findViewById(R.id.b_wednesday_oschedule);
        thursdayOpenSchedule = findViewById(R.id.b_thursday_oschedule);
        fridayOpenSchedule = findViewById(R.id.b_friday_oschedule);
        saturdayOpenSchedule = findViewById(R.id.b_saturday_oschedule);
        sundayOpenSchedule = findViewById(R.id.b_sunday_oschedule);

        mondayCloseSchedule = findViewById(R.id.b_monday_cschedule);
        tuesdayCloseSchedule = findViewById(R.id.b_tuesday_cschedule);
        wednesdayCloseSchedule = findViewById(R.id.b_wednesday_cschedule);
        thursdayCloseSchedule = findViewById(R.id.b_thursday_cschedule);
        fridayCloseSchedule = findViewById(R.id.b_friday_cschedule);
        saturdayCloseSchedule = findViewById(R.id.b_saturday_cschedule);
        sundayCloseSchedule = findViewById(R.id.b_sunday_cschedule);

        restaurantTable = findViewById(R.id.bs_tables1_restaurant);
        restaurantIndication = findViewById(R.id.bs_tables2_restaurant);
        restaurantVMPay = findViewById(R.id.bs_vm_pay_restaurant);

        spinRestaurantCategory1 = findViewById(R.id.s_category1_restaurant);
        spinRestaurantCategory2 = findViewById(R.id.s_category2_restaurant);

        ArrayAdapter adapterSpinCategory1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,typeCategory1);
        adapterSpinCategory1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRestaurantCategory1.setAdapter(adapterSpinCategory1);

        ArrayAdapter adapterSpinCategory2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,typeCategory2);
        adapterSpinCategory2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRestaurantCategory2.setAdapter(adapterSpinCategory2);

        spinRestaurantCategory1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CreateRestaurant.this, ""+adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                selectedItemCategory1 = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinRestaurantCategory2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(CreateRestaurant.this, ""+adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
                selectedItemCategory2 = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mondayOpenSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(mondayOpenSchedule);
            }
        });

        tuesdayOpenSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(tuesdayOpenSchedule);
            }
        });

        wednesdayOpenSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(wednesdayOpenSchedule);
            }
        });

        thursdayOpenSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(thursdayOpenSchedule);
            }
        });

        fridayOpenSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(fridayOpenSchedule);
            }
        });

        saturdayOpenSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(saturdayOpenSchedule);
            }
        });

        sundayOpenSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(sundayOpenSchedule);
            }
        });

        mondayCloseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(mondayCloseSchedule);
            }
        });

        tuesdayCloseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(tuesdayCloseSchedule);
            }
        });

        wednesdayCloseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(wednesdayCloseSchedule);
            }
        });

        thursdayCloseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(thursdayCloseSchedule);
            }
        });

        fridayCloseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(fridayCloseSchedule);
            }
        });

        saturdayCloseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(saturdayCloseSchedule);
            }
        });

        sundayCloseSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSchedule(sundayCloseSchedule);
            }
        });

        continueCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickContinueCreate();
            }
        });
    }

    private void onClickSchedule(final Button button) {
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourSchedule, int minuteSchedule) {
                // Formatea la hora y los minutos para asegurar el formato de dos dígitos
                String formattedTime = String.format("%02d:%02d", hourSchedule, minuteSchedule);
                button.setText(formattedTime);
                // Guardar la información en un String con el formato adecuado
                String scheduleTime = formattedTime;
                // Puedes hacer algo con la información guardada, como pasarlo a la base de datos
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    private void onClickContinueCreate() {
        String name = restaurantName.getText().toString().trim();
        String category1 = selectedItemCategory1;
        String category2 = selectedItemCategory2;
        String phone = restaurantPhone.getText().toString().trim();
        String code = restaurantCode.getText().toString().trim();
        String mondayOpen = mondayOpenSchedule.getText().toString().trim();
        String mondayClose = mondayCloseSchedule.getText().toString().trim();
        String tuesdayOpen = tuesdayOpenSchedule.getText().toString().trim();
        String tuesdayClose = tuesdayCloseSchedule.getText().toString().trim();
        String wednesdayOpen = wednesdayOpenSchedule.getText().toString().trim();
        String wednesdayClose = wednesdayCloseSchedule.getText().toString().trim();
        String thursdayOpen = thursdayOpenSchedule.getText().toString().trim();
        String thursdayClose = thursdayCloseSchedule.getText().toString().trim();
        String fridayOpen = fridayOpenSchedule.getText().toString().trim();
        String fridayClose = fridayCloseSchedule.getText().toString().trim();
        String saturdayOpen = saturdayOpenSchedule.getText().toString().trim();
        String saturdayClose = saturdayCloseSchedule.getText().toString().trim();
        String sundayOpen = sundayOpenSchedule.getText().toString().trim();
        String sundayClose = sundayCloseSchedule.getText().toString().trim();


        if(name.isEmpty() || category1.isEmpty() || category2.isEmpty() || phone.isEmpty() || code.isEmpty() ||
                mondayOpen.equals("Entrada") || mondayClose.equals("Cierre") || tuesdayOpen.equals("Entrada") || tuesdayClose.equals("Cierre") || wednesdayOpen.equals("Entrada") || wednesdayClose.equals("Cierre") ||
                thursdayOpen.equals("Entrada") || thursdayClose.equals("Cierre") || fridayOpen.equals("Entrada") || fridayClose.equals("Cierre") || saturdayOpen.equals("Entrada") || saturdayClose.equals("Cierre") ||
                sundayOpen.equals("Entrada") || sundayClose.equals("Cierre")){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            createRestaurant(name, category1, category2, phone, code);
        }
    }

    private void createRestaurant(String name, String category1, String category2, String phone, String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("category1",category1);
        map.put("category2",category2);
        map.put("phone",phone);
        map.put("code",code);
        // Añadir los datos del restaurante...
        map.put("userId", mAuth.getCurrentUser().getUid()); // Usa el UID del usuario autenticado

        db.collection("restaurant").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String restaurantId = documentReference.getId(); // Aquí obtienes el ID del restaurante
                Toast.makeText(CreateRestaurant.this, "Creado Exitosamente", Toast.LENGTH_SHORT).show();
                saveSchedulesForRestaurant(restaurantId); // Llamada al método para guardar los horarios asociados al restaurante
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateRestaurant.this, "Error al crear el restaurante", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSchedulesForRestaurant(String restaurantId) {
        // Guardar los horarios para el restaurante utilizando el ID proporcionado
        // Suponiendo que tienes una lista de botones de horarios
        Button[] scheduleButtons = {
                mondayOpenSchedule, mondayCloseSchedule,
                tuesdayOpenSchedule, tuesdayCloseSchedule,
                wednesdayOpenSchedule, wednesdayCloseSchedule,
                thursdayOpenSchedule, thursdayCloseSchedule,
                fridayOpenSchedule, fridayCloseSchedule,
                saturdayOpenSchedule, saturdayCloseSchedule,
                sundayOpenSchedule, sundayCloseSchedule
        };

        // Iterar sobre los botones para guardar los horarios
        for (int i = 0; i < scheduleButtons.length; i += 2) {
            String day = getDayFromButtonId(scheduleButtons[i].getId());
            String openTime = scheduleButtons[i].getText().toString();
            String closeTime = scheduleButtons[i + 1].getText().toString();

            // Crear un mapa con la información del horario
            Map<String, Object> scheduleData = new HashMap<>();
            scheduleData.put("day", day);
            scheduleData.put("openTime", openTime);
            scheduleData.put("closeTime", closeTime);
            scheduleData.put("restaurantId", restaurantId);

            // Guardar el horario en la colección "Schedules"
            db.collection("schedules")
                    .add(scheduleData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(CreateRestaurant.this, "Horario guardado exitosamente", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateRestaurant.this, "Error al guardar el horario", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private String getDayFromButtonId(int buttonId) {
        String day = "";
        View button = findViewById(buttonId);
        if (button != null) {
            day = button.getTag().toString();
        }
        return day;
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
            startActivity(new Intent(CreateRestaurant.this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}