package com.hope.commonsense.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hope.commonsense.Adapters.AgeListAdapter;
import com.hope.commonsense.Adapters.NothingSelectedSpinnerAdapter;
import com.hope.commonsense.Adapters.QuestionsAdapter;
import com.hope.commonsense.Adapters.SpinnerAdapter;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.AgeRange;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;
import com.hope.commonsense.Web.WebStatistics;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class QuestionListActivity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "QListActivity";
    MyActivity activity;

    DrawerLayout mDrawer;
    NavigationView mNavigation;
    Button mNavigationBtn;
    RecyclerView ageList;
    Spinner categoriesSpinner;
    ImageView spinnerArrow;
    RecyclerView mRecycler;
    View mLoading;
    TextView ageTitle;
    TextViewWithFont noDataText;
    QuestionsAdapter mAdapter;
    TextView collapseToolbarTitle;
    ProgressBar mProgressBar;
    LinearLayout mLogout;
    Toolbar toolbar;

    AgeListAdapter ageListAdapter;
    Typeface mTypeface;
    ArrayList<Question> questions;
    int categoryId = 0;
    String currentAgesTitle;
    boolean lockRequest = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        activity = this;
        questions = new ArrayList<>();
        /*if(MainActivity.mainActivity.questions != null) {
            questions.addAll(MainActivity.mainActivity.questions);
        }*/

        categoryId = getIntent().getIntExtra("catId", 0);
        onCreateView();

        getQuestions();
    }


    private void onCreateView(){
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mLoading = findViewById(R.id.loading);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        categoriesSpinner = (Spinner) findViewById(R.id.spinner);
        spinnerArrow = (ImageView) findViewById(R.id.spinnerArrow);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mNavigationBtn = (Button) findViewById(R.id.btn_navigation);
        ageList = (RecyclerView) findViewById(R.id.age_list);
        ageTitle = (TextView) findViewById(R.id.age_title);
        noDataText = (TextViewWithFont) findViewById(R.id.no_data);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapseToolbarTitle = (TextView) findViewById(R.id.collapse_toolbar_title);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mLogout = (LinearLayout) findViewById(R.id.logout);
        Button searchBtn = (Button) findViewById(R.id.btn_search);

        mTypeface = Typeface.createFromAsset(getAssets(), TextViewWithFont.Font);
        collapseToolbarTitle.setTypeface(mTypeface);

        toolbarTitle.setText(getString(R.string.activities_title));
        toolbar.setVisibility(View.GONE);

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

        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        setupNavigationDrawer();
        setupCategoriesSpinner();
        setupAgeList();
        setupRecycler();

    }

// -------------------------------------------------------------------------------------------------

    private void doLogout() {
        DataStore.isUserRegistered = false;
        SharedPreferences preferences = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, false).commit();
        setupNavigationHeader();
    }
