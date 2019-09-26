package com.hope.commonsense.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hope.commonsense.Adapters.CommentAdapter;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Comment;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Web.WebServiceManager;
import com.hope.commonsense.Web.WebStatistics;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class CommentActivity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener{

    CommentActivity activity;
    String TAG = "CommentActivity";

    RelativeLayout layout;
    RecyclerView mCommentsList;
    EditText mCommentInput;
    View mCommentsLoading;
    CommentAdapter mCommentsAdapter;
    TextView mNoCommentText;
    DrawerLayout mDrawer;
    NavigationView mNavigation;
    Button mNavigationBtn;
    LinearLayout mLogout;
    ProgressBar sendCommentProgressbar;
    TextView sendComment;

    Boolean loadMore = true;
    ArrayList<Comment> comments;

    Article article;
    Question question;
    Video video;

    String commentText="";
    int removedIndex = -1;
    int type;
    final int ARTICLE = 1;
    final int QUESTION = 2;
    final int VIDEO = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        activity = this;
        comments = new ArrayList<>();

        Object object = getIntent().getSerializableExtra("object");
        if(object instanceof Article){
            article = (Article)object;
            type = ARTICLE;
        } else if(object instanceof Question){
            question = (Question) object;
            type = QUESTION;
        } else {
            video = (Video)object;
            type = VIDEO;
        }

        onCreateView();
        getComments();
    }

// -------------------------------------------------------------------------------------------------

    private void onCreateView(){
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        mNavigationBtn = (Button) findViewById(R.id.btn_navigation);
        layout = (RelativeLayout) findViewById(R.id.layout);
        mCommentInput = (EditText) findViewById(R.id.input_comment);
        mCommentsLoading = findViewById(R.id.comments_loading);
        mCommentsList = (RecyclerView) findViewById(R.id.comments);
        mNoCommentText = (TextView) findViewById(R.id.no_comment);
        sendComment = (TextView) findViewById(R.id.btn_send_comment);
        mLogout = (LinearLayout) findViewById(R.id.logout);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.back_layout);
        sendCommentProgressbar = (ProgressBar) findViewById(R.id.progressbar_send_comment);

        sendComment.setPaintFlags(sendComment.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DataStore.isUserRegistered){
                    String com = mCommentInput.getText().toString();
                    if(!com.equals("")){
                        newComment(com);
                    } else {
                        Utils.showSnackBar(activity, layout, getString(R.string.enter_text));
                    }
                } else {
                    startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishMe();
            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogout();
            }
        });

        setupCommentsList();
        setupNavigationDrawer();
    }


    private void setupCommentsList() {
        mCommentsAdapter = new CommentAdapter(activity, comments);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mCommentsList.setLayoutManager(mLayoutManager);
        mCommentsList.setAdapter(mCommentsAdapter);

        mCommentsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(-1)) {
                    onScrolledToTop();
                } else if (!recyclerView.canScrollVertically(1)) {
                    onScrolledToBottom();
                } else if (dy < 0) {
                    onScrolledUp();
                } else if (dy > 0) {
                    onScrolledDown();
                }
            }

            public void onScrolledUp() {
            }

            public void onScrolledDown() {
            }

            public void onScrolledToTop() {
            }

            public void onScrolledToBottom() {
//                getComments();
            }

        });
    }

    private void refreshCommentsLists(){
        mCommentsAdapter.notifyDataSetChanged();
        if(mCommentsAdapter.getItemCount() == 0){
            mNoCommentText.setVisibility(View.VISIBLE);
            mCommentsList.setVisibility(View.GONE);
        } else {
            mNoCommentText.setVisibility(View.GONE);
            mCommentsList.setVisibility(View.VISIBLE);
        }
        mCommentsLoading.setVisibility(View.GONE);
    }

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

    private void doLogout() {
        DataStore.isUserRegistered = false;
        SharedPreferences preferences = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, false).commit();
        setupNavigationHeader();
    }

