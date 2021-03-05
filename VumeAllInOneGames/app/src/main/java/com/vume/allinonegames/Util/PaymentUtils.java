package com.vume.allinonegames.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PaymentUtils {
    public static final  String PAYTM_PAYMENTMETHOD = "Paytm";
    public static final String MOBIKWIK_PAYMENTMETHOD = "Mobikwik";
    public static final String RAZORPAY_PAYMENTMETHOD = "Razorpay";

    public static final String PAYTM_PAYMENTMETHOD_INSHORT = "pym";
    public static final String MOBIKWIK_PAYMENTMETHOD_INSHORT = "mwk";
    public static final String RAZORPAY_PAYMENTMETHOD_INSHORT = "rzp";

    public static final int STARTPAYTM_TRXN_REQUESTCODE = 9728;
    public static final int PAYMENT_SUCCESS_GO_TOLOBBY_TIMERINSECS = 10;

    public static final int TRANSACTION_TYPE_WITHDRAWAL = 4;
    public static final int TRANSACTION_TYPE_DEPOSIT = 3;
    public static final int TRANSACTION_TYPE_WINNINGS = 2;
    public static final int TRANSACTION_TYPE_WAGER = 1 ;
    public static final int TRANSACTION_TYPE_WITHDRAWAL_REVERSAL = 6;

    public static final int WITHDRAWAL_TRANSACTION_TYPE_INITIATED = 0;
    public static final int WITHDRAWAL_TRANSACTION_TYPE_INPROGRESS = 1;
    public static final int WITHDRAWAL_TRANSACTION_TYPE_ACCEPTED = 2;
    public static final int WITHDRAWAL_TRANSACTION_TYPE_REJECTED = -1 ;
    public static final int WITHDRAWAL_TRANSACTION_TYPE_CANCELLEDBYUSER = -2;

    public static String CURRENCY = "INR";

    //FOR STAGING
    public static String PAYTM_BASE_URL = "https://securegw-stage.paytm.in";
    //For Production
    //private static String paytmBaseUrl = "https://securegw.paytm.in";

    private static String currentPaymentMethod = PAYTM_PAYMENTMETHOD;

    public static String getCurrentPaymentMethod() {
        return currentPaymentMethod;
    }

    public static void setCurrentPaymentMethod(String currentPaymntMethod) {
        currentPaymentMethod = currentPaymntMethod;
    }

    public static String getCurrentPaymentMethodInShort(){
        switch (currentPaymentMethod){
            case PAYTM_PAYMENTMETHOD:
                return PAYTM_PAYMENTMETHOD_INSHORT;


            case MOBIKWIK_PAYMENTMETHOD:
                return MOBIKWIK_PAYMENTMETHOD_INSHORT;


            case RAZORPAY_PAYMENTMETHOD:
                return RAZORPAY_PAYMENTMETHOD_INSHORT;

        }
        return "";
    }


    public static String getmTxnTimeInFormat(final long mTxnTimestamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone currentTimeZone = cal.getTimeZone();
        String DATE_FORMAT_9 = "h:mm a dd MMMM yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_9);
        dateFormat.setTimeZone(currentTimeZone);
        Date txndate = new Date(mTxnTimestamp);
        return dateFormat.format(txndate);
    }

    public static boolean isWithdrawRequestCompleted(int withdrawTxnStatus){
        return (withdrawTxnStatus == WITHDRAWAL_TRANSACTION_TYPE_ACCEPTED || withdrawTxnStatus == WITHDRAWAL_TRANSACTION_TYPE_REJECTED ||
                withdrawTxnStatus == WITHDRAWAL_TRANSACTION_TYPE_CANCELLEDBYUSER);
    }
}
