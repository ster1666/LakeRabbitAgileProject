package com.example.lakebaikal;

public class PaymentHistory {

    public String paymentTimestamp;
    public Integer paymentCost;

    public PaymentHistory(String payTimestamp, Integer payCost){
        this.paymentTimestamp = payTimestamp;
        this.paymentCost = payCost;

    }
}
