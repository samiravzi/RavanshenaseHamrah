package com.hope.commonsense.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by User on 5/21/2017.
 */
public class Question implements Serializable{

    private int id;
    private int catId;
    private String title;
    private String answer;
    private int salary;
    private String Field;
    private int likeCount;
    private int viewCount;
    private int commentCount;
    private String translator;
    private Calendar date;
    private ArrayList<Comment> comments;
    private ArrayList<Tag> tags;
    private ArrayList<AgeRange> ages;



    public Question(){
        comments = new ArrayList<>();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getField() {
        return Field;
    }

    public void setField(String field) {
        Field = field;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public ArrayList<Comment> getComments() {
        if(comments == null){
            return new ArrayList<>();
        }
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComments(ArrayList<Comment> comments){
        this.comments.addAll(comments);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }


    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public ArrayList<AgeRange> getAges() {
        return ages;
    }

    public void setAges(ArrayList<AgeRange> ages) {
        this.ages = ages;
    }
}
