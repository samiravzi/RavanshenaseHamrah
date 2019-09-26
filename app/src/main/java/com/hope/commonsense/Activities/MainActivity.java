package com.hope.commonsense.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.hope.commonsense.Adapters.ArticlesAdapter;
import com.hope.commonsense.Adapters.QuestionsAdapter;
import com.hope.commonsense.Adapters.VideosAdapter;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.SliderItem;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.CustomTextSliderView;
import com.hope.commonsense.Web.WebStatistics;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends MyActivity implements ViewPagerEx.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener{

    String TAG = "MainActivity";
    public static MainActivity mainActivity;

    SliderLayout mSlider;
    RecyclerView mArticlesList;
    RecyclerView mQuestionsList;
    RecyclerView mVideosList;
    View mArticleLoading;
    View mQuestionLoading;
    View mVideoLoading;
    View mSliderLoading;
    DrawerLayout mDrawer;
    NavigationView mNavigation;
    Button mNavigationBtn;
    LinearLayout mLogout;

    ArticlesAdapter mArticleAdapter;
    QuestionsAdapter mQuestionAdapter;
    VideosAdapter mVideoAdapter;

    ArrayList<Article> articles;
    ArrayList<Question> questions;
    ArrayList<Video> videos;
    public static ArrayList<SliderItem> sliderItems;
    int sliderSelectedIndex=0;
    int backCounter = 0;

    int ARTICLE_LIST = 1;
    int QUESTION_LIST = 2;
    int VIDEO_LIST = 3;
    int VIDEO = 4;
    int ARTICLE = 5;
    int QUESTION = 6;

    public int UPDATE_LIKE = 0;
    public int UPDATE_COMMENT = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        onCreateViews();
        getData();
    }

// -------------------------------------------------------------------------------------------------
    private void getData(){
        try{
            getArticles();
            // then getQuestion;
            // then getVideos
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage()+"");
        }
    }


    public void getArticles(){
        mArticlesList.setVisibility(View.GONE);
        mArticleLoading.setVisibility(View.VISIBLE);

        requestCode = ARTICLE_LIST;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_ARTICLES);

        sendRequest();
    }

    private void getQuestions(){
        requestCode = QUESTION_LIST;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_QUESTIONS);
        sendRequest();
    }

    private void getVideos(){
        requestCode = VIDEO_LIST;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_VIDEOS);
        sendRequest();
    }

    public void sendRequest(){
        parameters.put(WebStatistics.KEY_INDEX, String.valueOf(0));
        parameters.put(WebStatistics.KEY_LIMIT, String.valueOf(WebStatistics.HOME_ITEMS_LIMIT));
        parameters.put(WebStatistics.KEY_TYPE, String.valueOf(WebStatistics.TYPE_BY_CATEGORY));
        parameters.put(WebStatistics.KEY_MIN_AGE, String.valueOf(0));
        parameters.put(WebStatistics.KEY_MAX_AGE, String.valueOf(99));
        parameters.put(WebStatistics.KEY_CATEGORY_ID, String.valueOf(0));
        parameters.put(WebStatistics.KEY_TAG_ID, String.valueOf(0));
        parameters.put(WebStatistics.KEY_ORDER_BY, String.valueOf(WebStatistics.ORDER_BY_DATE_DES));
        super.sendRequest();
    }


    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);
        if(result){
            JSONArray bodyArray = null;
            JSONObject bodyObject = null;
            try {
                if(requestCode < 4){
                    bodyArray = new JSONObject(data).getJSONArray("Body");
                } else {
                    bodyObject = new JSONObject(data).getJSONObject("Body");
                }

                if(requestCode == ARTICLE_LIST){
                    articles = JsonParser.jsonToArticles(bodyArray);
                    setupArticlesList();
                    getQuestions();
                } else if(requestCode == QUESTION_LIST){
                    questions = JsonParser.jsonToQuestions(bodyArray);
                    setupQuestionsList();
//                    getVideos();
                } else if(requestCode == VIDEO_LIST){
                    videos = JsonParser.jsonToVideos(bodyArray);
                    setupVideosList();
                } else if(requestCode == ARTICLE){
                    Article article = JsonParser.jsonToArticle(bodyObject);
                    startActivity(new Intent(mainActivity, ArticleActivity.class).putExtra("article", article));
                } else if(requestCode == QUESTION){
                    Question question = JsonParser.jsonToQuestion(bodyObject);
                    startActivity(new Intent(mainActivity, QuestionActivity.class).putExtra("question", question));
                } else if(requestCode == VIDEO){
                    Video video  = JsonParser.jsonToVideo(bodyObject);
                    startActivity(new Intent(mainActivity, VideoActivity.class).putExtra("video", video));
                }
            } catch (JSONException e) {
                Log.e(TAG, ""+e.getMessage());
                e.printStackTrace();
            }
        }
    }

