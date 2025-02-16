package com.example.proyectocuisinefood.application;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.proyectocuisinefood.R;
import com.example.proyectocuisinefood.adapter.IngredientsAdapter;
import com.example.proyectocuisinefood.adapter.MenuAdapter;
import com.example.proyectocuisinefood.adapter.RestaurantSelectedAdapter;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Ingredients;
import com.example.proyectocuisinefood.model.Restaurant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CreateIngredients extends DialogFragment {
    EditText newIngredient;
    Button addIngredient, subtractIngredient;
    SearchView searchIngredient;
    Query query;
    RecyclerView recyclerViewShowIngredient;
    IngredientsAdapter ingredientsAdapter;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_ingredients, container, false);
        db = FirebaseFirestore.getInstance();

        newIngredient = v.findViewById(R.id.ed_new_ingredient);
        addIngredient = v.findViewById(R.id.b_add_ingredient);
        subtractIngredient = v.findViewById(R.id.b_subtract_ingredient);
        searchIngredient = v.findViewById(R.id.sv_ingredient);

        recyclerViewShowIngredient = v.findViewById(R.id.r_show_ingredient);
        recyclerViewShowIngredient.setLayoutManager(new LinearLayoutManager(getContext()));

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddIngredient();
            }
        });

        subtractIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubtractIngredient();
            }
        });

        query = db.collection("ingredients");

        FirestoreRecyclerOptions<Ingredients> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ingredients>()
                .setQuery(query, Ingredients.class).build();

        ingredientsAdapter = new IngredientsAdapter(firestoreRecyclerOptions);
        ingredientsAdapter.notifyDataSetChanged();
        recyclerViewShowIngredient.setAdapter(ingredientsAdapter);

        searchViewRestaurant();

        return v;
    }

    private void searchViewRestaurant() {
        searchIngredient.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });
    }

    private void textSearch(String s) {
        FirestoreRecyclerOptions<Ingredients> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ingredients>()
                .setQuery(query.orderBy("name").startAt(s).endAt(s+"~"), Ingredients.class).build();

        ingredientsAdapter = new IngredientsAdapter(firestoreRecyclerOptions);
        ingredientsAdapter.startListening();
        recyclerViewShowIngredient.setAdapter(ingredientsAdapter);
    }

    private void onClickSubtractIngredient() {
        // Obtener los ingredientes seleccionados del adaptador
        ArrayList<String> selectedIngredientIds = ingredientsAdapter.getSelectedIngredientIds();

        // Verificar si hay ingredientes seleccionados
        if (selectedIngredientIds.isEmpty()) {
            Toast.makeText(getContext(), "No hay ingredientes seleccionados para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Eliminar los ingredientes seleccionados de la base de datos
        deleteSelectedIngredients(selectedIngredientIds);
    }

    private void deleteSelectedIngredients(ArrayList<String> selectedIngredientIds) {
        // Iterar sobre los ingredientes seleccionados y eliminarlos de la base de datos
        for (String ingredientId : selectedIngredientIds) {
            db.collection("ingredients").document(ingredientId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Ingredientes eliminados con éxito
                            Toast.makeText(getContext(), "Ingrediente eliminado con éxito", Toast.LENGTH_SHORT).show();
                            // Actualizar la lista de ingredientes en el adaptador después de eliminar
                            ingredientsAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error al eliminar ingredientes
                            Toast.makeText(getContext(), "Error al eliminar ingredientes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void onClickAddIngredient() {
        String name = newIngredient.getText().toString().trim();
        if(name.isEmpty()){
            Toast.makeText(getContext(), "No puede dejar espacios en blanco", Toast.LENGTH_SHORT).show();
        } else if (!name.matches("^[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+(\\s*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]*)*[a-zA-ZÀ-ÿ\\u00f1\\u00d1]+$")) { // Verifica si el nombre contiene caracteres no válidos
            Toast.makeText(getContext(), "El nombre del ingrediente solo puede contener letras", Toast.LENGTH_SHORT).show();
            return;
        } else {
            createIngredient(name);
        }
    }

    private void createIngredient(String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",name);

        db.collection("ingredients").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getContext(), "Ingrediente añadido", Toast.LENGTH_SHORT).show();
                //getDialog().dismiss(); // Cerrar el fragmento
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al añadir el ingrediente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ingredientsAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ingredientsAdapter.stopListening();
    }
}