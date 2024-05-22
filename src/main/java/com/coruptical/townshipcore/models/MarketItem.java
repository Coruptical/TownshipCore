package com.coruptical.townshipcore.models;

import org.bukkit.Material;

public class MarketItem{
    private String itemName;
    private double buyPrice;
    private double sellPrice;
    private Material material;

    public MarketItem(String itemName, double buyPrice, double sellPrice, Material material) {
        this.itemName = itemName;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.material = material;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    @Override
    public String toString() {
        return "MarketItem{" +
                "itemName='" + itemName + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                '}';
    }
}
