package com.hope.commonsense.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hope.commonsense.Activities.TagActivity;
import com.hope.commonsense.Adapters.VideosAdapter;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Web.WebStatistics;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;


public class VideoFragment extends TagFragment {

    RecyclerView mRecycler;
    View mLoading;
    TextView noDataText;
    VideosAdapter mAdapter;
    ProgressBar mProgressBar;

    ArrayList<Video> videos;
    boolean requestFlag=true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        videos = new ArrayList<>();
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
        getVideos();
        return v;
    }


    @Override
    public void bringToFront() {
        super.bringToFront();
        getVideos();
    }

// -------------------------------------------------------------------------------------------------

    private void setupRecycler(){
        mAdapter = new VideosAdapter(getActivity(), videos);
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
                getVideos();
            }

        });
    }
// -------------------------------------------------------------------------------------------------

    public void getVideos(){
        if(!requestFlag){
            return;
        } else{
            requestFlag = false;
        }

        if(videos.size() > 0){
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
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_VIDEOS);
        parameters.put(WebStatistics.KEY_INDEX, String.valueOf(videos.size()>0?videos.get(videos.size()-1).getId():0));
        parameters.put(WebStatistics.KEY_LIMIT, String.valueOf(WebStatistics.HOME_ITEMS_LIMIT));
        parameters.put(WebStatistics.KEY_TYPE, String.valueOf(WebStatistics.TYPE_BY_TAG));
        parameters.put(WebStatistics.KEY_CATEGORY_ID, String.valueOf(0));
        parameters.put(WebStatistics.KEY_TAG_ID, String.valueOf(TagActivity.activity.currentTag.getId()));
        TagActivity.activity.sendRequest(parameters, TagActivity.VIDEO);
    }


    @Override
    public void parseData(JSONArray array) {
        videos.addAll(JsonParser.jsonToVideos(array));
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


    @Override
    public void refreshList() {
        videos.clear();
        getVideos();
    }

}
