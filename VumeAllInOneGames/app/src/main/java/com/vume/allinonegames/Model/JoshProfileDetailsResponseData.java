package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JoshProfileDetailsResponseData {

    @SerializedName("address")
    private String mAddress;
    @SerializedName("bankAcct")
    private String mBankAccountNo;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("isBankAcctVerified")
    private boolean mIsBankAcctVerified;
    @SerializedName("updatedAt")
    private String mUpdatedAt;
    @SerializedName("sex")
    private String mSex;
    @SerializedName("address3")
    private String mAddressThree;
    @SerializedName("city")
    private String mCity;
    @SerializedName("firstName")
    private String mFirstName;
    @SerializedName("createdAt")
    private String mCreatedAt;
    @SerializedName("state")
    private String mState;
    @SerializedName("imageUrl")
    private String mProfilePicUrl;
    @SerializedName("currency")
    private String mCurrency;
    @SerializedName("dateOfBirth")
    private String mDateOfBirth;
    @SerializedName("kycs")
    private List<String> mKycs;
    @SerializedName("address2")
    private String mAddressTwo;
    @SerializedName("lastName")
    private String mLastname;
    @SerializedName("pincode")
    private String mPinCode;
    @SerializedName("lastUpdated")
    private String mLastUpdated;
    @SerializedName("userId")
    private String mUserID;
    @SerializedName("isKycVerified")
    private boolean mIsKycVerified;
    @SerializedName("phoneNumber")
    private String mPhoneNumber;
    @SerializedName("country")
    private String mCountry;

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public List<String> getmKycs() {
        return mKycs;
    }

    public void setmKycs(List<String> mKycs) {
        this.mKycs = mKycs;
    }

    public boolean ismIsBankAcctVerified() {
        return mIsBankAcctVerified;
    }

    public void setmIsBankAcctVerified(boolean mIsBankAcctVerified) {
        this.mIsBankAcctVerified = mIsBankAcctVerified;
    }

    public boolean ismIsKycVerified() {
        return mIsKycVerified;
    }

    public void setmIsKycVerified(boolean mIsKycVerified) {
        this.mIsKycVerified = mIsKycVerified;
    }

    public String getmProfilePicUrl() {
        return mProfilePicUrl;
    }

    public void setmProfilePicUrl(String mProfilePicUrl) {
        this.mProfilePicUrl = mProfilePicUrl;
    }

    public String getmBankAccountNo() {
        return mBankAccountNo;
    }

    public void setmBankAccountNo(String mBankAccountNo) {
        this.mBankAccountNo = mBankAccountNo;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmUpdatedAt() {
        return mUpdatedAt;
    }

    public void setmUpdatedAt(String mUpdatedAt) {
        this.mUpdatedAt = mUpdatedAt;
    }

    public String  getmSex() {
        return mSex;
    }

    public void setmSex(String  mSex) {
        this.mSex = mSex;
    }

    public String getmAddressThree() {
        return mAddressThree;
    }

    public void setmAddressThree(String mAddressThree) {
        this.mAddressThree = mAddressThree;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(String mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public String getmState() {
        return mState;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public String getmCurrency() {
        return mCurrency;
    }

    public void setmCurrency(String mCurrency) {
        this.mCurrency = mCurrency;
    }

    public String getmDateOfBirth() {
        return mDateOfBirth;
    }

    public void setmDateOfBirth(String mDateOfBirth) {
        this.mDateOfBirth = mDateOfBirth;
    }

    public String getmAddressTwo() {
        return mAddressTwo;
    }

    public void setmAddressTwo(String mAddressTwo) {
        this.mAddressTwo = mAddressTwo;
    }

    public String getmLastname() {
        return mLastname;
    }

    public void setmLastname(String mLastname) {
        this.mLastname = mLastname;
    }

    public String getmPinCode() {
        return mPinCode;
    }

    public void setmPinCode(String mPinCode) {
        this.mPinCode = mPinCode;
    }

    public String getmLastUpdated() {
        return mLastUpdated;
    }

    public void setmLastUpdated(String mLastUpdated) {
        this.mLastUpdated = mLastUpdated;
    }

    public String getmUserID() {
        return mUserID;
    }

    public void setmUserID(String mUserID) {
        this.mUserID = mUserID;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }
}