// -------------------------------------------------------------------------------------------------
    private void onCreateViews(){
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        mNavigationBtn = (Button) findViewById(R.id.btn_navigation);
        mArticlesList = (RecyclerView) findViewById(R.id.articles_list);
        mQuestionsList = (RecyclerView) findViewById(R.id.questions_list);
        mVideosList = (RecyclerView) findViewById(R.id.videos_list);
        mArticleLoading = findViewById(R.id.article_loading);
        mQuestionLoading = findViewById(R.id.question_loading);
        mVideoLoading = findViewById(R.id.video_loading);
        mSliderLoading = findViewById(R.id.slider_loading);
        mSlider = (SliderLayout)findViewById(R.id.slider);
        TextView moreQuestionBtn = (TextView) findViewById(R.id.btn_more_questions);
        TextView moreArticleBtn = (TextView) findViewById(R.id.btn_more_articles);
        TextView moreVideoBtn = (TextView) findViewById(R.id.btn_more_videos);
        mLogout = (LinearLayout) findViewById(R.id.logout);
        Button searchBtn = (Button) findViewById(R.id.btn_search);

        moreQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mainActivity, QuestionListActivity.class));
            }
        });

        moreArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mainActivity, ArticleListActivity.class));
            }
        });

        moreVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mainActivity, VideoListActivity.class));
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogout();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mainActivity, SearchActivity.class));
            }
        });

        setupSlider();
        setupNavigationDrawer();
        setupListsTitle();
    }

    private void setupListsTitle() {
        TextView articlesTitle = (TextView) findViewById(R.id.article_list_title);
        TextView questionTitle = (TextView) findViewById(R.id.question_list_title);
        TextView videoTitle = (TextView) findViewById(R.id.video_list_title);
        articlesTitle.setText(getString(R.string.articles));
        questionTitle.setText(getString(R.string.questions));
        videoTitle.setText(getString(R.string.videos));
    }


    private void setupArticlesList(){
        mArticlesList.setVisibility(View.VISIBLE);
        mArticleLoading.setVisibility(View.GONE);

        mArticleAdapter = new ArticlesAdapter(mainActivity, articles);
        mArticleAdapter.isHeightMatchParent = false;
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(mainActivity);
        MyLayoutManager.setReverseLayout(true);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mArticlesList.setAdapter(mArticleAdapter);
        mArticlesList.setLayoutManager(MyLayoutManager);
    }

    private void setupQuestionsList(){
        mQuestionsList.setVisibility(View.VISIBLE);
        mQuestionLoading.setVisibility(View.GONE);

        mQuestionAdapter = new QuestionsAdapter(mainActivity, questions);
        mQuestionAdapter.isHeightMatchParent = false;
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(mainActivity);
        MyLayoutManager.setReverseLayout(true);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mQuestionsList.setAdapter(mQuestionAdapter);
        mQuestionsList.setLayoutManager(MyLayoutManager);
    }

    private void setupVideosList() {
        mVideosList.setVisibility(View.VISIBLE);
        mVideoLoading.setVisibility(View.GONE);

        mVideoAdapter = new VideosAdapter(mainActivity, videos);
        mVideoAdapter.isHeightMatchParent = false;
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(mainActivity);
        MyLayoutManager.setReverseLayout(true);
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mVideosList.setAdapter(mVideoAdapter);
        mVideosList.setLayoutManager(MyLayoutManager);
    }

    private void setupSlider(){
        try{
            if(sliderItems.size() == 0){
                mSlider.setVisibility(View.GONE);
                return;
            }

            for(SliderItem si : sliderItems){
                CustomTextSliderView textSliderView = new CustomTextSliderView(this);
                textSliderView
                        .description(si.getTitle())
                        .empty(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .image(si.getImage())
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                        .setOnSliderClickListener(si.onSliderClickListener);

                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", si.getTitle());

                mSlider.addSlider(textSliderView);
            }

            mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mSlider.setCustomAnimation(new DescriptionAnimation());
            mSlider.setDuration(4000);
            mSlider.addOnPageChangeListener(this);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mSlider.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage()+"");
        }
    }

    private void setupNavigationDrawer(){
        Utils.applyFont(mainActivity, mNavigation);
        mNavigation.setNavigationItemSelectedListener(this);
        mNavigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawer.isDrawerOpen(mNavigation)) {
                    mDrawer.closeDrawer(mNavigation);
                } else
                    mDrawer.openDrawer(mNavigation);
            }
        });

        Menu nav_Menu = mNavigation.getMenu();
        nav_Menu.findItem(R.id.videos).setVisible(false);
        if(getString(R.string.showvideolist).equals("0")){
            nav_Menu.findItem(R.id.videolist).setVisible(false);
            nav_Menu.findItem(R.id.applist).setVisible(false);
        }
        setupNavigationHeader();
    }

    private void setupNavigationHeader(){
        final View header = mNavigation.getHeaderView(0);
        ImageView profileImage = (ImageView) header.findViewById(R.id.profile_image);
        TextView nicknameTxv = (TextView) header.findViewById(R.id.nav_nickname);
        TextView statusTxv = (TextView) header.findViewById(R.id.nav_status);

        if(DataStore.isUserRegistered){
            String img = DataStore.currentUser.getImage();
            if(img.equals("")){
                Picasso.with(mainActivity).load(R.drawable.noprofile).into(profileImage);
            } else {
                Picasso.with(mainActivity).load(new File(img)).into(profileImage);
            }

            nicknameTxv.setText(DataStore.currentUser.getNickname());
            statusTxv.setText(DataStore.currentUser.getStatus());
            mLogout.setVisibility(View.VISIBLE);
        }
        else {
            Picasso.with(mainActivity).load(R.drawable.noprofile).into(profileImage);
            int color = getResources().getColor(R.color.text_layout_description);
            String string = getString(R.string.login_txt, color);
            statusTxv.setText(Html.fromHtml(string));
            nicknameTxv.setText("");
            mLogout.setVisibility(View.GONE);
        }

        statusTxv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DataStore.isUserRegistered){
                    startActivity(new Intent(mainActivity, LoginActivity.class));
                }
            }
        });
    }
