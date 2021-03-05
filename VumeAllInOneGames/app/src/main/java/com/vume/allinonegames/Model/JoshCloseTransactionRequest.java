package com.vume.allinonegames.Model;

import retrofit2.http.Query;

public class JoshCloseTransactionRequest {

    public final String razorpay_payment_id;
    public final String razorpay_order_id;
    public final String razorpay_signature;
    public final String paymentGateway;

    public JoshCloseTransactionRequest(@Query("razorpay_payment_id")  String razorpay_payment_id,
                                       @Query("razorpay_order_id") String razorpay_order_id,
                                       @Query("razorpay_signature") String razorpay_signature, String paymentGateway) {
        this.razorpay_payment_id = razorpay_payment_id;
        this.razorpay_order_id = razorpay_order_id;
        this.razorpay_signature = razorpay_signature;
        this.paymentGateway = paymentGateway;
    }

    public String getRazorpay_payment_id() {
        return razorpay_payment_id;
    }

    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public String getRazorpay_signature() {
        return razorpay_signature;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }
}
