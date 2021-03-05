package com.vume.allinonegames.Model;

public class JoshResetPasswordRequestData {

    public final String password;
    private final String accessToken;
    private final String credential;
    private final String username;

    public JoshResetPasswordRequestData(String password, String accessToken, String credential, String username) {
        this.password = password;
        this.accessToken = accessToken;
        this.credential = credential;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getCredential() {
        return credential;
    }

    public String getUsername() {
        return username;
    }
}
