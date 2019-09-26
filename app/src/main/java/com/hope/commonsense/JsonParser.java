package com.hope.commonsense;

import android.util.Log;

import com.hope.commonsense.Entities.AgeRange;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Category;
import com.hope.commonsense.Entities.Comment;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.SearchResult;
import com.hope.commonsense.Entities.SliderItem;
import com.hope.commonsense.Entities.Tag;
import com.hope.commonsense.Entities.User;
import com.hope.commonsense.Entities.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JsonParser {

    static String TAG = "JsonParser";

    public static ArrayList<Article> jsonToArticles(JSONArray arr) {
        ArrayList<Article> list = new ArrayList<>();
        try {
//            JSONArray arr = new JSONArray(jsonStr);

            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                int catId = 0;
                if(json.has("CategoryId")){
                    catId = json.getInt("CategoryId");
                }
                int likeCount = 0;
                if(json.has("LikeCount")){
                    likeCount = json.getInt("LikeCount");
                }
                int viewCount = 0;
                if(json.has("ViewCount")){
                    viewCount = json.getInt("ViewCount");
                }
                int commentCount = 0;
                if(json.has("CommentCount")){
                    commentCount = json.getInt("CommentCount");
                }
                String title = json.getString("Title");
                String image = json.getString("Image");
                String translator="";
                if(json.has("Translator")){
                    translator = json.getString("Translator");
                }
//                String field = json.getString("Field");
                ArrayList<AgeRange> ages = new ArrayList<>();
                if(json.has("Ages")){
                    ages = jsonToAge(json.getJSONArray("Ages"));
                }

                ArrayList<Tag> tags = new ArrayList<>();
                if(json.has("Ages")){
                    tags = jsonToTagArray(json.getJSONArray("Tags"));
                }



                Article article = new Article();
                article.setId(id);
                article.setTitle(title);
                article.setCatId(catId);
                article.setImage(image);
                article.setLikeCount(likeCount);
                article.setViewCount(viewCount);
                article.setTranslator(translator);
//                article.setField(field);
                article.setAges(ages);
                article.setTags(tags);
                article.setCommentCount(commentCount);

                list.add(article);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return list;
    }

    public static ArrayList<Question> jsonToQuestions(JSONArray arr) {
        ArrayList<Question> list = new ArrayList<>();
        try {
            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                int catId = 0;
                if(json.has("CategoryId")){
                    catId = json.getInt("CategoryId");
                }
                int likeCount = 0;
                if(json.has("LikeCount")){
                    likeCount = json.getInt("LikeCount");
                }
                int viewCount = 0;
                if(json.has("ViewCount")){
                    viewCount = json.getInt("ViewCount");
                }
                int commentCount = 0;
                if(json.has("CommentCount")){
                    commentCount = json.getInt("CommentCount");
                }
                String title = json.getString("Title");
                String answer = "";
                if(json.has("Answer")){
                    answer = json.getString("Answer");
                }

                String translator="";
                if(json.has("Translator")){
                    translator = json.getString("Translator");
                }
//                String field = json.getString("Field");
//                int salary = json.getInt("Salary");
                ArrayList<AgeRange> ages = new ArrayList<>();
                if(json.has("Ages")){
                    ages = jsonToAge(json.getJSONArray("Ages"));
                }

                ArrayList<Tag> tags = new ArrayList<>();
                if(json.has("Ages")){
                    tags = jsonToTagArray(json.getJSONArray("Tags"));
                }


                Question question = new Question();
                question.setId(id);
                question.setTitle(title);
                question.setAnswer(answer);
                question.setCatId(catId);
                question.setLikeCount(likeCount);
                question.setViewCount(viewCount);
                question.setCommentCount(commentCount);
                question.setTranslator(translator);
//                question.setField(field);
                question.setAges(ages);
                question.setTags(tags);

                list.add(question);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return list;
    }

    public static ArrayList<Video> jsonToVideos(JSONArray arr) {
        ArrayList<Video> list = new ArrayList<>();
        try {
//            JSONArray arr = new JSONArray(jsonStr);

            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                int catId = 0;
                if(json.has("CategoryId")){
                    catId = json.getInt("CategoryId");
                }
                int likeCount = 0;
                if(json.has("LikeCount")){
                    likeCount = json.getInt("LikeCount");
                }
                int viewCount = 0;
                if(json.has("ViewCount")){
                    viewCount = json.getInt("ViewCount");
                }
                int commentCount = 0;
                if(json.has("CommentCount")){
                    commentCount = json.getInt("CommentCount");
                }
                String title = json.getString("Title");
//                String field = json.getString("Field");
//                int salary = json.getInt("Salary");
                ArrayList<AgeRange> ages = new ArrayList<>();
                if(json.has("Ages")){
                    ages = jsonToAge(json.getJSONArray("Ages"));
                }

                ArrayList<Tag> tags = new ArrayList<>();
                if(json.has("Ages")){
                    tags = jsonToTagArray(json.getJSONArray("Tags"));
                }

                String image = "";
                if(json.has("Image")){
                    image = json.getString("Image");
                }

                String link = "";
                if(json.has("Link")){
                    link = json.getString("Link");
                }

                String time = "";
                if(json.has("Duration")){
                    time = json.getString("Duration");
                }

                String date = "";
                if(json.has("Date")){
                    date = json.getString("Date");
                }

                Video video = new Video();
                video.setId(id);
                video.setTitle(title);
                video.setCatId(catId);
                video.setImage(image);
                video.setLikeCount(likeCount);
                video.setViewCount(viewCount);
                video.setCommentCount(commentCount);
                video.setLink(link);
                video.setTime(time);
//                video.setField(field);
//                video.setDate(Utils.stringToDate(date));
                video.setDate(date);
                video.setAges(ages);
                video.setTags(tags);

                list.add(video);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return list;
    }

    public static ArrayList<SliderItem> jsonToSliderItems(JSONArray arr) {
        ArrayList<SliderItem> list = new ArrayList<>();
        try {

            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                String title = json.getString("Title");
                String image = json.getString("Image");
                String link = json.getString("Link");
                int articleId = json.getInt("ArticleId");
                int questionId = json.getInt("QuestionId");
                int videoId = json.getInt("VideoId");
                int type = json.getInt("Type");
                int likeCount = json.getInt("LikeCount");
                int commentCount = json.getInt("CommentCount");
                int viewCount = 0;
                if(!json.getString("ViewCount").equals("null")){
                    viewCount = json.getInt("ViewCount");
                }
                int categoryId = json.getInt("CategoryId");
                String answer = json.getString("Answer");
                String translator = json.getString("Translator");
                ArrayList<Tag> tags = jsonToTagArray(json.getJSONArray("Tags"));
//                ArrayList<AgeRange> ages = jsonToAge

                SliderItem sliderItem = new SliderItem();
                sliderItem.setTitle(title);
                sliderItem.setImage(image);
                sliderItem.setLink(link);
                sliderItem.setArticleId(articleId);
                sliderItem.setQuestionId(questionId);
                sliderItem.setVideoId(videoId);
                sliderItem.setType(type);
                sliderItem.setCategoryId(categoryId);
                sliderItem.setLikeCount(likeCount);
                sliderItem.setViewCount(viewCount);
                sliderItem.setCommentCount(commentCount);
                sliderItem.setAnswer(answer);
                sliderItem.setTranslator(translator);
                sliderItem.setTags(tags);

                list.add(sliderItem);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return list;
    }

    public static ArrayList<Comment> jsonToComments(JSONArray arr) {
        ArrayList<Comment> list = new ArrayList<>();
        try {

            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                String text = json.getString("Text");
                String date = json.getString("Date");
                int likeCount = json.getInt("LikeCount");
                User user = jsonToUser(json.getJSONObject("User"));

                Comment comment = new Comment();
                comment.setId(id);
                comment.setText(text);
                comment.setDate(Utils.stringToDate(date));
                comment.setLikeCount(likeCount);
                comment.setUser(user);

                list.add(comment);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return list;
    }

    public static User jsonToUser(JSONObject json) {
        User user = new User();
        try {
            String status = json.getString("Status");
            String image = json.getString("Image");
            String email = "";
            if(json.has("Email")){
                email = json.getString("Email");
            }

            String nickname = "";
            if(json.has("Nickname")){
                nickname = json.getString("Nickname");
            } else if(json.has("Nicknam")){
                nickname = json.getString("Nicknam");
            }

            String token = "";
            if(json.has("Token")){
                token = json.getString("Token");
            }

            String username = "";
            if(json.has("UserName")){
                username = json.getString("UserName");
            }

            if(image == null || image.equals("null")){
                image = "";
            }

            if(status == null || status.equals("null")){
                status = "";
            }

            if(email == null || email.equals("null")){
                email = "";
            }

            user.setToken(token);
            user.setUsername(username);
            user.setNickname(nickname);
            user.setStatus(status);
            user.setImage(image);
            user.setEmail(email);

        } catch (JSONException e){
            e.printStackTrace();
        }
        return user;
    }

    public static ArrayList<Category> jsonToCategory(JSONArray arr) {
        ArrayList<Category> list = new ArrayList<>();
        try {

            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                String title = json.getString("Title");
                String image = json.getString("Image");
                int articleCount = json.getInt("ArticleCount");
                int questionCount = json.getInt("QuestionCount");
                int videoCount = json.getInt("VideoCount");

                Category category = new Category();
                category.setId(id);
                category.setTitle(title);
                category.setArticleCount(articleCount);
                category.setQuestionCount(questionCount);
                category.setVideoCount(videoCount);

                list.add(category);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage() + "");
        }

        return list;
    }

    public static Question jsonToQuestion(JSONObject json) {
        Question question = new Question();
        try {
            int id = json.getInt("Id");
            int catId = 0;
            if(json.has("CategoryId")){
                catId = json.getInt("CategoryId");
            }
            int likeCount = json.getInt("LikeCount");
//                int viewCount = json.getInt("ViewCount");
            int commentCount = json.getInt("CommentCount");
//                int salary = json.getInt("Salary");
            String title = json.getString("Title");
            String answer = json.getString("Answer");
            String translator = json.getString("Translator");
            String field = json.getString("Field");
            ArrayList<AgeRange> ages = jsonToAge(json.getJSONArray("Ages"));
            ArrayList<Tag> tags = jsonToTagArray(json.getJSONArray("Tags"));

            question.setId(id);
            question.setTitle(title);
            question.setAnswer(answer);
            question.setCatId(catId);
            question.setLikeCount(likeCount);
//                question.setViewCount(viewCount);
            question.setCommentCount(commentCount);
//                question.setSalary(salary);
            question.setTranslator(translator);
            question.setField(field);
            question.setAges(ages);
            question.setTags(tags);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return question;
    }

    public static Article jsonToArticle(JSONObject json){
        Article article = new Article();
        try{
            int id = json.getInt("Id");
            int catId = 0;
            if(json.has("CategoryId")){
                catId = json.getInt("CategoryId");
            }
            int likeCount = 0;
            if(json.has("LikeCount")){
                catId = json.getInt("LikeCount");
            }
            int viewCount = 0;
            if(json.has("ViewCount")){
                catId = json.getInt("ViewCount");
            }
            int commentCount = json.getInt("CommentCount");
            String content = json.getString("Content");
            String title = json.getString("Title");
            String image = json.getString("Image");
            String translator = json.getString("Translator");
            String field = json.getString("Field");
            ArrayList<AgeRange> ages = jsonToAge(json.getJSONArray("Ages"));
            ArrayList<Tag> tags = jsonToTagArray(json.getJSONArray("Tags"));


            article.setId(id);
            article.setTitle(title);
            article.setContent(content);
            article.setCatId(catId);
            article.setImage(image);
            article.setLikeCount(likeCount);
            article.setViewCount(viewCount);
            article.setCommentCount(commentCount);
            article.setTranslator(translator);
            article.setField(field);
            article.setAges(ages);
            article.setTags(tags);
        }  catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }
        return article;
    }

    public static Video jsonToVideo(JSONObject json){
        Video video = new Video();
        try{
            int id = json.getInt("Id");
            int catId = 0;
            if(json.has("CategoryId")){
                catId = json.getInt("CategoryId");
            }
            int likeCount = json.getInt("LikeCount");
            int viewCount = json.getInt("ViewCount");
            int commentCount = json.getInt("CommentCount");
            String title = json.getString("Title");
            String image = json.getString("Image");
            String link = json.getString("Link");
            String duration = json.getString("Duration");
            String field = json.getString("Field");
            String date = json.getString("Date");
            ArrayList<AgeRange> ages = jsonToAge(json.getJSONArray("Ages"));
            ArrayList<Tag> tags = jsonToTagArray(json.getJSONArray("Tags"));

            video.setId(id);
            video.setTitle(title);
            video.setCatId(catId);
            video.setImage(image);
            video.setLikeCount(likeCount);
            video.setViewCount(viewCount);
            video.setCommentCount(commentCount);
            video.setLink(link);
            video.setTime(duration);
            video.setField(field);
            video.setDate(date);
            video.setAges(ages);
            video.setTags(tags);

        }  catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }
        return video;
    }

    public static ArrayList<SearchResult> jsonToSearchResult(JSONArray arr){
        ArrayList<SearchResult> list = new ArrayList<>();
        try {
            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                int type = json.getInt("Type");
                String title = json.getString("Title");
                String image = json.getString("Image");

                SearchResult searchResult = new SearchResult();
                searchResult.setId(id);
                searchResult.setType(type);
                searchResult.setTitle(title);
                searchResult.setImage(image);
                list.add(searchResult);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return list;
    }

    public static ArrayList<Category> jsonToCategoryTags(JSONArray arr) {
        ArrayList<Category> list = new ArrayList<>();
        try {
            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                String title = json.getString("Title");
                ArrayList<Tag> tags = jsonToTag(json.getJSONArray("Tags"));

                Category category = new Category();
                category.setId(id);
                category.setTitle(title);
                category.setTags(tags);
                list.add(category);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }
        return list;
    }

    private static ArrayList<Tag> jsonToTag(JSONArray arr) {
        ArrayList<Tag> list = new ArrayList<>();
        try {
            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                String title = json.getString("Title");

                Tag tag = new Tag();
                tag.setId(id);
                tag.setTitle(title);
                list.add(tag);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }
        return list;
    }

    private static ArrayList<AgeRange> jsonToAge(JSONArray ages) {
        return null;
    }

    private static ArrayList<Tag> jsonToTagArray(JSONArray arr) {
        ArrayList<Tag> list = new ArrayList<>();
        try {
            for(int i=0; i<arr.length(); i++){
                JSONObject json = arr.getJSONObject(i);
                int id = json.getInt("Id");
                String title = json.getString("Title");

                Tag tag = new Tag();
                tag.setId(id);
                tag.setTitle(title);
                list.add(tag);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        }

        return list;
    }

    public static String agesToJson(ArrayList<AgeRange> ages){
        StringBuilder body = new StringBuilder();
        body.append("[");

        for(AgeRange age : ages){
            body.append("{");
            body.append("\"Id\": ");
            body.append(age.getId());
            body.append("},");
         /*   body.append(age.getId());
            body.append(",");*/
        }
        if(body.toString().contains(",")){
            body.deleteCharAt(body.lastIndexOf(","));
        }
        body.append("]");
        return body.toString();
    }

}
