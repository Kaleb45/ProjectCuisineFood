package com.example.proyectocuisinefood.model;

public class Restaurant {
    String logo,
            name,
            category1,
            category2,
            phone,
            code,
            tableDistribution,
            direction,
            relationedRestaurantId,
            photo,
            userId,
            paymentMethodId,
            quantityTables
    ;

    public Restaurant(){}

    public Restaurant(String logo, String name, String category1, String category2, String phone, String code, String tableDistribution, String direction, String relationedRestaurantId, String photo, String userId, String paymentMethodId, String quantityTables) {
        this.logo = logo;
        this.name = name;
        this.category1 = category1;
        this.category2 = category2;
        this.phone = phone;
        this.code = code;
        this.tableDistribution = tableDistribution;
        this.direction = direction;
        this.relationedRestaurantId = relationedRestaurantId;
        this.photo = photo;
        this.userId = userId;
        this.paymentMethodId = paymentMethodId;
        this.quantityTables = quantityTables;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTableDistribution() {
        return tableDistribution;
    }

    public void setTableDistribution(String tableDistribution) {
        this.tableDistribution = tableDistribution;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRelationedRestaurantId() {
        return relationedRestaurantId;
    }

    public void setRelationedRestaurantId(String relationedRestaurantId) {
        this.relationedRestaurantId = relationedRestaurantId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getQuantityTables() {
        return quantityTables;
    }

    public void setQuantityTables(String quantityTables) {
        this.quantityTables = quantityTables;
    }
}
