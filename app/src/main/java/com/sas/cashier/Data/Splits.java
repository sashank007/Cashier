package com.sas.cashier.Data;

public class Splits {

    public String Name;
    public Double Amount;

    public Splits(String name , Double amount )
    {
        this.Name=name;
        this.Amount = amount;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }
}
