package com.hope.commonsense.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hope.commonsense.Activities.MyActivity;
import com.hope.commonsense.Activities.TagActivity;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.AgeRange;
import com.hope.commonsense.Entities.Category;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 5/22/2017.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>  {

    MyActivity activity;
    ArrayList<Category> categories;
    LayoutInflater inflater;

    public CategoryAdapter(MyActivity activity, ArrayList<Category> categories) {
        this.activity = activity;
        this.categories = categories;
        inflater = LayoutInflater.from(activity);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_row_category, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Category category = categories.get(position);

        holder.title.setText(category.getTitle());

        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < categories.get(position).getTags().size(); i++) {
            View view = inflater.inflate(R.layout.tag_layout, holder.tagsLayout, false);
            final LinearLayout tag = (LinearLayout) view;
            TextView text = (TextView) tag.findViewById(R.id.text);
            text.setText(category.getTags().get(i).getTitle());
            final int finalI = i;
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.startActivity(new Intent(activity, TagActivity.class).putExtra("tag", category.getTags().get(finalI)));
                }
            });
            views.add(tag);
        }
        Utils.populateTags(holder.tagsLayout, views, activity);
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        LinearLayout tagsLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            tagsLayout = (LinearLayout) itemView.findViewById(R.id.tags_layout);
        }
    }

}
