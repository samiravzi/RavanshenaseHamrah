package com.hope.commonsense.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hope.commonsense.Activities.CommentActivity;
import com.hope.commonsense.Activities.ProfileActivity;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.Comment;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;

import java.util.ArrayList;

import ir.noghteh.JustifiedTextView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    CommentActivity context;
    ArrayList<Comment> comments;
    PopupWindow popupWindowObj;


    int removePosition=-1;

    public CommentAdapter(CommentActivity context, ArrayList<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public void doRemoveComment(boolean isRemoved) {
        if(isRemoved){
            comments.remove(removePosition);
        }
        removePosition = -1;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        JustifiedTextView text;
        LinearLayout moreBtn;
        Button moreIcon;
        RelativeLayout progressLayout;
        LinearLayout commentBody;
        ProgressBar progressBar;

        public MyViewHolder(View v) {
            super(v);
            text = (JustifiedTextView) v.findViewById(R.id.text);
            date = (TextView) v.findViewById(R.id.date);
            title = (TextView) v.findViewById(R.id.title);
            moreBtn = (LinearLayout) v.findViewById(R.id.btn_more);
            moreIcon = (Button) v.findViewById(R.id.icon_more);
            progressLayout = (RelativeLayout) v.findViewById(R.id.progressbar_layout);
            commentBody = (LinearLayout) v.findViewById(R.id.comment_body);
            progressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_comment, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.title.setText(comments.get(position).getTitle());
        holder.text.setText(comments.get(position).getText());
        holder.date.setText(Utils.getFormattedDate(comments.get(position).getDate()));
        setupTextFontAndSize(holder.text);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProfileActivity.class).putExtra("user", comments.get(position).getUser()));
            }
        });

        if(comments.get(position).getUser().getToken().equals(DataStore.currentUser.getToken())){
            holder.moreBtn.setVisibility(View.VISIBLE);
        } else {
            holder.moreBtn.setVisibility(View.GONE);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowObj = popupDisplay(position);
                popupWindowObj.showAsDropDown(holder.moreIcon, 10, 10);
            }
        });

        if(position == removePosition){
            resizeView(holder.commentBody, holder.progressLayout);
            holder.progressLayout.setVisibility(View.VISIBLE);
        } else {
            holder.progressLayout.setVisibility(View.GONE);
        }
    }


    private void resizeView(LinearLayout baseView, RelativeLayout currentView){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) baseView.getLayoutParams();
        currentView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private PopupWindow popupDisplay(final int position){

        final PopupWindow popupWindow = new PopupWindow(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_comment, null);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        TextView delete = (TextView) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePosition = position;
                notifyDataSetChanged();
                context.removeComment(comments.get(position).getId(), position);
                popupWindow.dismiss();
            }
        });

        return popupWindow;
    }

    private void setupTextFontAndSize(JustifiedTextView tv){
        Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), TextViewWithFont.Font);
        tv.setTextColor(context.getResources().getColor(R.color.black));
        tv.setTypeFace(mTypeface);
//        tv.setLineSpacing((int)getResources().getDimension(R.dimen.session_line_space));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int)context.getResources().getDimension(R.dimen.list_row_comment_text));
    }


}
