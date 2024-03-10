package com.example.proyectocuisinefood.model;

public class Ingredients {
    String name;
    boolean isDefault;

    public Ingredients(){

    }

    public Ingredients(String name, boolean isDefault) {
        this.name = name;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