// -------------------------------------------------------------------------------------------------

    private void getComments(){
        if(loadMore){
            loadMore = false;
            mCommentsLoading.setVisibility(View.VISIBLE);
            mCommentsList.setVisibility(View.GONE);
            mNoCommentText.setVisibility(View.GONE);
            requestCode = 1;
            parameters.clear();
            parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_COMMENTS);
            parameters.put(WebStatistics.KEY_INDEX, String.valueOf(comments.size()));
            parameters.put(WebStatistics.KEY_LIMIT, String.valueOf(WebStatistics.COMMENT_LIMIT));
            parameters.put(WebStatistics.KEY_ARTICLE_ID, type==ARTICLE? article.getId()+"" : "0");
            parameters.put(WebStatistics.KEY_QUESTION_ID, type==QUESTION? question.getId()+"" : "0");
            parameters.put(WebStatistics.KEY_VIDEO_ID, type==VIDEO? video.getId()+"" : "0");
            super.sendRequest();
        }
    }

    private void newComment(String text){
        commentText = text;
        sendComment.setVisibility(View.INVISIBLE);
        sendCommentProgressbar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        params.put(WebStatistics.KEY_URL, WebStatistics.URL_COMMENT);
        params.put(WebStatistics.KEY_TOKEN, DataStore.currentUser.getToken());
        params.put(WebStatistics.KEY_USERNAME, DataStore.currentUser.getUsername());
        params.put(WebStatistics.KEY_ARTICLE_ID, type==ARTICLE? article.getId()+"" : "0");
        params.put(WebStatistics.KEY_QUESTION_ID, type==QUESTION? question.getId()+"" : "0");
        params.put(WebStatistics.KEY_VIDEO_ID, type==VIDEO? video.getId()+"" : "0");
        params.put(WebStatistics.KEY_TEXT, text);
        WebServiceManager webServiceManager = new WebServiceManager(this, 2);
        webServiceManager.execute(params);
    }

    public void removeComment(int commentId, int position) {
        removedIndex = position;
        if(commentId == 0){
            getCommentId();
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(WebStatistics.KEY_URL, WebStatistics.URL_DELETE_COMMENTS);
        params.put(WebStatistics.KEY_TOKEN, DataStore.currentUser.getToken());
        params.put(WebStatistics.KEY_USERNAME, DataStore.currentUser.getUsername());
        params.put(WebStatistics.KEY_COMMENT_ID, commentId+"");
        WebServiceManager webServiceManager = new WebServiceManager(this, 3);
        webServiceManager.execute(params);
    }

    private void getCommentId(){
        requestCode = 4;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_COMMENTS);
        parameters.put(WebStatistics.KEY_INDEX, String.valueOf(comments.size()-5>0?comments.size()-5:0));
        parameters.put(WebStatistics.KEY_LIMIT, String.valueOf(10));
        parameters.put(WebStatistics.KEY_ARTICLE_ID, type==ARTICLE? article.getId()+"" : "0");
        parameters.put(WebStatistics.KEY_QUESTION_ID, type==QUESTION? question.getId()+"" : "0");
        parameters.put(WebStatistics.KEY_VIDEO_ID, type==VIDEO? video.getId()+"" : "0");
        super.sendRequest();
    }

    private void updateCurrentObject(){
        if(type == ARTICLE){
            article.setComments(comments);
            article.setCommentCount(comments.size());
            MainActivity.mainActivity.updateArticle(article, MainActivity.mainActivity.UPDATE_COMMENT);
        } else if(type == QUESTION){
            question.setComments(comments);
            question.setCommentCount(comments.size());
            MainActivity.mainActivity.updateQuestion(question, MainActivity.mainActivity.UPDATE_COMMENT);
        } else {
            video.setComments(comments);
            video.setCommentCount(comments.size());
            MainActivity.mainActivity.updateVideo(video, MainActivity.mainActivity.UPDATE_COMMENT);
        }
    }

    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);
        try{
            loadMore = true;
            if(result){
                if(requestCode == 1){
                    JSONArray array = new JSONObject(data).getJSONArray("Body");
                    ArrayList<Comment> newComments = JsonParser.jsonToComments(array);
                    comments.addAll(newComments);
                    refreshCommentsLists();
                    if(newComments.size() == 0){
                        loadMore = false;
                    }
                } else if(requestCode == 2){
                    sendComment.setVisibility(View.VISIBLE);
                    sendCommentProgressbar.setVisibility(View.GONE);
                    Comment comment = new Comment();
                    comment.setId(0);
                    comment.setText(commentText);
                    comment.setUser(DataStore.currentUser);
                    comment.setDate(Calendar.getInstance());
                    comments.add(comment);
                    updateCurrentObject();
                    refreshCommentsLists();
                    mCommentsList.scrollToPosition(comments.size()-1);
                    mCommentInput.setText("");

                } else if(requestCode == 3){
                    Parcelable recyclerViewState = mCommentsList.getLayoutManager().onSaveInstanceState();
                    mCommentsAdapter.doRemoveComment(true);
                    if(recyclerViewState != null){
                        mCommentsList.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    }
                    updateCurrentObject();
                } else if(requestCode == 4){
                    JSONArray array = new JSONObject(data).getJSONArray("Body");
                    ArrayList<Comment> comments = JsonParser.jsonToComments(array);
                    Log.e(TAG,"pos: "+removedIndex);
                    int id = getCommentIdByText(comments.get(removedIndex), comments);
                    if(id > 0){
                        removeComment(id, removedIndex);
                    }
                }
            } else {
                if(requestCode == 2){
                    sendComment.setVisibility(View.VISIBLE);
                    sendCommentProgressbar.setVisibility(View.GONE);
                } else if(requestCode == 3){
                    mCommentsAdapter.doRemoveComment(false);
                } else if(requestCode == 4){
                    mCommentsAdapter.doRemoveComment(false);
                }
                Utils.showSnackBar(activity, mDrawer, getString(R.string.error_retry_text));
            }
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage()+"");
            if(requestCode == 2){
                sendComment.setVisibility(View.VISIBLE);
                sendCommentProgressbar.setVisibility(View.GONE);
            } else if(requestCode == 3){
                mCommentsAdapter.doRemoveComment(false);
            } else if(requestCode == 4){
                mCommentsAdapter.doRemoveComment(false);
            }
            Utils.showSnackBar(activity, mDrawer, getString(R.string.error_retry_text));
        }
    }

    private int getCommentIdByText(Comment comment, ArrayList<Comment> comments) {
        for(Comment comment1 : comments){
            if(comment1.getText().equals(comment.getText())){
                return comment1.getId();
            }
        }
        return -1;
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
        Intent intent = new Intent();
        switch (type){
            case ARTICLE: intent.putExtra("object", article); break;
            case QUESTION: intent.putExtra("object", question); break;
            case VIDEO: intent.putExtra("object", video); break;
        }
        setResult(RESULT_OK, intent);
        finish();
    }


}
