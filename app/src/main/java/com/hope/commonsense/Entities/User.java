package com.hope.commonsense.Entities;

import java.io.Serializable;

/**
 * Created by User on 5/23/2017.
 */
public class User implements Serializable {

    private String token;
    private String username; // phone number
    private String email;
    private String nickname;
    private String status;
    private String image;
    private String password;

    public User(){
    }

    public User(String token, String username, String nickname, String password) {
        this.token = token;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
