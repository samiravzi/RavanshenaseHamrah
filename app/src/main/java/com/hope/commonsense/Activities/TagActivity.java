package com.hope.commonsense.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hope.commonsense.Adapters.AgeListAdapter;
import com.hope.commonsense.Adapters.ViewPagerAdapter;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.Tag;
import com.hope.commonsense.Fragments.ArticleFragment;
import com.hope.commonsense.Fragments.QuestionFragment;
import com.hope.commonsense.Fragments.TagFragment;
import com.hope.commonsense.Fragments.VideoFragment;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;
import com.hope.commonsense.Web.WebServiceManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;


public class TagActivity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "TagActivity";
    public  static TagActivity activity;

    DrawerLayout mDrawer;
    NavigationView mNavigation;
    Button mNavigationBtn;
    ViewPager mViewPager;
    ViewPagerAdapter mAdapter;
    TabLayout mTabs;
    LinearLayout mLogout;
    TagFragment[] fragments;
    RecyclerView ageList;
    TextView ageTitle;
    AgeListAdapter ageListAdapter;
    TextView collapseToolbarTitle;
    Toolbar toolbar;

    Typeface mTypeface;
    public Tag currentTag;
    int currentFragmentIndex=2;

    public static final int ARTICLE = 2;
    public static final int QUESTION = 1;
    public static final int VIDEO = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        activity = this;
        currentTag = (Tag) getIntent().getSerializableExtra("tag");

        onCreateView();
    }


    private void onCreateView(){
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigation = (NavigationView) findViewById(R.id.nav_view);
        mNavigationBtn = (Button) findViewById(R.id.btn_navigation);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabs = (TabLayout) findViewById(R.id.tabs);
        Button searchBtn = (Button) findViewById(R.id.btn_search);
        mLogout = (LinearLayout) findViewById(R.id.logout);
        ageList = (RecyclerView) findViewById(R.id.age_list);
        ageTitle = (TextView) findViewById(R.id.age_title);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapseToolbarTitle = (TextView) findViewById(R.id.collapse_toolbar_title);

        mTypeface = Typeface.createFromAsset(getAssets(), TextViewWithFont.Font);
        collapseToolbarTitle.setTypeface(mTypeface);
        toolbarTitle.setText(currentTag.getTitle());
        toolbar.setVisibility(View.GONE);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "search btn clicked");
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
        setupNavigationDrawer();
        setupViewPager();
        setupTabLayout();
//        setupAgeList();

        initCollapsingToolbar();
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
        fragments = new TagFragment[3];
        fragments[ARTICLE] = new ArticleFragment();
        fragments[QUESTION] = new QuestionFragment();
//        fragments[VIDEO] = new VideoFragment();
//        mAdapter.addFragment(fragments[VIDEO], getString(R.string.video));
        mAdapter.addFragment(fragments[QUESTION], getString(R.string.questions));
        mAdapter.addFragment(fragments[ARTICLE], getString(R.string.articles));
        mViewPager.setAdapter(mAdapter);
        Log.e(TAG, mAdapter.getCount()+"");
        final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageScrollStateChanged(int state) {}

            @Override
            public void onPageSelected(int position) {
                currentFragmentIndex = position;
//                fragments[position].bringToFront();
                // samir --> Ravanshenasi
                fragments[position+1].bringToFront();
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

    private void setupAgeList() {
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(activity);
        myLayoutManager.setReverseLayout(true);
        myLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ageList.setLayoutManager(myLayoutManager);
        ageListAdapter = new AgeListAdapter(activity);
        ageList.setAdapter(ageListAdapter);
    }

// -------------------------------------------------------------------------------------------------

    private void doLogout() {
        DataStore.isUserRegistered = false;
        SharedPreferences preferences = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
        preferences.edit().putBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, false).commit();
        setupNavigationHeader();
    }

// -------------------------------------------------------------------------------------------------


    public void sendRequest(HashMap<String, String> params, int requestCode){
        WebServiceManager webServiceManager = new WebServiceManager(activity, requestCode);
        webServiceManager.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);

        if(result){
            try {
                JSONArray array = new JSONObject(data).getJSONArray("Body");
                switch (requestCode){
                    case ARTICLE: fragments[ARTICLE].parseData(array); break;
                    case QUESTION: fragments[QUESTION].parseData(array); break;
                    case VIDEO: fragments[VIDEO].parseData(array); break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

// -------------------------------------------------------------------------------------------------

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setHorizontalScrollBarEnabled(true);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        getSupportActionBar().setTitle(null);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapseToolbarTitle.setText(currentTag.getTitle());
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
    public void refreshList() {
        fragments[currentFragmentIndex].refreshList();
    }


    @Override
    public void refreshAgeTitle(String newTitle){
        ageTitle.setText(newTitle);
    }

}
