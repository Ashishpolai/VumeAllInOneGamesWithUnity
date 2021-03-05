package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshRazorpayTxnSuccessResponseData {

    @SerializedName("razorpay_payment_id")
    private String mRazorpayPaymentId;
    @SerializedName("razorpay_order_id")
    private String mRazorpayOrderId;
    @SerializedName("razorpay_signature")
    private String mRazorpaySignature;

    public JoshRazorpayTxnSuccessResponseData(String mRazorpayPaymentId, String mRazorpayOrderId, String mRazorpaySignature) {
        this.mRazorpayPaymentId = mRazorpayPaymentId;
        this.mRazorpayOrderId = mRazorpayOrderId;
        this.mRazorpaySignature = mRazorpaySignature;
    }

    public String getmRazorpayPaymentId() {
        return mRazorpayPaymentId;
    }

    public void setmRazorpayPaymentId(String mRazorpayPaymentId) {
        this.mRazorpayPaymentId = mRazorpayPaymentId;
    }

    public String getmRazorpayOrderId() {
        return mRazorpayOrderId;
    }

    public void setmRazorpayOrderId(String mRazorpayOrderId) {
        this.mRazorpayOrderId = mRazorpayOrderId;
    }

    public String getmRazorpaySignature() {
        return mRazorpaySignature;
    }

    public void setmRazorpaySignature(String mRazorpaySignature) {
        this.mRazorpaySignature = mRazorpaySignature;
    }
}
