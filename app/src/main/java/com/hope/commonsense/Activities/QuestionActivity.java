package com.hope.commonsense.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.hope.commonsense.Adapters.ArticlesAdapter;
import com.hope.commonsense.Adapters.QuestionsAdapter;
import com.hope.commonsense.Adapters.VideosAdapter;
import com.hope.commonsense.DBAdapter;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;
import com.hope.commonsense.Web.WebServiceManager;
import com.hope.commonsense.Web.WebStatistics;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ir.noghteh.JustifiedTextView;


public class QuestionActivity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener  {

    String TAG = "QuestionActivity";
    MyActivity activity;
    Question currentQuestion;
    Typeface mTypeface;
    DBAdapter db;

    DrawerLayout mDrawer;
    NavigationView mNavigation;
    Button mNavigationBtn;
    SliderLayout mSlider;
    RecyclerView mArticlesList;
    RecyclerView mQuestionsList;
    RecyclerView mVideosList;
    View mArticleLoading;
    View mQuestionLoading;
    View mVideoLoading;
    View mCommentsLoading;
    QuestionsAdapter mQuestionsAdapter;
    ArticlesAdapter mArticleAdapter;
    VideosAdapter mVideoAdapter;
    LinearLayout mArticleLayout;
    LinearLayout mQuestionLayout;
    LinearLayout mVideoLayout;
    LinearLayout tagsLayout;
    WebView answer;
    LinearLayout mLogout;
    TextView detailTxt;
    ProgressBar likeProgressbar;
    ImageView likeBtn;

    ArrayList<Article> articles;
    ArrayList<Question> questions;
    ArrayList<Video> videos;

    boolean isLiked;
    int LIKE = 7;
    int REPORT_VIEW = 8;
    final int RELATED_ARTICLE = 1;
    final int RELATED_QUESTION = 2;
    final int RELATED_VIDEO = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        activity = this;
        db = new DBAdapter(activity);

        currentQuestion = (Question) getIntent().getSerializableExtra("question");
        onCreateView();

        getRelatedData(RELATED_ARTICLE);
        sendViewReport();
    }


    private void onCreateView(){
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mNavigationBtn = (Button) findViewById(R.id.btn_navigation);
        Button searchBtn = (Button) findViewById(R.id.btn_search);
        JustifiedTextView title = (JustifiedTextView) findViewById(R.id.title);
        answer = (WebView) findViewById(R.id.answer);
        mArticlesList = (RecyclerView) findViewById(R.id.articles_list);
        mQuestionsList = (RecyclerView) findViewById(R.id.questions_list);
        mVideosList = (RecyclerView) findViewById(R.id.videos_list);
        mArticleLoading = findViewById(R.id.article_loading);
        mQuestionLoading = findViewById(R.id.question_loading);
        mVideoLoading = findViewById(R.id.video_loading);
        mSlider = (SliderLayout)findViewById(R.id.slider);
        LinearLayout likeLayout = (LinearLayout) findViewById(R.id.likeLayout);
        LinearLayout commentLayout = (LinearLayout) findViewById(R.id.commentLayout);
        LinearLayout shareLayout = (LinearLayout) findViewById(R.id.share_layout);
        likeBtn = (ImageView) findViewById(R.id.btn_like);
        mCommentsLoading = findViewById(R.id.comments_loading);
        mArticleLayout = (LinearLayout)findViewById(R.id.article_layout);
        mQuestionLayout = (LinearLayout)findViewById(R.id.question_layout);
        mVideoLayout = (LinearLayout) findViewById(R.id.video_layout);
        tagsLayout = (LinearLayout) findViewById(R.id.tags_layout);
        mLogout = (LinearLayout) findViewById(R.id.logout);
        detailTxt = (TextView) findViewById(R.id.txt_detail);
        TextView moreQuestionBtn = (TextView) findViewById(R.id.btn_more_questions);
        TextView moreArticleBtn = (TextView) findViewById(R.id.btn_more_articles);
        TextView moreVideoBtn = (TextView) findViewById(R.id.btn_more_videos);
        likeProgressbar = (ProgressBar) findViewById(R.id.progressbar_like);


        mTypeface = Typeface.createFromAsset(getAssets(), TextViewWithFont.Font);
        setupTextFontAndSize(title);

        toolbarTitle.setText(getString(R.string.activities_title));
        Spanned sp = Utils.fromHtml(currentQuestion.getTitle());
        title.setText(sp.toString());

        detailTxt.setText(getDetailText());

        db.open();
        isLiked = db.isLiked(currentQuestion);
        db.close();
        if(isLiked){
            likeBtn.setBackgroundResource(R.drawable.btn_liked);
        } else {
            likeBtn.setBackgroundResource(R.drawable.btn_like);
        }

        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataStore.isUserRegistered){
                    setLike();
                } else {
                    startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });

        commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, CommentActivity.class).putExtra("object", currentQuestion), 1);
            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.shareObject(activity, currentQuestion);
            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SearchActivity.class));
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogout();
            }
        });

        moreQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, QuestionListActivity.class).putExtra("catId", currentQuestion.getCatId()));
            }
        });

        moreArticleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, ArticleListActivity.class).putExtra("catId", currentQuestion.getCatId()));
            }
        });

        moreVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity, VideoListActivity.class).putExtra("catId", currentQuestion.getCatId()));
            }
        });


        setupAnswer();
        setupNavigationDrawer();
        setupRelatedLists();
        setupTagsLayout();
    }

