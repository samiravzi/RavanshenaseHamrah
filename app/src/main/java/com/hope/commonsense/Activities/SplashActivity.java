package com.hope.commonsense.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.AgeRange;
import com.hope.commonsense.Entities.Category;
import com.hope.commonsense.Entities.User;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Web.WebStatistics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SplashActivity extends MyActivity {

    String TAG = "SplashActivity";
    public Activity activity;
    SharedPreferences preferences;
    RelativeLayout mainLayout;
    LinearLayout progressbarLayout;

    int CATEGORY = 1;
    int SLIDER = 2;

    boolean isActivityDestroyed = false;
    boolean isStartActivityEnable = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        activity = this;
        preferences = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);

        mainLayout = (RelativeLayout) findViewById(R.id.layout);
        progressbarLayout = (LinearLayout) findViewById(R.id.progressbar_layout);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isActivityDestroyed){
                    if(isStartActivityEnable){
                        startActivity(new Intent(activity, MainActivity.class));
                    } else {
                        isStartActivityEnable = true;
                    }
                }
            }
        }, 2000);


        startLoadAnimation();
        checkUserRegistration();
        loadAgeRanges();
        if(Utils.isNetworkAvailable(this)){
            getCategories();
        } else {
            progressbarLayout.setVisibility(View.GONE);
            showNoConnectionDialog();
        }

    }

    private void showNoConnectionDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout_textview, null);
        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishMe();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if(Utils.isNetworkAvailable(activity)){
                            getCategories();
                        } else {
                            showNoConnectionDialog();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void startLoadAnimation() {
        ImageView animationTarget = (ImageView) this.findViewById(R.id.progressbar);
        RotateAnimation rotateAnimation = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(3000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        animationTarget.startAnimation(rotateAnimation);
    }

    private void checkUserRegistration() {

        if(preferences.getBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, false)){
            User user = new User();
            user.setUsername(preferences.getString(DataStore.PREFERENCES_KEY_USERNAME,""));
            user.setToken(preferences.getString(DataStore.PREFERENCES_KEY_TOKEN,""));
            user.setNickname(preferences.getString(DataStore.PREFERENCES_KEY_NICKNAME,""));
            user.setStatus(preferences.getString(DataStore.PREFERENCES_KEY_STATUS,""));
            user.setImage(preferences.getString(DataStore.PREFERENCES_KEY_IMAGE,""));
            user.setEmail(preferences.getString(DataStore.PREFERENCES_KEY_EMAIL,""));
            user.setPassword(preferences.getString(DataStore.PREFERENCES_KEY_PASSWORD,""));
            DataStore.currentUser = user;
            DataStore.isUserRegistered = true;

        } else {
            User user = new User();
            user.setToken(generateToken());
            user.setStatus("");
            user.setEmail("");
            user.setPassword("");
            user.setUsername("");
            user.setImage("");
            DataStore.currentUser = user;
            DataStore.isUserRegistered = false;

            /*user.setNickname("samir");
            user.setToken("83563624c4ba6a052927aebf8e6c70");
            user.setStatus("مادر پوریا 3 ساله");
            user.setEmail("");
            user.setPassword("sami");
            user.setUsername("09121010");
            user.setImage("");*/
        }
        Log.e(TAG, "CurrentUser: "+DataStore.currentUser.getUsername()+" / "+DataStore.currentUser.getNickname());
    }

    private String generateToken() {
        String token = preferences.getString(DataStore.PREFERENCES_KEY_TOKEN, "");
        if(token.equals("")){
            token = Utils.generateToken();
            preferences.edit().putString(DataStore.PREFERENCES_KEY_TOKEN, token).commit();
        }
        return token;
    }

    private void loadAgeRanges() {
        int[] ageIds = getResources().getIntArray(R.array.age_ids);
        int[] agePriority = getResources().getIntArray(R.array.age_priority);
        String[] ageTitles = getResources().getStringArray(R.array.age_titles);

        for(int i=0; i<ageIds.length; i++){
            DataStore.ages.add(new AgeRange(ageIds[i], ageTitles[i], agePriority[i]));
        }
    }

    private void getCategories() {
        try{
            DataStore.categories.clear();
            Category allCategory = new Category();
            allCategory.setId(0);
            allCategory.setTitle(getString(R.string.all_categories));
            DataStore.categories.add(allCategory);
        } catch (NullPointerException ex){
            Log.e(TAG, ex.getMessage()+"");
        }
        progressbarLayout.setVisibility(View.VISIBLE);
        requestCode = CATEGORY;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_CATEGORIES);
        super.sendRequest();
    }

    private void getSliderContents() {
        requestCode = SLIDER;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_SLIDER_CONTENTS);
        parameters.put(WebStatistics.KEY_LIMIT, WebStatistics.HOME_SLIDER_ITEMS_COUNT+"");
        super.sendRequest();
    }

    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);

        try {
            if(result){
                if(requestCode == CATEGORY){
                    JSONArray array = new JSONObject(data).getJSONArray("Body");
                    DataStore.categories.addAll(JsonParser.jsonToCategory(array));
                    getSliderContents();
                }
                else if(requestCode == SLIDER){
                    JSONArray array = new JSONObject(data).getJSONArray("Body");
                    MainActivity.sliderItems = JsonParser.jsonToSliderItems(array);
                    if(!isActivityDestroyed){
                        if(isStartActivityEnable){
                            startActivity(new Intent(activity, MainActivity.class));
                        } else {
                            isStartActivityEnable = true;
                        }
                    }
                }
            }
            else {
                if(requestCode == CATEGORY){
                    getSliderContents();
                } else {
                    if(!isActivityDestroyed){
                        startActivity(new Intent(activity, MainActivity.class));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
        } catch (Exception e){
            Log.e(TAG, e.getMessage()+"");
        }
    }

// -------------------------------------------------------------------------------------------------

    private void finishMe(){
        isActivityDestroyed = true;
        finish();
    }

    @Override
    public void onBackPressed() {
        isActivityDestroyed = true;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        isActivityDestroyed = true;
        super.onDestroy();
    }
}
