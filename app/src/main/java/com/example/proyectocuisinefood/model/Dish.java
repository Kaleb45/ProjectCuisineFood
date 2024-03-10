package com.example.proyectocuisinefood.model;

public class Dish {
    String name, cost, photo, restaurantId;

    public Dish(){

    }

    public Dish(String name, String cost, String photo, String restaurantId) {
        this.name = name;
        this.cost = cost;
        this.photo = photo;
        this.restaurantId = restaurantId;
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
}
