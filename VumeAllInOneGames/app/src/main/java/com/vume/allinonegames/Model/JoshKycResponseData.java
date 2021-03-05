package com.vume.allinonegames.Model;

import com.google.gson.annotations.SerializedName;

public class JoshKycResponseData {

    @SerializedName("idType")
    private String idType;
    @SerializedName("idFile")
    private String idFileURL;
    @SerializedName("description")
    private String idDescription;
    @SerializedName("status")
    private int idApprovalStatus;

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdFileURL() {
        return idFileURL;
    }

    public void setIdFileURL(String idFileURL) {
        this.idFileURL = idFileURL;
    }

    public String getIdDescription() {
        return idDescription;
    }

    public void setIdDescription(String idDescription) {
        this.idDescription = idDescription;
    }

    public int getIdApprovalStatus() {
        return idApprovalStatus;
    }

    public void setIdApprovalStatus(int idApprovalStatus) {
        this.idApprovalStatus = idApprovalStatus;
    }

    public boolean isKYCApproved(){
        return getIdApprovalStatus()==0?false:true;
    }
}
