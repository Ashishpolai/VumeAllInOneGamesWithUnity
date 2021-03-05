package com.vume.allinonegames.Model;

import retrofit2.http.Query;

public class JoshChangePassRequestData {

    public final String old_password;
    public final String new_password;

    public JoshChangePassRequestData(@Query("old_password") String oOldPassword, @Query("new_password") String oNewPassword) {
        old_password = oOldPassword;
        new_password = oNewPassword;
    }

    public String getOldPassword() {
        return old_password;
    }

    public String getNewPassword() {
        return new_password;
    }
}
