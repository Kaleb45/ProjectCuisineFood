package com.example.proyectocuisinefood.model;

public class Orders {
    String numberTable, dishId, status, restaurantId;

    public Orders(){

    }

    public Orders(String numberTable, String dishId, String status, String restaurantId) {
        this.numberTable = numberTable;
        this.dishId = dishId;
        this.status = status;
        this.restaurantId = restaurantId;
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
}
