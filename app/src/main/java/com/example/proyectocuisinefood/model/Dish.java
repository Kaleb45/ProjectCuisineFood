package com.example.proyectocuisinefood.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Dish {
    String name, cost, photo, restaurantId;
    ArrayList<String> ingredientIds;

    public Dish(){

    }

    public Dish(String name, String cost, String photo, String restaurantId, ArrayList<String> ingredientIds) {
        this.name = name;
        this.cost = cost;
        this.photo = photo;
        this.restaurantId = restaurantId;
        this.ingredientIds = ingredientIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public ArrayList<String> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(ArrayList<String> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }
}
