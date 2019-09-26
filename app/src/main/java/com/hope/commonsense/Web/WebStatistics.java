package com.hope.commonsense.Web;

/**
 * Created by User on 4/30/2017.
 */
public class WebStatistics {

    public static String WS_URL = "http://Commonsense.dekopazh.ir";
    public static String URL_GET_ARTICLES = WS_URL + "/api/V1/GetArticles";
    public static String URL_GET_QUESTIONS= WS_URL + "/api/V1/GetQuestions";
    public static String URL_GET_VIDEOS = WS_URL + "/api/V1/GetVideos";
    public static String URL_GET_SLIDER_CONTENTS = WS_URL + "/api/V1/GetSliderContent";
    public static String URL_GET_RELATED = WS_URL + "/api/V1/GetRelated";
    public static String URL_GET_COMMENTS = WS_URL + "/api/V1/GetComments";
    public static String URL_GET_CATEGORIES= WS_URL + "/api/V1/GetCategories";
    public static String URL_GET_ARTICLE_BY_ID= WS_URL + "/api/V1/GetArticleById";
    public static String URL_GET_QUESTION_BY_ID= WS_URL + "/api/V1/GetQuestionById";
    public static String URL_GET_VIDEO_BY_ID= WS_URL + "/api/V1/GetVideoById";
    public static String URL_LIKE= WS_URL + "/api/V1/LikeSubject";
    public static String URL_COMMENT= WS_URL + "/api/V1/CommentSubject";
    public static String URL_LOGIN= WS_URL + "/api/V1/LoginUser";
    public static String URL_REGISTER_USER= WS_URL + "/api/V1/RegisterUser";
    public static String URL_SEARCH_SUBJECT= WS_URL + "/api/V1/SearchSubject";
    public static String URL_EDIT_PROFILE= WS_URL + "/api/V1/EditProfile";
    public static String URL_GET_CATEGORIES_WITH_TAGS = WS_URL + "/api/V1/GetCategoriesWithTags";
    public static String URL_GET_USER_BY_ID = WS_URL + "/api/V1/GetUserByToken";
    public static String URL_DELETE_COMMENTS = WS_URL + "/api/V1/DeleteComment";





    public static String KEY_URL = "url";
    public static String KEY_TOKEN = "Token";
    public static String KEY_ID = "Id";
    public static String KEY_INDEX = "Index";
    public static String KEY_LIMIT = "Limit";
    public static String KEY_TYPE = "Type";
    public static String KEY_CATEGORY_ID = "CatId";
    public static String KEY_TAG_ID = "TagId";
    public static String KEY_ORDER_BY = "OrderBy";
    public static String KEY_FIELD = "Feild";
    public static String KEY_SALARY = "Salary";
    public static String KEY_MIN_AGE = "MinAge";
    public static String KEY_MAX_AGE = "MaxAge";
    public static String KEY_ARTICLE_ID = "ArticleId";
    public static String KEY_QUESTION_ID = "QuestionId";
    public static String KEY_VIDEO_ID = "VideoId";
    public static String KEY_TEXT = "Text";
    public static String KEY_USERNAME = "Username";
    public static String KEY_PASSWORD = "Password";
    public static String KEY_NICKNAME = "Nickname";
    public static String KEY_STATUS = "Status";
    public static String KEY_IMAGE = "Image";
    public static String KEY_TYPE_TO_GET = "TypeToGet";
    public static String KEY_AGES = "Ages";
    public static String KEY_COMMENT_ID = "CommentId";


    public static int HOME_ITEMS_LIMIT = 10;
    public static int HOME_SLIDER_ITEMS_COUNT = 5;
    public static int COMMENT_LIMIT = 15;

    public static int TYPE_ARTICLE = 1;
    public static int TYPE_QUESTION = 2;
    public static int TYPE_VIDEO = 3;

    public static String TYPE_BY_CATEGORY = "1";
    public static String TYPE_BY_TAG = "2";
    public static String TYPE_MOST_VISITED = "6";
    public static String TYPE_FEATURED = "7";

    public static String ORDER_BY_ID =  "0";
    public static String ORDER_BY_MOST_VISITED = "1";
    public static String ORDER_BY_DATE_DES = "2";
    public static String ORDER_BY_DATE_ASC = "3";
    public static String ORDER_BY_AGE_DES = "4";
    public static String ORDER_BY_AGE_ASC = "5";
}
