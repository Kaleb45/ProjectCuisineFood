package com.example.proyectocuisinefood.model;

public class Order {
    String numberTable, orderImage, orderDescription, orderName;

    public Order(){

    }

    public Order(String numberTable, String orderImage, String orderDescription, String orderName) {
        this.numberTable = numberTable;
        this.orderImage = orderImage;
        this.orderDescription = orderDescription;
        this.orderName = orderName;
    }

    public String getNumberTable() {
        return numberTable;
    }

    public void setNumberTable(String numberTable) {
        this.numberTable = numberTable;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public void setOrderImage(String orderImage) {
        this.orderImage = orderImage;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }
}
