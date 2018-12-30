package com.google.android.gms.example.RingtoneFree;

public class Product {
    private int id;
    private String text_str;

    //Constructor

    public Product(int id, String text_str) {
        this.id = id;
        this.text_str = text_str;
    }

    //Setter, getter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText_str() {
        return text_str;
    }

    public void setText_str(String text_str) {
        this.text_str = text_str;
    }
}
