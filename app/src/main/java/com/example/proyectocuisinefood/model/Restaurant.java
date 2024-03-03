package com.example.proyectocuisinefood.model;

public class Restaurant {
    String name,
            categoryPrimaryRestaurant,
            categorySecondaryRestaurant,
            phoneRestaurant,
            addressRestaurant,
            relatedRestaurants,
            distributionTablesRestaurant,
            codeEmployeesRestaurant,
            methodsPaymentsRestaurants,
            userId;

    public Restaurant(){}

    public Restaurant(String name, String categoryPrimaryRestaurant, String categorySecondaryRestaurant, String phoneRestaurant, String addressRestaurant, String relatedRestaurants, String distributionTablesRestaurant, String codeEmployeesRestaurant, String methodsPaymentsRestaurants, String userId) {
        this.name = name;
        this.categoryPrimaryRestaurant = categoryPrimaryRestaurant;
        this.categorySecondaryRestaurant = categorySecondaryRestaurant;
        this.phoneRestaurant = phoneRestaurant;
        this.addressRestaurant = addressRestaurant;
        this.relatedRestaurants = relatedRestaurants;
        this.distributionTablesRestaurant = distributionTablesRestaurant;
        this.codeEmployeesRestaurant = codeEmployeesRestaurant;
        this.methodsPaymentsRestaurants = methodsPaymentsRestaurants;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void getName(String name) {
        this.name = name;
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

    public String getCodeEmployeesRestaurant() {
        return codeEmployeesRestaurant;
    }

    public void setCodeEmployeesRestaurant(String codeEmployeesRestaurant) {
        this.codeEmployeesRestaurant = codeEmployeesRestaurant;
    }

    public String getMethodsPaymentsRestaurants() {
        return methodsPaymentsRestaurants;
    }

    public void setMethodsPaymentsRestaurants(String methodsPaymentsRestaurants) {
        this.methodsPaymentsRestaurants = methodsPaymentsRestaurants;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
