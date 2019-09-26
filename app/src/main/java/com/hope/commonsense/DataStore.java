package com.hope.commonsense;

import com.hope.commonsense.Entities.AgeRange;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Category;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.User;
import com.hope.commonsense.Entities.Video;

import java.util.ArrayList;

/**
 * Created by User on 5/22/2017.
 */
public class DataStore {

    public static String PREFERENCES_NAME = "prefs";
    public static String PREFERENCES_IS_USER_LOGGED_IN = "isUserRegistered";
    public static String PREFERENCES_KEY_TOKEN = "token";
    public static String PREFERENCES_KEY_USERNAME = "username";
    public static String PREFERENCES_KEY_PASSWORD = "password";
    public static String PREFERENCES_KEY_NICKNAME = "nickname";
    public static String PREFERENCES_KEY_STATUS = "status";
    public static String PREFERENCES_KEY_IMAGE = "image";
    public static String PREFERENCES_KEY_EMAIL = "email";



    public static ArrayList<AgeRange> ages = new ArrayList<>();
    public static ArrayList<Category> categories = new ArrayList<>();

    public static boolean isUserRegistered;
    public static User currentUser;


    public static int getCategoryIndex(int categoryId) {
        for(Category cat : categories){
            if(cat.getId() == categoryId)
                return categories.indexOf(cat);
        }

        return 0;
    }
}