// -------------------------------------------------------------------------------------------------

    private void doLogout() {
        DataStore.isUserRegistered = false;
        SharedPreferences preferences = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, false).commit();
        setupNavigationHeader();
    }

// -------------------------------------------------------------------------------------------------

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.articles: startActivity(new Intent(mainActivity, ArticleListActivity.class));break;
            case R.id.questions: startActivity(new Intent(mainActivity, QuestionListActivity.class)); break;
            case R.id.videos: startActivity(new Intent(mainActivity, VideoListActivity.class)); break;
            case R.id.categories: startActivity(new Intent(mainActivity, CategoryActivity.class)); break;
            case R.id.applist: break;
            case R.id.videolist: break;
            case R.id.profile:{
                        if(DataStore.isUserRegistered){
                            startActivity(new Intent(mainActivity, ProfileActivity.class).putExtra("user", DataStore.currentUser));
                        } else {
                            startActivity(new Intent(mainActivity, LoginActivity.class));
                        }
            } break;

        }
        mDrawer.closeDrawer(GravityCompat.END);
        return true;
    }
// -------------------------------------------------------------------------------------------------

    public void updateArticle(Article article, int attribute){
        for(Article article1 : articles){
            if(article1.getId() == article.getId()){
                if(attribute == UPDATE_LIKE){
                    article1.setLikeCount(article.getLikeCount());
                } else {
//                    article1.setComments(article.getComments());
                    article1.setCommentCount(article.getCommentCount());
                }
            }
        }
        mArticlesList.getAdapter().notifyDataSetChanged();
    }

    public void updateQuestion(Question question, int attribute){
        for(Question question1 : questions){
            if(question1.getId() == question.getId()){
                if(attribute == UPDATE_LIKE){
                    question1.setLikeCount(question.getLikeCount());
                } else {
//                    question1.setComments(question.getComments());
                    question1.setCommentCount(question.getCommentCount());
                }
            }
        }
        mQuestionsList.getAdapter().notifyDataSetChanged();
    }

    public void updateVideo(Video video, int attribute){
        for(Video video1 : videos){
            if(video1.getId() == video.getId()){
                if(attribute == UPDATE_LIKE){
                    video1.setLikeCount(video.getLikeCount());
                } else {
//                    video1.setComments(video.getComments());
                    video1.setCommentCount(video.getCommentCount());
                }
            }
        }
        mVideosList.getAdapter().notifyDataSetChanged();
    }

// -------------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationHeader();
        if(mQuestionAdapter != null){
            mQuestionAdapter.notifyDataSetChanged();
        }
        if(mSliderLoading != null){
            mSliderLoading.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.END)){
            mDrawer.closeDrawer(GravityCompat.END);
        }else if(backCounter == 0){
            backCounter++;
            Utils.showSnackBar(mainActivity, mDrawer, getString(R.string.exit_txt));
            startExitCounterTimer();
        } else {
            super.onBackPressed();
        }

    }


    private void startExitCounterTimer(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backCounter=0;
            }
        }, 2000);
    }
}
