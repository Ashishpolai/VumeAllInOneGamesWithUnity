package com.vume.allinonegames.Model;

import retrofit2.http.Query;

public class JoshLoginRequestData {

    public final String username;
    public final String password;
    public final String install_key;

    public JoshLoginRequestData(String ousername, String opassword, @Query("install-key") String insatllkey) {
        install_key = insatllkey;
        username = ousername;
        password = opassword;
    }

    public String getmPassword() {
        return password;
    }

    public String getmUsername() {
        return username;
    }

    public String getmInstallKey() {
        return install_key;
    }
}
