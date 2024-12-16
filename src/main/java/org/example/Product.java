package org.example;

public class Product {
    private String name;
    private String shop;
    private double price;
    private String image_Url;

    public Product() {
    }

    public String getName() {
        return name;
    }

    public String getShop() {
        return shop;
    }

    public double getPrice() {
        return price;
    }

    public String getImage_Url() {
        return image_Url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImage_Url(String image_Url) {
        this.image_Url = image_Url;
    }

    @Override
    public String toString() {
        return  "name: " + name + " || shop: " + shop + " || price: " + price;
    }
}
