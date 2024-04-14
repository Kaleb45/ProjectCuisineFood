package com.example.proyectocuisinefood.model;

public class Restaurant {
    String name, logo, direction, category1, category2, code, paymentMethodId, phone, photo, quantityTables, relationedRestaurantId, tableDistribution, tableIndication, userId;

    public Restaurant(){}

    public Restaurant(String name, String logo, String direction, String category1, String category2, String code, String paymentMethodId, String phone, String photo, String quantityTables, String relationedRestaurantId, String tableDistribution, String tableIndication, String userId) {
        this.name = name;
        this.logo = logo;
        this.direction = direction;
        this.category1 = category1;
        this.category2 = category2;
        this.code = code;
        this.paymentMethodId = paymentMethodId;
        this.phone = phone;
        this.photo = photo;
        this.quantityTables = quantityTables;
        this.relationedRestaurantId = relationedRestaurantId;
        this.tableDistribution = tableDistribution;
        this.tableIndication = tableIndication;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCategory1() {
        return category1;
    }

    public void setCategory1(String category1) {
        this.category1 = category1;
    }

    public String getCategory2() {
        return category2;
    }

    public void setCategory2(String category2) {
        this.category2 = category2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getQuantityTables() {
        return quantityTables;
    }

    public void setQuantityTables(String quantityTables) {
        this.quantityTables = quantityTables;
    }

    public String getRelationedRestaurantId() {
        return relationedRestaurantId;
    }

    public void setRelationedRestaurantId(String relationedRestaurantId) {
        this.relationedRestaurantId = relationedRestaurantId;
    }

    public String getTableDistribution() {
        return tableDistribution;
    }

    public void setTableDistribution(String tableDistribution) {
        this.tableDistribution = tableDistribution;
    }

    public String getTableIndication() {
        return tableIndication;
    }

    public void setTableIndication(String tableIndication) {
        this.tableIndication = tableIndication;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
