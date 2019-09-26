package com.hope.commonsense.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hope.commonsense.Activities.QuestionActivity;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.R;
import com.hope.commonsense.Views.TextViewWithFont;

import java.util.ArrayList;

/**
 * Created by User on 5/27/2017.
 */
public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    ArrayList<Question> questions;
    public boolean isHeightMatchParent=false;



    public QuestionsAdapter(Context context, ArrayList<Question> questions) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.questions=questions;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(isHeightMatchParent){
            view = inflater.inflate(R.layout.list_row_question_rec, parent, false);
        } else {
            view = inflater.inflate(R.layout.list_row_question, parent, false);
        }
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), TextViewWithFont.Font);
        holder.title.setTypeface(mTypeface);
        holder.title.setText(questions.get(position).getTitle());
        holder.commentCount.setText(questions.get(position).getCommentCount() + " " + context.getString(R.string.comment));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, QuestionActivity.class).putExtra("question", questions.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView commentCount;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            commentCount = (TextView) itemView.findViewById(R.id.commentCount);
        }
    }


}
