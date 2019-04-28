package com.example.lakebaikal;

public class User {

    public String userId, fullName, email, btaddr, timestamp,lastPayed;
    public Integer balance,passes;

    public  User(String id, String name, String email, String addr, Integer funds){
        this.userId = id;
        this.fullName = name;
        this.email = email;
        this.balance = funds;
        this.btaddr = addr;
        this.timestamp = " ";
        this.lastPayed= " ";
        this.passes = 0;
    }
}
