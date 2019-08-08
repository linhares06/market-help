package com.felipe.markethelper.adapter;

import java.math.BigDecimal;

public class Item {

    public long id;
    public String name;
    public String price;
    public String brand;
    public Integer quantity;
    public BigDecimal total;

    public Item(long id, String name, String price, String brand, Integer quantity) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.quantity = quantity;
        this.total = BigDecimal.valueOf(quantity).multiply(new BigDecimal(price));
    }
}