// -------------------------------------------------------------------------------------------------

    private void getQuestions(){
        if(!lockRequest){
            lockRequest = true;
            if(questions.size() > 0){
                mProgressBar.setVisibility(View.VISIBLE);
                mRecycler.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.GONE);
                noDataText.setVisibility(View.GONE);
            } else {
                mRecycler.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                noDataText.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }

            requestCode = 1;
            parameters.clear();
            parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_QUESTIONS);
            sendRequest();
        }
    }

    public void sendRequest(){
        parameters.put(WebStatistics.KEY_INDEX, String.valueOf(questions.size()>0?questions.get(questions.size()-1).getId():0));
        parameters.put(WebStatistics.KEY_LIMIT, String.valueOf(WebStatistics.HOME_ITEMS_LIMIT));
        if(categoryId>0){
            parameters.put(WebStatistics.KEY_TYPE, String.valueOf(WebStatistics.TYPE_BY_CATEGORY));
        } else {
            parameters.put(WebStatistics.KEY_TYPE, String.valueOf(WebStatistics.TYPE_MOST_VISITED));
        }
        parameters.put(WebStatistics.KEY_AGES, JsonParser.agesToJson(ageListAdapter.selectedAges));
        parameters.put(WebStatistics.KEY_CATEGORY_ID, String.valueOf(categoryId));
        parameters.put(WebStatistics.KEY_TAG_ID, String.valueOf(0));
        super.sendRequest();
    }

    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);
        mLoading.setVisibility(View.GONE);
        lockRequest = false;
        try {
            if(result){
                if(requestCode == 1){
                    JSONArray array = new JSONObject(data).getJSONArray("Body");
                    questions.addAll(JsonParser.jsonToQuestions(array));
                    refreshRecycler();
                }
            }
        } catch (Exception ex){
        }
    }

    @Override
    public void refreshList() {
        lockRequest = false;
        questions.clear();
        getQuestions();
    }

    private void refreshRecycler(){
        mProgressBar.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        if(mAdapter.getItemCount() == 0){
            noDataText.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            noDataText.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void refreshAgeTitle(String newTitle){
        ageTitle.setText(newTitle);
    }


// -------------------------------------------------------------------------------------------------

    private void setupCategoriesSpinner(){
        if(DataStore.categories == null || DataStore.categories.size() == 0){
            return;
        } else {
            String[] datesTitle = Utils.getTitles(DataStore.categories);
            ArrayAdapter dataAdapter =  new SpinnerAdapter(QuestionListActivity.this, R.layout.list_row_spinner, datesTitle);
            dataAdapter.setDropDownViewResource(R.layout.list_row_spinner);
            categoriesSpinner.setPrompt(getString(R.string.all_categories));
            categoriesSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                    dataAdapter,
                    R.layout.list_row_spinner_nothing_selected,
                    this));

            categoriesSpinner.post(new Runnable() {
                @Override
                public void run() {
                    TextView spinnerText = (TextView) categoriesSpinner.getChildAt(0);
                    spinnerText.setTextColor(getResources().getColor(R.color.black));
                }
            });


            categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    if (DataStore.categories.size()>0 && position!=0) {
                        categoryId = DataStore.categories.get(position-1).getId();
                        refreshList();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinnerArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    categoriesSpinner.performClick();
                }
            });

            categoriesSpinner.setSelection(DataStore.getCategoryIndex(categoryId));
        }
    }

    private void setupAgeList() {
//        ageTitle.setText(DataStore.ages.get(DataStore.ages.size()-1).getText());
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(activity);
        myLayoutManager.setReverseLayout(true);
        myLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ageList.setLayoutManager(myLayoutManager);
        ageListAdapter = new AgeListAdapter(activity);
        ageList.setAdapter(ageListAdapter);
    }

    private void setupNavigationDrawer(){
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

    private void setupRecycler() {
        Log.e(TAG, "setup question: " + questions.size());
        mRecycler.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);

        mAdapter = new QuestionsAdapter(activity, questions);
        mAdapter.isHeightMatchParent = true;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 2);
        mRecycler.setLayoutManager(gridLayoutManager);
        mRecycler.setAdapter(mAdapter);

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                getQuestions();
            }

        });
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

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setHorizontalScrollBarEnabled(true);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        getSupportActionBar().setTitle(null);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    String text = "";
                    if (categoriesSpinner.getSelectedItem() != null) {
                        text = categoriesSpinner.getSelectedItem().toString();
                    } else {
                        text = getString(R.string.all_categories);
                    }
                    text = text + ": " + currentAgesTitle;
                    collapseToolbarTitle.setText(text);
                    collapseToolbarTitle.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
//                    collapseToolbarTitle.setSelected(true);
                    isShow = true;
                } else if (isShow) {
                    collapseToolbarTitle.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
    }

// -------------------------------------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        setupNavigationHeader();
    }

    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(mNavigation)){
            mDrawer.closeDrawer(mNavigation);
        } else {
            super.onBackPressed();
        }
    }


}
