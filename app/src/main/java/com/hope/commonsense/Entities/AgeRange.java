package com.hope.commonsense.Entities;

import java.io.Serializable;

/**
 * Created by User on 5/22/2017.
 */
public class AgeRange implements Serializable {

    private int id;
    private String title;
    private boolean isSelected = false;
    private int priority;
    public static String allAgesTitle = "همه سنین";



    public AgeRange(int id, String title, int priority){
        this.id = id;
        this.title = title;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
