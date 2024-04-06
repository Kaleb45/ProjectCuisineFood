package com.example.proyectocuisinefood.model;

import java.util.ArrayList;

public class Orders {
    //Orders
    String numberTable, dishId, status, restaurantId, userId, quantity, time;
    ArrayList<String> ingredientIds;

    public Orders(){

    }

    public Orders(String numberTable, String dishId, String status, String restaurantId, String userId, String quantity, String time, ArrayList<String> ingredientIds) {
        this.numberTable = numberTable;
        this.dishId = dishId;
        this.status = status;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.ingredientIds = ingredientIds;
        this.quantity = quantity;
        this.time = time;
    }

    public String getNumberTable() {
        return numberTable;
    }

    public void setNumberTable(String numberTable) {
        this.numberTable = numberTable;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(ArrayList<String> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
