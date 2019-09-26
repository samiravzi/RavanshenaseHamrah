package com.hope.commonsense.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hope.commonsense.Activities.VideoActivity;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.R;
import com.hope.commonsense.Views.TextViewWithFont;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ir.noghteh.JustifiedTextView;

/**
 * Created by User on 2/18/2017.
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {

    Context context;
    ArrayList<Video> videos;
    PopupWindow popupWindowObj;
    public boolean isHeightMatchParent=false;
    LayoutInflater inflater;


    public VideosAdapter(Context context, ArrayList<Video> videos) {
        this.context = context;
        this.videos = videos;
        inflater = LayoutInflater.from(context);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        public MyViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            image = (ImageView) v.findViewById(R.id.image);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(isHeightMatchParent){
            view = inflater.inflate(R.layout.list_row_video_rec, parent, false);
        } else {
            view = inflater.inflate(R.layout.list_row_video, parent, false);
        }
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), TextViewWithFont.Font);
        holder.title.setTypeface(mTypeface);
        holder.title.setText(videos.get(position).getTitle());

        Picasso.with(context).load(videos.get(position).getImage()).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, VideoActivity.class).putExtra("video", videos.get(position)));
            }
        });

      /*  if(comments.get(position).getUser().getUniqueId().equals(DataStore.currentUser.getUniqueId())){
            holder.actionBtn.setVisibility(View.VISIBLE);
        } else {
            holder.actionBtn.setVisibility(View.GONE);
        }

        holder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowObj = popupDisplay(position);
                popupWindowObj.showAsDropDown(holder.moreIcon, -20, 18);
            }
        });*/


    }


    @Override
    public int getItemCount() {
        return videos.size();
    }


    private PopupWindow popupDisplay(final int position){

        final PopupWindow popupWindow = new PopupWindow(context);
        /*LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_card_comment, null);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        TextView delete = (TextView) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.photoActivity.removeComment(position);
                popupWindow.dismiss();
            }
        });*/

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
