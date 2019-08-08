package com.sas.cashier.Data;

public class User {

    public User()
    {}

    public User(String email ,  Double budget , Double expenditure)
    {
        this.email=email;
        this.budget=budget;
        this.expenditure=expenditure;
    }
    public String email;



    public Double budget;

    public Double expenditure;

    public String getEmail()
    {
        return this.email;
    }
    public Double getBudget()
    {
        return this.budget;
    }

    public Double getExpenditure()
    {
        return this.expenditure;
    }


}
