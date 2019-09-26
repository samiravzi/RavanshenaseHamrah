package com.hope.commonsense.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hope.commonsense.Activities.SearchActivity;
import com.hope.commonsense.Adapters.SearchResultAdapter;
import com.hope.commonsense.Entities.SearchResult;
import com.hope.commonsense.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 6/4/2017.
 */

public class MyFragment extends Fragment {

    int type;
    RecyclerView mRecycler;
    public ArrayList<SearchResult> searchResults = new ArrayList<>();

    public void setType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_fragment, container, false);
        mRecycler = (RecyclerView) v.findViewById(R.id.list);

        return v;
    }


    public void refreshList(){
        if(searchResults.size() > 0){
            mRecycler.setVisibility(View.VISIBLE);
            mRecycler.setLayoutManager(new LinearLayoutManager(SearchActivity.activity, LinearLayoutManager.VERTICAL, false));
            SearchResultAdapter mAdapter = new SearchResultAdapter(SearchActivity.activity, searchResults);
            mRecycler.setAdapter(mAdapter);
        }
        else {
            mRecycler.setVisibility(View.GONE);
        }
    }


}
