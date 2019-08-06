package com.sas.cashier.Data;

public class User {

    public User()
    {}

    public User(String email ,  String budget , Double expenditure)
    {
        this.email=email;
        this.budget=budget;
        this.expenditure=expenditure;
    }
    public String email;



    public String budget;

    public Double expenditure;

    public String getEmail()
    {
        return this.email;
    }
    public String getBudget()
    {
        return this.budget;
    }


}
