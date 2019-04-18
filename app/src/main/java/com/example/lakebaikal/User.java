package com.example.lakebaikal;

public class User {

    public String userId, fullName, email;
    public Integer funds;

    public  User(String id, String name, String email){
        this.userId = id;
        this.fullName = name;
        this.email = email;
        this.funds = 0;
    }
}
