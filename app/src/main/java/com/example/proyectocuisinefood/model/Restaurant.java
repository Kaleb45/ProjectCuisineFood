package com.example.proyectocuisinefood.model;

public class Restaurant {
    String name,
            logo,
            direction,
            categoryPrimaryRestaurant,
            categorySecondaryRestaurant,
            phoneRestaurant,
            addressRestaurant,
            relatedRestaurants,
            distributionTablesRestaurant,
            code,
            methodsPaymentsRestaurants,
            adminRestaurant;

    public Restaurant(){}

    public Restaurant(String name, String logo, String direction, String categoryPrimaryRestaurant, String categorySecondaryRestaurant, String phoneRestaurant, String addressRestaurant, String relatedRestaurants, String distributionTablesRestaurant, String code, String methodsPaymentsRestaurants, String adminRestaurant) {
        this.name = name;
        this.logo = logo;
        this.direction = direction;
        this.categoryPrimaryRestaurant = categoryPrimaryRestaurant;
        this.categorySecondaryRestaurant = categorySecondaryRestaurant;
        this.phoneRestaurant = phoneRestaurant;
        this.addressRestaurant = addressRestaurant;
        this.relatedRestaurants = relatedRestaurants;
        this.distributionTablesRestaurant = distributionTablesRestaurant;
        this.code = code;
        this.methodsPaymentsRestaurants = methodsPaymentsRestaurants;
        this.adminRestaurant = adminRestaurant;
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

    public String getCategoryPrimaryRestaurant() {
        return categoryPrimaryRestaurant;
    }

    public void setCategoryPrimaryRestaurant(String categoryPrimaryRestaurant) {
        this.categoryPrimaryRestaurant = categoryPrimaryRestaurant;
    }

    public String getCategorySecondaryRestaurant() {
        return categorySecondaryRestaurant;
    }

    public void setCategorySecondaryRestaurant(String categorySecondaryRestaurant) {
        this.categorySecondaryRestaurant = categorySecondaryRestaurant;
    }

    public String getPhoneRestaurant() {
        return phoneRestaurant;
    }

    public void setPhoneRestaurant(String phoneRestaurant) {
        this.phoneRestaurant = phoneRestaurant;
    }

    public String getAddressRestaurant() {
        return addressRestaurant;
    }

    public void setAddressRestaurant(String addressRestaurant) {
        this.addressRestaurant = addressRestaurant;
    }

    public String getRelatedRestaurants() {
        return relatedRestaurants;
    }

    public void setRelatedRestaurants(String relatedRestaurants) {
        this.relatedRestaurants = relatedRestaurants;
    }

    public String getDistributionTablesRestaurant() {
        return distributionTablesRestaurant;
    }

    public void setDistributionTablesRestaurant(String distributionTablesRestaurant) {
        this.distributionTablesRestaurant = distributionTablesRestaurant;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMethodsPaymentsRestaurants() {
        return methodsPaymentsRestaurants;
    }

    public void setMethodsPaymentsRestaurants(String methodsPaymentsRestaurants) {
        this.methodsPaymentsRestaurants = methodsPaymentsRestaurants;
    }

    public String getAdminRestaurant() {
        return adminRestaurant;
    }

    public void setAdminRestaurant(String adminRestaurant) {
        this.adminRestaurant = adminRestaurant;
    }
}
