package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class OffersResponseData {

    @SerializedName("offer_msg")
    private String mOfferMsg;
    @SerializedName("offer_validtill")
    private String mOfferTill;
    @SerializedName("offer_termsandconditions_url")
    private String mOfferTermsAndConditionsUrl;
    @SerializedName("offer_promocode")
    private String mOfferPromocode;

    public OffersResponseData(String mOfferMsg, String mOfferTill, String mOfferTermsAndConditionsUrl,
                              String mOfferPromocode) {
        this.mOfferMsg = mOfferMsg;
        this.mOfferTill = mOfferTill;
        this.mOfferTermsAndConditionsUrl = mOfferTermsAndConditionsUrl;
        this.mOfferPromocode = mOfferPromocode;
    }

    public String getmOfferMsg() {
        return mOfferMsg;
    }

    public void setmOfferMsg(String mOfferMsg) {
        this.mOfferMsg = mOfferMsg;
    }

    public String getmOfferTill() {
        return mOfferTill;
    }

    public void setmOfferTill(String mOfferTill) {
        this.mOfferTill = mOfferTill;
    }

    public String getmOfferTermsAndConditionsUrl() {
        return mOfferTermsAndConditionsUrl;
    }

    public void setmOfferTermsAndConditionsUrl(String mOfferTermsAndConditionsUrl) {
        this.mOfferTermsAndConditionsUrl = mOfferTermsAndConditionsUrl;
    }

    public String getmOfferPromocode() {
        return mOfferPromocode;
    }

    public void setmOfferPromocode(String mOfferPromocode) {
        this.mOfferPromocode = mOfferPromocode;
    }
}
