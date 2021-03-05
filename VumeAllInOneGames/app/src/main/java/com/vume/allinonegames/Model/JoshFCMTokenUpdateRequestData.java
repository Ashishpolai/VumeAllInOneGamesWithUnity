package com.vume.allinonegames.Model;

import retrofit2.http.Query;

public class JoshFCMTokenUpdateRequestData {

    public final String mIdType;
    public final String mIdValue;
    public final String install_key;

    public JoshFCMTokenUpdateRequestData(String idType, String idValue, @Query("install-key") String insatllkey) {
        install_key = insatllkey;
        mIdType = idType;
        mIdValue = idValue;
    }

    public String getmIdType() {
        return mIdType;
    }

    public String getmIdValue() {
        return mIdValue;
    }

    public String getInstall_key() {
        return install_key;
    }
}
