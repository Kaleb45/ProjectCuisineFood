package com.example.proyectocuisinefood.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Orders {
    //Orders
    String numberTable, dishId, status, restaurantId, userId, quantity, time, orderId, paymentMethodId, scheduleId, totalPrice;

    Timestamp timestamp;
    ArrayList<String> ingredientIds;

    public Orders(){

    }

    public Orders(String numberTable, String dishId, String status, String restaurantId, String userId, String quantity, String time, String orderId, String paymentMethodId, String scheduleId, Timestamp timestamp, String totalPrice, ArrayList<String> ingredientIds) {
        this.numberTable = numberTable;
        this.dishId = dishId;
        this.status = status;
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.quantity = quantity;
        this.time = time;
        this.orderId = orderId;
        this.paymentMethodId = paymentMethodId;
        this.scheduleId = scheduleId;
        this.timestamp = timestamp;
        this.totalPrice = totalPrice;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<String> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(ArrayList<String> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }
}