// -------------------------------------------------------------------------------------------------
    private void getRelatedData(int type) {
        requestCode = type;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_RELATED);
        parameters.put(WebStatistics.KEY_INDEX, String.valueOf(questions==null?0:questions.size()));
        parameters.put(WebStatistics.KEY_LIMIT, String.valueOf(WebStatistics.HOME_ITEMS_LIMIT));
        parameters.put(WebStatistics.KEY_TYPE, String.valueOf(WebStatistics.TYPE_QUESTION));
        parameters.put(WebStatistics.KEY_TYPE_TO_GET, String.valueOf(type));
        parameters.put(WebStatistics.KEY_ID, String.valueOf(currentQuestion.getId()));
        parameters.put(WebStatistics.KEY_ORDER_BY, String.valueOf(WebStatistics.ORDER_BY_ID));
        super.sendRequest();
    }

    private void setLike(){
        likeBtn.setVisibility(View.GONE);
        likeProgressbar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put(WebStatistics.KEY_URL, WebStatistics.URL_LIKE);
        params.put(WebStatistics.KEY_TOKEN, DataStore.currentUser.getToken());
        params.put(WebStatistics.KEY_USERNAME, DataStore.currentUser.getUsername());
        params.put(WebStatistics.KEY_ARTICLE_ID, "0");
        params.put(WebStatistics.KEY_QUESTION_ID, currentQuestion.getId()+"");
        params.put(WebStatistics.KEY_VIDEO_ID, "0");
        WebServiceManager webServiceManager = new WebServiceManager(this, LIKE);
        webServiceManager.execute(params);
    }

    private void sendViewReport() {
        requestCode = REPORT_VIEW;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_QUESTION_BY_ID);
        parameters.put(WebStatistics.KEY_QUESTION_ID, String.valueOf(currentQuestion.getId()));
        super.sendRequest();
    }


    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);
        if(result){
            try {
                if(requestCode == RELATED_ARTICLE) {
                    JSONObject body = new JSONObject(data).getJSONObject("Body");
                    if (body.has("Articles")) {
                        articles.addAll(JsonParser.jsonToArticles(body.getJSONArray("Articles")));
                    }
                    refreshArticleList();
                    getRelatedData(RELATED_QUESTION);

                } else if(requestCode == RELATED_QUESTION) {
                    JSONObject body = new JSONObject(data).getJSONObject("Body");
                    if(body.has("Questions")){
                        questions.addAll(JsonParser.jsonToQuestions(body.getJSONArray("Questions")));
                    }
                    refreshQuestionList();
//                    getRelatedData(RELATED_VIDEO);

                } else if(requestCode == RELATED_VIDEO) {
                    JSONObject body = new JSONObject(data).getJSONObject("Body");
                    if(body.has("Videos")){
                        videos.addAll(JsonParser.jsonToVideos(body.getJSONArray("Videos")));
                    }
                    refreshVideoList();
                } else if(requestCode == LIKE){
                    isLiked = !isLiked;
                    updateLikeBtn();
                    if(isLiked){
                        currentQuestion.setLikeCount(currentQuestion.getLikeCount()+1);
                    } else {
                        currentQuestion.setLikeCount(currentQuestion.getLikeCount()-1);
                    }
                    detailTxt.setText(getDetailText());
                    MainActivity.mainActivity.updateQuestion(currentQuestion, MainActivity.mainActivity.UPDATE_LIKE);
                    db.open();
                    db.setLike(currentQuestion, isLiked);
                    db.close();
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getMessage()+"");
                e.printStackTrace();
            }
        }  else {
            if(requestCode == LIKE){
                updateLikeBtn();
                Utils.showSnackBar(activity, mDrawer, getString(R.string.error_retry_text));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");

        if(data != null){
            currentQuestion = (Question) data.getSerializableExtra("object");
            detailTxt.setText(getDetailText());
            Log.e(TAG, "after comment: "+getDetailText());
        }
    }

// -------------------------------------------------------------------------------------------------

    private String getDetailText() {
        StringBuilder sb = new StringBuilder();
        sb.append(currentQuestion.getLikeCount());
        sb.append(getString(R.string.like) + "، ");
        sb.append(currentQuestion.getCommentCount());
        sb.append(getString(R.string.comment) + "، ");
        sb.append(currentQuestion.getViewCount());
        sb.append(getString(R.string.view));
        return sb.toString();
    }

    private void setupAnswer(){
        answer.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        String html = getString(R.string.html);
        html = html.replace("&?", currentQuestion.getAnswer());
        answer.loadDataWithBaseURL("", html, "text/html; charset=utf-8", "utf-8", "");
        answer.getSettings();
        answer.setBackgroundColor(Color.TRANSPARENT);
//        mContent.setText(Html.fromHtml(currentArticle.getContent()));
    }

    private void setupTextFontAndSize(JustifiedTextView tv){
        tv.setTextColor(getResources().getColor(R.color.black));
        tv.setTypeFace(mTypeface);
        if(tv.getTag().equals("title")){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)getResources().getDimension(R.dimen.activity_question_title));
        } else if(tv.getTag().equals("answer")){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)getResources().getDimension(R.dimen.activity_question_answer));
        }
    }

    private void setupRelatedLists(){
        articles = new ArrayList<>();
        questions = new ArrayList<>();
        videos = new ArrayList<>();

        LinearLayoutManager myLayoutManager1 = new LinearLayoutManager(activity);
        LinearLayoutManager myLayoutManager2 = new LinearLayoutManager(activity);
        LinearLayoutManager myLayoutManager3 = new LinearLayoutManager(activity);
        myLayoutManager1.setReverseLayout(true);
        myLayoutManager2.setReverseLayout(true);
        myLayoutManager3.setReverseLayout(true);
        myLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        myLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        myLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        mQuestionsAdapter = new QuestionsAdapter(activity, questions);
        mArticleAdapter = new ArticlesAdapter(activity, articles);
        mVideoAdapter = new VideosAdapter(activity, videos);
        mQuestionsList.setLayoutManager(myLayoutManager1);
        mVideosList.setLayoutManager(myLayoutManager2);
        mArticlesList.setLayoutManager(myLayoutManager3);
        mArticlesList.setAdapter(mArticleAdapter);
        mQuestionsList.setAdapter(mQuestionsAdapter);
        mVideosList.setAdapter(mVideoAdapter);
    }

    private void setupNavigationDrawer(){
        try{
            Utils.applyFont(activity, mNavigation);
            mNavigation.setNavigationItemSelectedListener(this);
            mNavigationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mDrawer.isDrawerOpen(mNavigation)){
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
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage()+"");
        }
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

    private void setupTagsLayout(){
        LayoutInflater inflater = LayoutInflater.from(activity);
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < currentQuestion.getTags().size(); i++) {
            View view = inflater.inflate(R.layout.tag_layout, tagsLayout, false);
            LinearLayout tag = (LinearLayout) view;
            TextView text = (TextView) tag.findViewById(R.id.text);
            text.setText(currentQuestion.getTags().get(i).getTitle());
            text.setTextColor(getResources().getColor(R.color.colorPrimary));
            text.setBackgroundResource(R.drawable.bg_tag_light);
            final int finalI = i;
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(activity, TagActivity.class).putExtra("tag", currentQuestion.getTags().get(finalI)));
                }
            });
            views.add(tag);
        }
        Utils.populateTags(tagsLayout, views, activity);
    }

    private void refreshArticleList(){
        mArticleLoading.setVisibility(View.GONE);
        mArticleAdapter.notifyDataSetChanged();
        if(mArticleAdapter.getItemCount() == 0){
            mArticleLayout.setVisibility(View.GONE);
        }
    }

    private void refreshQuestionList(){
        mQuestionLoading.setVisibility(View.GONE);
        mQuestionsAdapter.notifyDataSetChanged();
        if(mQuestionsAdapter.getItemCount() == 0){
            mQuestionLayout.setVisibility(View.GONE);
        }
    }

    private void refreshVideoList(){
        mVideoLoading.setVisibility(View.GONE);
        mVideoAdapter.notifyDataSetChanged();
        if(mVideoAdapter.getItemCount() == 0){
            mVideoLayout.setVisibility(View.GONE);
        }
    }

    private void updateLikeBtn(){
        if(isLiked){
            likeBtn.setBackgroundResource(R.drawable.btn_liked);
        } else {
            likeBtn.setBackgroundResource(R.drawable.btn_like);
        }
        likeBtn.setVisibility(View.VISIBLE);
        likeProgressbar.setVisibility(View.GONE);
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

    private void doLogout() {
        DataStore.isUserRegistered = false;
        SharedPreferences preferences = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, false).commit();
        setupNavigationHeader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationHeader();
    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.END)){
            mDrawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


}
