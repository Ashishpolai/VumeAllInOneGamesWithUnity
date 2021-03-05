package com.vume.allinonegames.Model;

public class JoshUpdateUsernameEmailRequest {

     public final String username;
     public final String email;

    public JoshUpdateUsernameEmailRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
