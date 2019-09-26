package com.hope.commonsense.Entities;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by User on 5/21/2017.
 */
public class Comment implements Serializable {

    private int id;
    private String title;
    private String text;
    private Calendar date;
    private int likeCount;
    private User user;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return user.getNickname() + "، "+user.getStatus();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
