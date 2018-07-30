package com.rubic.demo.trade;

/**
 * @author rubic
 */
public class TradeTransaction {

    private long id;

    private double price;

    public TradeTransaction() {}

    public TradeTransaction(long id, double price) {
        this.id = id;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
