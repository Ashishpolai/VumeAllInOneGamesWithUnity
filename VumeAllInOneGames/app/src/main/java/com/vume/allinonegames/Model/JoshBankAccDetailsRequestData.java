package com.vume.allinonegames.Model;

public class JoshBankAccDetailsRequestData {

    public final String accountHolderName;
    public final String bankCode;
    public final String accountNumber;
    public final String ifsc;
    public final String branch;

    public JoshBankAccDetailsRequestData(String oAccountHolderName, String oBankCode, String oAccNo, String oIfsc, String oBranch) {
        accountHolderName = oAccountHolderName;
        bankCode = oBankCode;
        accountNumber = oAccNo;
        ifsc = oIfsc;
        branch = oBranch;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIfsc() {
        return ifsc;
    }

    public String getBranch() {
        return branch;
    }
}
