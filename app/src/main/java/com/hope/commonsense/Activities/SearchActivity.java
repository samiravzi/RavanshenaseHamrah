package com.hope.commonsense.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hope.commonsense.Adapters.ViewPagerAdapter;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.SearchResult;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.Fragments.MyFragment;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;
import com.hope.commonsense.Web.WebServiceManager;
import com.hope.commonsense.Web.WebStatistics;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class SearchActivity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "SearchActivity";
    public static SearchActivity activity;
    Typeface mTypeface;

    DrawerLayout mDrawer;
    NavigationView mNavigation;
    Button mNavigationBtn;
    ViewPager mViewPager;
    TabLayout mTabs;
    RelativeLayout progressBar;
    EditText input;
    LinearLayout mLogout;
    RelativeLayout mLoading;
    LinearLayout mNoResultLayout;

    ViewPagerAdapter mAdapter;
    Thread textWatcher;
    String inputText="";
    String prevInputText ="";
    boolean isTextWatcherRunning=false;

    public static final int TYPE_ARTICLE = 1;
    public static final int TYPE_QUESTION = 2;
    public final int TYPE_VIDEO = 3;
    public final int TYPE_ALL = 4;
    public int type = TYPE_ARTICLE;
    int prevType=TYPE_ARTICLE;

    MyFragment[] fragments = new MyFragment[5];
    WebServiceManager searchThread;
    ArrayList<Integer> requestStack;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        activity = this;
        requestStack = new ArrayList<>();

        onCreateView();
    }


    private void onCreateView(){
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        mNavigationBtn = (Button) findViewById(R.id.btn_navigation);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabs = (TabLayout) findViewById(R.id.tabs);
        progressBar = (RelativeLayout) findViewById(R.id.searchingLayout);
        input = (EditText) findViewById(R.id.input);
        Button searchBtn = (Button) findViewById(R.id.btn_search);
        searchBtn.setVisibility(View.GONE);
        mLogout = (LinearLayout) findViewById(R.id.logout);
        mLoading = (RelativeLayout) findViewById(R.id.loading_layout);
        mNoResultLayout = (LinearLayout) findViewById(R.id.no_result_layout);

        final Runnable searchRunnable = new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(!isTextWatcherRunning){
                        break;
                    }
                    if( (!prevInputText.equals(inputText) && !inputText.equals("")) || prevType != type ){
                        prevInputText = inputText;
                        prevType = type;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                                sendRequest(prevInputText);
                            }
                        });
                    }

                    try {
//                        Log.e("thread","sleep ------------------> ");
                        Thread.sleep(1500);
//                        Log.e("thread", "awake ------------------->>");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };


        mTypeface = Typeface.createFromAsset(getAssets(), TextViewWithFont.Font);
        input.setTypeface(mTypeface);
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    inputText = s.toString();
                    Log.e(TAG,"---------> "+s);
                    if(!isTextWatcherRunning){
                        isTextWatcherRunning = true;
                        textWatcher = new Thread(searchRunnable);
                        textWatcher.start();
                    }
                } catch (Exception ex){
                    Log.e("Search Act","Error: onTextChanged: "+ex.getMessage());
                }

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SearchActivity.class));
            }
        });

        setupNavigationDrawer();
        setupViewPager();
        setupTabLayout();
    }

// -------------------------------------------------------------------------------------------------

    private void setupNavigationDrawer(){
        Utils.applyFont(activity, mNavigation);
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
                Picasso.with(activity).load(R.drawable.noprofile).into(profileImage);
            } else {
                Picasso.with(activity).load(new File(img)).into(profileImage);
            }

            nicknameTxv.setText(DataStore.currentUser.getNickname());
            statusTxv.setText(DataStore.currentUser.getStatus());
            mLogout.setVisibility(View.VISIBLE);
        }
        else {
            Picasso.with(activity).load(R.drawable.noprofile).into(profileImage);
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
                    startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });
    }

    private void setupViewPager(){
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(int i=1; i<5 ; i++){
            fragments[i] = new MyFragment();
            fragments[i].setType(i);
        }

        mAdapter.addFragment(fragments[1], getString(R.string.articles));
        mAdapter.addFragment(fragments[2], getString(R.string.questions));
//        mAdapter.addFragment(fragments[3], getString(R.string.video));
        mAdapter.addFragment(fragments[4], getString(R.string.all));
        mViewPager.setAdapter(mAdapter);
        Log.e(TAG, mAdapter.getCount()+"");
        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0: type = TYPE_ARTICLE; break;
                    case 1: type = TYPE_QUESTION; break;
//                    case 2: type = TYPE_VIDEO; break;
                    case 2: type = TYPE_ALL; break;
                }
            }
        };

        mViewPager.addOnPageChangeListener(onPageChangeListener);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(fragments.length-1);
            }
        });
    }

    private void setupTabLayout()  {
        mTabs.setupWithViewPager(mViewPager);
        Utils.changeTabsFont(activity, mTabs);
    }

