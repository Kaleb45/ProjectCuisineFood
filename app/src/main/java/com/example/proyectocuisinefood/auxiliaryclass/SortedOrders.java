package com.example.proyectocuisinefood.auxiliaryclass;

import com.example.proyectocuisinefood.model.Orders;

public class SortedOrders {
    private Orders order;
    private int time;

    public SortedOrders(){}

    public SortedOrders(Orders order, int time) {
        this.order = order;
        this.time = time;
    }

    public Orders getOrder() {
        return order;
    }

    public int getTime() {
        return time;
    }
}
