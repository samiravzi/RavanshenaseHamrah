package com.hope.commonsense.Entities;

import android.content.Intent;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.hope.commonsense.Activities.ArticleActivity;
import com.hope.commonsense.Activities.MainActivity;
import com.hope.commonsense.Activities.QuestionActivity;
import com.hope.commonsense.Activities.VideoActivity;
import com.hope.commonsense.Utils;

import java.util.ArrayList;

/**
 * Created by User on 5/21/2017.
 */
public class SliderItem {

    private MainActivity mainActivity;

    private String title;
    private String image;
    private String link;
    private int articleId;
    private int questionId;
    private int videoId;
    private int type;
    private int categoryId;
    private int likeCount;
    private int commentCount;
    private int viewCount;
    private String translator;
    private String answer;
    private ArrayList<Tag> tags;
    private ArrayList<AgeRange> ages;
    public BaseSliderView.OnSliderClickListener onSliderClickListener;

    final int ARTICLE = 1;
    final int QUESTION = 2;
    final int VIDEO = 3;
    final int WEB_SITE = 4;



    public SliderItem() {
        mainActivity = MainActivity.mainActivity;
        ages = new ArrayList<>();
        tags = new ArrayList<>();
        onSliderClickListener = new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                showItem();
            }
        };
    }

//--------------------------------------------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getType(){
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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


// -------------------------------------------------------------------------------------------------
    private void showItem(){
        switch (type){
            case WEB_SITE: Utils.showWebPage(mainActivity, link); break;
            case ARTICLE:  MainActivity.mainActivity.startActivity(new Intent(MainActivity.mainActivity, ArticleActivity.class).putExtra("article", this.toArticle())); break;
            case QUESTION:  MainActivity.mainActivity.startActivity(new Intent(MainActivity.mainActivity, QuestionActivity.class).putExtra("question", this.toQuestion())); break;
            case VIDEO:  MainActivity.mainActivity.startActivity(new Intent(MainActivity.mainActivity, VideoActivity.class).putExtra("video", this.toVideo())); break;
        }
    }

    private Video toVideo() {
        Video video = new Video();
        video.setId(videoId);
        video.setTitle(title);
        video.setImage(image);
        video.setLikeCount(likeCount);
        video.setCommentCount(commentCount);
        video.setViewCount(viewCount);
        video.setLink(link);
        video.setCatId(categoryId);
        video.setTags(tags);
        return video;
    }

    private Question toQuestion() {
        Question question = new Question();
        question.setId(questionId);
        question.setTitle(title);
        question.setLikeCount(likeCount);
        question.setCommentCount(commentCount);
        question.setViewCount(viewCount);
        question.setAnswer(answer);
        question.setCatId(categoryId);
        question.setTranslator(translator);
        question.setTags(tags);
        return question;
    }

    private Article toArticle() {
        Article article = new Article();
        article.setId(articleId);
        article.setTitle(title);
        article.setImage(image);
        article.setLikeCount(likeCount);
        article.setCommentCount(commentCount);
        article.setViewCount(viewCount);
        article.setCatId(categoryId);
        article.setTranslator(translator);
        article.setTags(tags);
        return article;
    }

}
