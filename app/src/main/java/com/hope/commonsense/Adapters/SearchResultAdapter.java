package com.hope.commonsense.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hope.commonsense.Activities.MyActivity;
import com.hope.commonsense.Activities.SearchActivity;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.SearchResult;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.MyViewHolder>  {


    SearchActivity activity;
    ArrayList<SearchResult> searchResults;


    public SearchResultAdapter(SearchActivity activity, ArrayList<SearchResult> searchResults) {
        this.activity = activity;
        this.searchResults = searchResults;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView title;

        public MyViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            type = (TextView) v.findViewById(R.id.type);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = activity.getLayoutInflater().inflate(R.layout.list_row_search, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.title.setText(searchResults.get(position).getTitle()+"");
        final String type = getType(position);
        if(type.equals("")){
            holder.type.setVisibility(View.GONE);
        } else {
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setText(type);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.activity.getItemById(searchResults.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }


    private String getType(int position) {
        String text = "";
        if(activity.type == activity.TYPE_ALL){
            switch (searchResults.get(position).getType()){
                case 1: text = "مقاله"; break;
                case 2: text = "پرسش و پاسخ"; break;
                case 3: text = "ویدئو"; break;
            }
        } else {
            text="";
        }
        return text;
    }
}
