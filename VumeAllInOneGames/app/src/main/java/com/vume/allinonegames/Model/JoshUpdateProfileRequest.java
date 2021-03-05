package com.vume.allinonegames.Model;

public class JoshUpdateProfileRequest {

    public final String first_name;
    public final String last_name;
    public final String username;
    public final String email;

    public JoshUpdateProfileRequest(String first_name, String last_name, String username, String email) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
