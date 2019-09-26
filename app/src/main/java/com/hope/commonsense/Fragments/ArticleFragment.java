package com.hope.commonsense.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hope.commonsense.Activities.TagActivity;
import com.hope.commonsense.Adapters.ArticlesAdapter;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Web.WebStatistics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ArticleFragment extends TagFragment {

    RecyclerView mRecycler;
    View mLoading;
    TextView noDataText;
    ArticlesAdapter mAdapter;
    ProgressBar mProgressBar;

    ArrayList<Article> articles;
    boolean requestFlag=true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        articles = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tag, container, false);
        mRecycler = (RecyclerView) v.findViewById(R.id.recycler);
        mLoading = v.findViewById(R.id.loading);
        noDataText = (TextView) v.findViewById(R.id.no_data);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressbar);

        setupRecycler();
        getArticles();
        return v;
    }


    @Override
    public void bringToFront() {
        super.bringToFront();
        getArticles();
    }

// -------------------------------------------------------------------------------------------------

    private void setupRecycler(){
        mAdapter = new ArticlesAdapter(getActivity(), articles);
        mAdapter.isHeightMatchParent = true;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
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
                Log.e("sami","list bottom");
                getArticles();
            }

        });
    }

// -------------------------------------------------------------------------------------------------

    @Override
    public void refreshList() {
        articles.clear();
        getArticles();
    }

    public void getArticles(){
        if(!requestFlag){
            return;
        } else{
            requestFlag = false;
        }

        if(articles.size() > 0){
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

        HashMap<String, String > parameters = new HashMap<>();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_ARTICLES);
        parameters.put(WebStatistics.KEY_INDEX, String.valueOf(articles.size()>0?articles.get(articles.size()-1).getId():0));
        parameters.put(WebStatistics.KEY_LIMIT, String.valueOf(WebStatistics.HOME_ITEMS_LIMIT));
        parameters.put(WebStatistics.KEY_TYPE, String.valueOf(WebStatistics.TYPE_BY_TAG));
        parameters.put(WebStatistics.KEY_CATEGORY_ID, String.valueOf(0));
        parameters.put(WebStatistics.KEY_TAG_ID, String.valueOf(TagActivity.activity.currentTag.getId()));
        TagActivity.activity.sendRequest(parameters, TagActivity.ARTICLE);
    }


    @Override
    public void parseData(JSONArray array) {
        articles.addAll(JsonParser.jsonToArticles(array));
        refreshViews();
    }


    private void refreshViews(){
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

        requestFlag = true;
    }


}
