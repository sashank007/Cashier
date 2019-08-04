package com.sas.cashier.Data;


public class Item {

    public Item()
    {}
    public Item(String title , Double amount , String image)
    {
        this.title=title;
        this.amount=amount;
        this.image=image;

    }


    public String title;

    public Double amount;


    public String image;

    public String getTitle()
    {
        return this.title;
    }
    public Double getAmount()
    {
        return this.amount;
    }
    public String getImage()
    {
        return this.image;
    }




}
