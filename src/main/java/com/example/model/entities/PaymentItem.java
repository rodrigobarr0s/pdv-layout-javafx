package com.example.model.entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class PaymentItem {
    private final SimpleStringProperty method;
    private final SimpleDoubleProperty amount;

    public PaymentItem(String method, double amount) {
        this.method = new SimpleStringProperty(method);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getMethod() { return method.get(); }
    public void setMethod(String value) { method.set(value); }

    public double getAmount() { return amount.get(); }
    public void setAmount(double value) { amount.set(value); }

    public SimpleStringProperty methodProperty() { return method; }
    public SimpleDoubleProperty amountProperty() { return amount; }
}

