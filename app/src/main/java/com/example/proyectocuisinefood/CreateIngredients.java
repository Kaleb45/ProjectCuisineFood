package com.example.proyectocuisinefood;

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
import android.widget.Toast;

import com.example.proyectocuisinefood.adapter.IngredientsAdapter;
import com.example.proyectocuisinefood.adapter.MenuAdapter;
import com.example.proyectocuisinefood.model.Dish;
import com.example.proyectocuisinefood.model.Ingredients;
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

    CheckBox defaultIngredient;
    EditText newIngredient;
    Button addIngredient;
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

        defaultIngredient = v.findViewById(R.id.c_defect_ingredient);
        newIngredient = v.findViewById(R.id.ed_new_ingredient);
        addIngredient = v.findViewById(R.id.b_add_ingredient);

        recyclerViewShowIngredient = v.findViewById(R.id.r_show_ingredient);
        recyclerViewShowIngredient.setLayoutManager(new LinearLayoutManager(getContext()));

        addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddIngredient();
            }
        });

        Query query = db.collection("ingredients");

        FirestoreRecyclerOptions<Ingredients> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Ingredients>()
                .setQuery(query, Ingredients.class).build();

        ingredientsAdapter = new IngredientsAdapter(firestoreRecyclerOptions);
        ingredientsAdapter.notifyDataSetChanged();
        recyclerViewShowIngredient.setAdapter(ingredientsAdapter);

        return v;
    }

    private void onClickAddIngredient() {
        String name = newIngredient.getText().toString().trim();
        if(name.isEmpty()){
            Toast.makeText(getContext(), "No puede dejar espacios en blanco", Toast.LENGTH_SHORT).show();
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
                getDialog().dismiss(); // Cerrar el fragmento
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
    public void onStop() {
        super.onStop();
        ingredientsAdapter.stopListening();
    }
}