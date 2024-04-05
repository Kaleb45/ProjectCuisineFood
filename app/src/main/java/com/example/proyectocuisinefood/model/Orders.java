package com.example.proyectocuisinefood.model;

import java.util.ArrayList;

public class Orders {
    //Orders
    String numberTable, dishId, status, restaurantId, userId, quantity;
    //Dish and User
    String dishName, dishDescription, dishImage, userName;
    ArrayList<String> ingredientIds;

    public Orders() {

    }

    public Orders(String numberTable, String dishId, String status, String restaurantId, String userId, String quantity, String dishName, String dishDescription, String dishImage, String userName, ArrayList<String> ingredientIds) {
        this.numberTable = numberTable;
        this.dishId = dishId;
        this.status = status;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.quantity = quantity;
        this.dishName = dishName;
        this.dishDescription = dishDescription;
        this.dishImage = dishImage;
        this.userName = userName;
        this.ingredientIds = ingredientIds;
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

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getDishDescription() {
        return dishDescription;
    }

    public void setDishDescription(String dishDescription) {
        this.dishDescription = dishDescription;
    }

    public String getDishImage() {
        return dishImage;
    }

    public void setDishImage(String dishImage) {
        this.dishImage = dishImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
