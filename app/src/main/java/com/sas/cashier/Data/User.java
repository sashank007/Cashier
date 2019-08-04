package com.sas.cashier.Data;

public class User {

    public User()
    {}

    public User(String email ,  String budget)
    {
        this.email=email;
        this.budget=budget;
    }
    public String email;



    public String budget;

    public String getEmail()
    {
        return this.email;
    }
    public String getBudget()
    {
        return this.budget;
    }


}
