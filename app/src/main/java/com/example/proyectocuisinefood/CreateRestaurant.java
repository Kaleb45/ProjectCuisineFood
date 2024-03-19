package com.example.proyectocuisinefood;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
    StorageReference storageReference;
    PlacesClient placesClient;
    String storagePath = "restaurant/*"; // Ubicacion de la carpeta de imagenes
    private static final int codeSelectStorage = 200;
    private static final int PERMISSION_REQUEST_CODE = 300;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PLACE_PICKER_REQUEST = 2;
    private Uri imageUrl;
    String photo = "photo";
    ProgressDialog progressDialog;
    String imageType, downloadUri, logoRestaurant, tableIndication, tableDistribution, photoRestaurant, idd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_restaurant);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        // Inicializar Places API
        Places.initialize(getApplicationContext(), "AIzaSyDIEIhLdcWAaYo5FuIJhJTN4hIF3OcpduM");
        placesClient = Places.createClient(this);

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

        String restaurantId = getIntent().getStringExtra("restaurantId");

        if(restaurantId == null || restaurantId.isEmpty()){
            continueCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickContinueCreate();
                }
            });
        } else {
            continueCreate.setText("Actualizar el Restaurante");
            getRestaurant(restaurantId);
            continueCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickContinueUpdate(restaurantId);
                }
            });
        }

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

        restaurantTable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Si restaurantTable está activado, hacer visible el botón de restaurantMap
                    restaurantMap.setVisibility(View.VISIBLE);
                } else {
                    // Si restaurantTable no está activado, ocultar el botón de restaurantMap
                    restaurantMap.setVisibility(View.GONE);
                    // Establecer el campo "tableDistribution" como vacío
                    tableDistribution = "";
                }
            }
        });

        restaurantIndication.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Aquí actualizas la base de datos con el campo "tableIndication" como "Indicación"
                    // Por ejemplo:
                    tableIndication = "Indicación";
                } else {
                    tableIndication = "";
                }
            }
        });

        restaurantVMPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // Si restaurantVMPay no está activado, mostrar un mensaje o realizar alguna acción
                    // para indicar que este botón es obligatorio.
                    Toast.makeText(getApplicationContext(), "Por favor, active restaurantVMPay", Toast.LENGTH_SHORT).show();
                }
            }
        });


        restaurantLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType = "logo"; // Indica que se está subiendo el logo
                requestPermissions();
            }
        });

        restaurantMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType = "tableDistribution"; // Indica que se está subiendo el mapa de distribución de mesas
                requestPermissions();
            }
        });

        restaurantImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageType = "photo"; // Indica que se está subiendo una foto general del restaurante
                requestPermissions();
            }
        });

        restaurantDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lanzar la pantalla de selección de lugar
                startPlacePicker();
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

        // Habilitar el botón de retroceso en la barra de herramientas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Manejar el evento de clic en el botón de retroceso para volver a la actividad de Admin
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateRestaurant.this, Admin.class);
                startActivity(intent);
                finish(); // Opcional, dependiendo de si deseas mantener la actividad actual en la pila de actividades
            }
        });

    }

    private void startPlacePicker() {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                .setCountry("MX")
                .build(this);

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // Método para solicitar permisos
    private void requestPermissions() {
        // Verificar si ya se tienen los permisos
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen los permisos, solicitarlos al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Si ya se tienen los permisos, abrir el selector de imágenes
            openImageSelector();
        }
    }

    // Método para abrir el selector de imágenes
    private void openImageSelector() {
        // Crear un diálogo para que el usuario elija entre la galería de fotos y la cámara
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción")
                .setItems(new String[]{"Galería de fotos", "Cámara"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Abrir la galería de fotos
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                                break;
                            case 1:
                                // Abrir la cámara fotográfica
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    // Método para manejar el resultado de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        openImageSelector();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String address = place.getAddress();

                // Mostrar la dirección en el botón
                restaurantDirection.setText(address);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Error: ",""+status.getStatusMessage());
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                // Si el usuario elige una imagen de la galería de fotos, obtener la URI de la imagen
                imageUrl = data.getData();
                // Verificar qué ImageButton fue seleccionado y establecer la imagen en consecuencia
                if (imageType.equals("logo")) {
                    loadImageIntoButton(restaurantLogo, imageUrl.toString());
                    logoRestaurant = imageUrl.toString();
                } else if (imageType.equals("tableDistribution")) {
                    loadImageIntoButton(restaurantMap, imageUrl.toString());
                    tableDistribution = imageUrl.toString();
                } else if (imageType.equals("photo")) {
                    loadImageIntoButton(restaurantImage, imageUrl.toString());
                    photoRestaurant = imageUrl.toString();
                }

                sendPhoto(imageUrl);
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                // Si el usuario toma una foto con la cámara, obtener la imagen
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUrl = data.getData();
                // Verificar qué ImageButton fue seleccionado y establecer la imagen en consecuencia
                if (imageType.equals("logo")) {
                    loadImageIntoButton(restaurantLogo, imageUrl.toString());
                    logoRestaurant = imageUrl.toString();
                } else if (imageType.equals("tableDistribution")) {
                    loadImageIntoButton(restaurantMap, imageUrl.toString());
                    tableDistribution = imageUrl.toString();
                } else if (imageType.equals("photo")) {
                    loadImageIntoButton(restaurantImage, imageUrl.toString());
                    photoRestaurant = imageUrl.toString();
                }

                sendPhoto(imageUrl);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Método para obtener la URI de la imagen capturada con la cámara
    private Uri getImageUriFromCamera(Intent data) {
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    // Método para establecer la imagen en el ImageButton correspondiente
    private void loadImageIntoButton(ImageButton imageButton, String imageUrl) {
        // Utilizar Picasso para cargar la imagen en el ImageButton
        Picasso.get().load(imageUrl).resize(150,150).into(imageButton);
    }


    private void sendPhoto(Uri imageUrl) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        String ruteStoragePhoto = storagePath+""+photo+""+mAuth.getUid()+""+imageType;
        StorageReference reference = storageReference.child(ruteStoragePhoto);
        reference.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl(); // Toma la url que se le asignara a la imagen
                while(!uriTask.isSuccessful());
                if(uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUri= uri.toString();
                            Toast.makeText(CreateRestaurant.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateRestaurant.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
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

    private void onClickContinueUpdate(String restaurantId) {
        String name = restaurantName.getText().toString().trim();
        String category1 = selectedItemCategory1;
        String category2 = selectedItemCategory2;
        String phone = restaurantPhone.getText().toString().trim();
        String code = restaurantCode.getText().toString().trim();
        String direction = restaurantDirection.getText().toString().trim();
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

        // Verificar si el teléfono tiene y longitud máxima de 10 dígitos
        if (phone.length() != 10) {
            // Mostrar un mensaje de error si el teléfono no es válido
            Toast.makeText(this, "El teléfono debe contener solo números y tener 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.isEmpty() || category1.isEmpty() || category2.isEmpty() || phone.isEmpty() || code.isEmpty() || direction.isEmpty() || direction.equals("Dirección") ||
                mondayOpen.equals("Entrada") || mondayClose.equals("Cierre") || tuesdayOpen.equals("Entrada") || tuesdayClose.equals("Cierre") || wednesdayOpen.equals("Entrada") || wednesdayClose.equals("Cierre") ||
                thursdayOpen.equals("Entrada") || thursdayClose.equals("Cierre") || fridayOpen.equals("Entrada") || fridayClose.equals("Cierre") || saturdayOpen.equals("Entrada") || saturdayClose.equals("Cierre") ||
                sundayOpen.equals("Entrada") || sundayClose.equals("Cierre")){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            updateRestaurant(name, category1, category2, phone, code, direction, restaurantId);
        }
    }

    private void updateRestaurant(String name, String category1, String category2, String phone, String code, String direction, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("category1",category1);
        map.put("category2",category2);
        map.put("phone",phone);
        map.put("code",code);
        map.put("tableDistribution",tableDistribution);
        map.put("tableIndication",tableIndication);
        map.put("direction", direction);
        map.put("logo", logoRestaurant);
        map.put("photo", photoRestaurant);
        // Añadir los datos del restaurante...
        map.put("userId", mAuth.getCurrentUser().getUid()); // Usa el UID del usuario autenticado
        map.put("paymentMethodId",idd);

        db.collection("restaurant").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CreateRestaurant.this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateRestaurant.this, "Error al crear el restaurante", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRestaurant (String id){
        db.collection("restaurant").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String category1 = documentSnapshot.getString("category1");
                String category2 = documentSnapshot.getString("category2");
                String phone = documentSnapshot.getString("phone");
                String code = documentSnapshot.getString("code");
                String direction = documentSnapshot.getString("direction");
                db.collection("schedules")
                        .whereEqualTo("restaurantId", id) // Filtrar por el ID del restaurante
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    // Obtener los datos del documento
                                    String day = documentSnapshot.getString("day");
                                    String openTime = documentSnapshot.getString("openTime");
                                    String closeTime = documentSnapshot.getString("closeTime");

                                    // Asignar los horarios correspondientes al día
                                    switch (day) {
                                        case "Lunes":
                                            mondayOpenSchedule.setText(openTime);
                                            mondayCloseSchedule.setText(closeTime);
                                            break;
                                        case "Martes":
                                            tuesdayOpenSchedule.setText(openTime);
                                            tuesdayCloseSchedule.setText(closeTime);
                                            break;
                                        case "Miércoles":
                                            wednesdayOpenSchedule.setText(openTime);
                                            wednesdayCloseSchedule.setText(closeTime);
                                            break;
                                        case "Jueves":
                                            thursdayOpenSchedule.setText(openTime);
                                            thursdayCloseSchedule.setText(closeTime);
                                            break;
                                        case "Viernes":
                                            fridayOpenSchedule.setText(openTime);
                                            fridayCloseSchedule.setText(closeTime);
                                            break;
                                        case "Sábado":
                                            saturdayOpenSchedule.setText(openTime);
                                            saturdayCloseSchedule.setText(closeTime);
                                            break;
                                        case "Domingo":
                                            sundayOpenSchedule.setText(openTime);
                                            sundayCloseSchedule.setText(closeTime);
                                            break;
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateRestaurant.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                            }
                        });

                logoRestaurant = documentSnapshot.getString("logo");
                photoRestaurant = documentSnapshot.getString("photo");
                tableDistribution = documentSnapshot.getString("tableDistribution");

                /*Picasso.get().load(logoRestaurant).resize(150,150).into(restaurantLogo);
                Picasso.get().load(photoRestaurant).resize(150,150).into(restaurantImage);
                Picasso.get().load(tableDistribution).resize(150,150).into(restaurantMap);*/

                restaurantName.setText(name);
                restaurantPhone.setText(phone);
                restaurantCode.setText(code);
                restaurantDirection.setText(direction);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateRestaurant.this, "Error al obtner los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickContinueCreate() {
        String name = restaurantName.getText().toString().trim();
        String category1 = selectedItemCategory1;
        String category2 = selectedItemCategory2;
        String phone = restaurantPhone.getText().toString().trim();
        String code = restaurantCode.getText().toString().trim();
        String direction = restaurantDirection.getText().toString().trim();
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

        // Verificar si el teléfono tiene y longitud máxima de 10 dígitos
        if (phone.length() != 10) {
            // Mostrar un mensaje de error si el teléfono no es válido
            Toast.makeText(this, "El teléfono debe contener solo números y tener 10 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.isEmpty() || category1.isEmpty() || category2.isEmpty() || phone.isEmpty() || code.isEmpty() || direction.isEmpty() || direction.equals("Dirección") ||
                mondayOpen.equals("Entrada") || mondayClose.equals("Cierre") || tuesdayOpen.equals("Entrada") || tuesdayClose.equals("Cierre") || wednesdayOpen.equals("Entrada") || wednesdayClose.equals("Cierre") ||
                thursdayOpen.equals("Entrada") || thursdayClose.equals("Cierre") || fridayOpen.equals("Entrada") || fridayClose.equals("Cierre") || saturdayOpen.equals("Entrada") || saturdayClose.equals("Cierre") ||
                sundayOpen.equals("Entrada") || sundayClose.equals("Cierre")){
            Toast.makeText(this, "No puede dejar espacios vacios", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            createRestaurant(name, category1, category2, phone, code, direction);
        }
    }

    private void createRestaurant(String name, String category1, String category2, String phone, String code, String direction) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);
        map.put("category1",category1);
        map.put("category2",category2);
        map.put("phone",phone);
        map.put("code",code);
        map.put("tableDistribution",tableDistribution);
        map.put("tableIndication",tableIndication);
        map.put("direction", direction);
        map.put("logo", logoRestaurant);
        map.put("photo", photoRestaurant);
        // Añadir los datos del restaurante...
        map.put("userId", mAuth.getCurrentUser().getUid()); // Usa el UID del usuario autenticado

        if (restaurantVMPay.isChecked()) {

            // Creamos un HashMap con los campos necesarios para el nuevo documento
            Map<String, Object> paymentData = new HashMap<>();
            paymentData.put("cardNumber", "");
            paymentData.put("cvv", "");
            paymentData.put("date", "");
            paymentData.put("name", "");
            paymentData.put("type", "Visa/Mastercard");

            // Añadimos el nuevo documento a la colección paymentMethods
            db.collection("paymentMethods").add(paymentData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            idd = documentReference.getId();
                            map.put("paymentMethodId",idd);
                            // Si se añade correctamente, puedes realizar alguna acción adicional si es necesario
                            Log.d(TAG, "Documento de métodos de pago creado con ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Si hay un error al añadir el documento, manejarlo aquí
                            Log.e(TAG, "Error al crear el documento de métodos de pago", e);
                        }
                    });
        }

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