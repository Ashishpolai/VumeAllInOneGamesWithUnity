package com.vume.allinonegames.Model;

import retrofit2.http.Query;

public class JoshRegisterRequestData {

    private String username;
    private String password;
    private String accessToken;
    private String credential;
    private String install_key;

    public JoshRegisterRequestData(String useridtoken, String ocredential, @Query("install-key") String installkey, String ousername, String opassword) {
        username = ousername;
        password = opassword;
        accessToken = useridtoken;
        credential = ocredential;
        install_key = installkey;
    }

    public String getmUsername() {
        return username;
    }

    public void setmUsername(String mUsername) {
        this.username = mUsername;
    }

    public String getmPassword() {
        return password;
    }

    public void setmPassword(String mPassword) {
        this.password = mPassword;
    }

    public String getmUserIdToken() {
        return accessToken;
    }

    public void setmUserIdToken(String mUserIdToken) {
        this.accessToken = mUserIdToken;
    }

    public String getmCredential() {
        return credential;
    }

    public void setmCredential(String mCredential) {
        this.credential = mCredential;
    }

    public String getmInstallKey() {
        return install_key;
    }

    public void setmInstallKey(String mInstallKey) {
        this.install_key = mInstallKey;
    }
}
