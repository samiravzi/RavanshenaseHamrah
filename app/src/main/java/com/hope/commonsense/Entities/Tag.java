package com.hope.commonsense.Entities;

import java.io.Serializable;

/**
 * Created by User on 5/21/2017.
 */
public class Tag implements Serializable{

    private int id;
    private String title;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return "#"+title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