// -------------------------------------------------------------------------------------------------

    public void sendRequest(String input){
        mNoResultLayout.setVisibility(View.GONE);
        requestCode = 1;
        if(searchThread != null){
            searchThread.cancel(true);
        }
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_SEARCH_SUBJECT);
        parameters.put(WebStatistics.KEY_TYPE, type +"");
        parameters.put(WebStatistics.KEY_TEXT, input);
        super.sendRequest();
    }

    public void getItemById(SearchResult searchResult) {
        switch (searchResult.getType()){
            case 1: getArticleById(searchResult.getId()); break;
            case 2: getQuestionById(searchResult.getId()); break;
//            case 3: getVideoById(searchResult.getId()); break;
        }
    }

    private void getArticleById(int id){
        mLoading.setVisibility(View.VISIBLE);
        requestCode = 2;
        HashMap<String, String> parameters = new HashMap<>();
//        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_ARTICLE_BY_ID);
        parameters.put(WebStatistics.KEY_ARTICLE_ID, id+"");
        WebServiceManager webServiceManager = new WebServiceManager(this, requestCode);
        webServiceManager.execute(parameters);
    }

    private void getQuestionById(int id){
        mLoading.setVisibility(View.VISIBLE);
        requestCode = 3;
        HashMap<String, String> parameters = new HashMap<>();
//        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_QUESTION_BY_ID);
        parameters.put(WebStatistics.KEY_QUESTION_ID, id+"");
        WebServiceManager webServiceManager = new WebServiceManager(this, requestCode);
        webServiceManager.execute(parameters);
    }

    private void getVideoById(int id){
        mLoading.setVisibility(View.VISIBLE);
        requestCode = 4;
        HashMap<String, String> parameters = new HashMap<>();
//        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_VIDEO_BY_ID);
        parameters.put(WebStatistics.KEY_VIDEO_ID, id+"");
        WebServiceManager webServiceManager = new WebServiceManager(this, requestCode);
        webServiceManager.execute(parameters);
    }


    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);

        mLoading.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);

        Log.e(TAG, "request code : "+requestCode+"");
        if(result){
            try {
                if(requestCode == 1){
                    JSONArray body;
                    if(type == TYPE_ALL){
                        body = new JSONObject(data).getJSONObject("Body").getJSONArray("Articles");
                    } else {
                        body = new JSONObject(data).getJSONArray("Body");
                    }
                    if(isTextWatcherRunning){
                        fragments[prevType].searchResults = JsonParser.jsonToSearchResult(body);
                        fragments[prevType].refreshList();
                        if(fragments[prevType].searchResults.size() == 0){
                            mNoResultLayout.setVisibility(View.VISIBLE);
                        }
                    }

                } else if(requestCode == 2){
                    Article article = JsonParser.jsonToArticle(new JSONObject(data).getJSONObject("Body"));
                    if(isTextWatcherRunning){
                        startActivity(new Intent(activity, ArticleActivity.class).putExtra("article", article));
                        isTextWatcherRunning = false;
                    }

                } else if(requestCode == 3){
                    Question question = JsonParser.jsonToQuestion(new JSONObject(data).getJSONObject("Body"));
                    if(isTextWatcherRunning){
                        startActivity(new Intent(activity, QuestionActivity.class).putExtra("question", question));
                        isTextWatcherRunning = false;
                    }

                } else if(requestCode == 4){
                    Video video = JsonParser.jsonToVideo(new JSONObject(data).getJSONObject("Body"));
                    if(isTextWatcherRunning){
                        startActivity(new Intent(activity, VideoActivity.class).putExtra("video", video));
                        isTextWatcherRunning = false;
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage()+"");
                e.printStackTrace();
            }
        } else {
            Toast.makeText(activity, "error", Toast.LENGTH_LONG).show();
        }
    }

// -------------------------------------------------------------------------------------------------
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.articles: startActivity(new Intent(activity, ArticleListActivity.class));break;
            case R.id.questions: startActivity(new Intent(activity, QuestionListActivity.class)); break;
            case R.id.videos: startActivity(new Intent(activity, VideoListActivity.class)); break;
            case R.id.categories: startActivity(new Intent(activity, CategoryActivity.class)); break;
            case R.id.applist: break;
            case R.id.videolist: break;
            case R.id.profile: {
                if(DataStore.isUserRegistered){
                    startActivity(new Intent(activity, ProfileActivity.class).putExtra("user", DataStore.currentUser));
                } else {
                    startActivity(new Intent(activity, LoginActivity.class));
                }
            } break;
        }
        mDrawer.closeDrawer(GravityCompat.END);
        return true;
    }

// -------------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationHeader();
    }

    @Override
    public void onBackPressed() {
        finishMe();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        finishMe();
        super.onDestroy();
    }


    private void finishMe(){
        isTextWatcherRunning = false;
    }

}
