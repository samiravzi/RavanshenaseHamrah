package com.hope.commonsense.Entities;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by User on 5/21/2017.
 */
public class Article implements Serializable{

    private int id;
    private int catId;
    private String title;
    private String content;
    private String image;
    private String imagePath;
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


    public Article(){
        comments = new ArrayList<>();
        ages = new ArrayList<>();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }


    public void addComments(ArrayList<Comment> comments){
        this.comments.addAll(comments);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<AgeRange> getAges() {
        return ages;
    }

    public void setAges(ArrayList<AgeRange> ages) {
        this.ages = ages;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
