package com.vume.allinonegames.Model;

public class JoshInitiateTransactionRequest {

    public final double amount;
    public final String uuid;
    public final String paymentGateway;

    public JoshInitiateTransactionRequest(double mAmount, String mUUID, String paymentGatewayS) {
        amount = mAmount;
        uuid = mUUID;
        paymentGateway = paymentGatewayS;
    }

    public double getAmount() {
        return amount;
    }

    public String getUuid() {
        return uuid;
    }
}
