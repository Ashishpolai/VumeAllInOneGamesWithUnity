package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshOffersResponseData {

    public static final String OFFERTYPE_REFERRAL = "referral";
    public static final String OFFERTYPE_DEPOSIT = "deposit";

    @SerializedName("id")
    private String mId;
    @SerializedName("code")
    private String mOfferCode;
    @SerializedName("type")
    private String mOfferType;
    @SerializedName("expiresAt")
    private String mOfferExpiresAt;
    @SerializedName("title")
    private String mOfferTitle;
    @SerializedName("desc")
    private String mOfferDescription;
    @SerializedName("tncUrl")
    private String mTermsAndConditionsUrl;

    public String getmId() {
        return mId;
    }

    public String getmOfferCode() {
        return mOfferCode;
    }

    public String getmOfferType() {
        return mOfferType;
    }

    public String getmOfferExpiresAt() {
        return mOfferExpiresAt;
    }

    public String getmOfferTitle() {
        return mOfferTitle;
    }

    public String getmOfferDescription() {
        return mOfferDescription;
    }

    public String getmTermsAndConditionsUrl() {
        return mTermsAndConditionsUrl;
    }
}
