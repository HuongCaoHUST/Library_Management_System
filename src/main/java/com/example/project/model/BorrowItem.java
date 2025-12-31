package com.example.project.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;

@Getter
public class BorrowItem {
    private final Document document;
    private final IntegerProperty quantity = new SimpleIntegerProperty(1);

    public BorrowItem(Document document) {
        this.document = document;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void increaseQuantity() {
        quantity.set(quantity.get() + 1);
    }

    public void decreaseQuantity() {
        if (quantity.get() > 1) {
            quantity.set(quantity.get() - 1);
        }
    }
}