package com.hope.commonsense.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hope.commonsense.Activities.ArticleActivity;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.R;
import com.hope.commonsense.Views.TextViewWithFont;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by User on 5/27/2017.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    ArrayList<Article> articles;
    public boolean isHeightMatchParent=false;

    public ArticlesAdapter(Context context, ArrayList<Article> articles) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.articles=articles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(isHeightMatchParent){
            view = inflater.inflate(R.layout.list_row_article_rec, parent, false);
        } else {
            view = inflater.inflate(R.layout.list_row_article, parent, false);
        }
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), TextViewWithFont.Font);
        holder.title.setTypeface(mTypeface);
        holder.title.setText(articles.get(position).getTitle());
        Picasso.with(context).load(articles.get(position).getImage()).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ArticleActivity.class).putExtra("article", articles.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }


}
