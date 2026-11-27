package com.tecnocampus.LS2.protube_back.dto;

// Data sent by the frontend to log in (POST /api/auth/login)
public class LoginRequest {

    private String email;
    private String password;

    // Empty constructor: needed by Jackson to read the JSON body
    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
