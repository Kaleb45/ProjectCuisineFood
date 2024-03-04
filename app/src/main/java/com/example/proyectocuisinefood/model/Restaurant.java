package com.example.proyectocuisinefood.model;

public class Restaurant {
    String name,
            logo,
            categoryPrimaryRestaurant,
            categorySecondaryRestaurant,
            phoneRestaurant,
            addressRestaurant,
            relatedRestaurants,
            distributionTablesRestaurant,
            codeEmployeesRestaurant,
            methodsPaymentsRestaurants,
            adminRestaurant;

    public Restaurant(){}

    public Restaurant(String name, String logo, String categoryPrimaryRestaurant, String categorySecondaryRestaurant, String phoneRestaurant, String addressRestaurant, String relatedRestaurants, String distributionTablesRestaurant, String codeEmployeesRestaurant, String methodsPaymentsRestaurants, String adminRestaurant) {
        this.name = name;
        this.logo = logo;
        this.categoryPrimaryRestaurant = categoryPrimaryRestaurant;
        this.categorySecondaryRestaurant = categorySecondaryRestaurant;
        this.phoneRestaurant = phoneRestaurant;
        this.addressRestaurant = addressRestaurant;
        this.relatedRestaurants = relatedRestaurants;
        this.distributionTablesRestaurant = distributionTablesRestaurant;
        this.codeEmployeesRestaurant = codeEmployeesRestaurant;
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

    public String getAdminRestaurant() {
        return adminRestaurant;
    }

    public void setAdminRestaurant(String adminRestaurant) {
        this.adminRestaurant = adminRestaurant;
    }
}
