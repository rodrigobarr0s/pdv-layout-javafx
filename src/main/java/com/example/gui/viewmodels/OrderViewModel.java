package com.example.gui.viewmodels;

import com.example.model.entities.Order;
import com.example.model.entities.OrderItem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OrderViewModel {
    private Order order;
    private ObservableList<OrderItem> observableItems;

    public OrderViewModel(Order order) {
        this.order = order;
        this.observableItems = FXCollections.observableArrayList(order.getItems());
    }

    public void syncToEntity() {
        order.getItems().clear();
        order.getItems().addAll(observableItems);
    }

    public ObservableList<OrderItem> getObservableItems() {
        return observableItems;
    }

    public Order getOrder() {
        return order;
    }
    
}

