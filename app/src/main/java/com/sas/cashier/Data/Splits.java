package com.sas.cashier.Data;

public class Splits {

    public String SplitName;
    public Double AmountSpent;

    public  Splits()
    {

    }

    public Splits(String name , Double amount )
    {
        this.SplitName=name;
        this.AmountSpent = amount;
    }

    public String getName() {
        return SplitName;
    }

    public void setName(String name) {
        SplitName = name;
    }

    public Double getAmount() {
        return AmountSpent;
    }

    public void setAmount(Double amount) {
        AmountSpent = amount;
    }
}
