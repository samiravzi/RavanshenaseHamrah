package com.hope.commonsense.Adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hope.commonsense.Activities.MyActivity;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.AgeRange;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;

import java.util.ArrayList;

/**
 * Created by User on 5/22/2017.
 */
public class AgeListAdapter extends RecyclerView.Adapter<AgeListAdapter.MyViewHolder>  {

    MyActivity activity;
    ArrayList<AgeRange> ages;
    public ArrayList<AgeRange> selectedAges;

    int lastPosition=0;
    AgeRange selected;
    int selectedIndex=0;


    public AgeListAdapter(MyActivity activity) {
        this.activity = activity;
        ages = new ArrayList<>();
        selectedAges = new ArrayList<>();
        selectedAges.add(DataStore.ages.get(0));
        ages.addAll(DataStore.ages);
        activity.refreshAgeTitle(getTitles());
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.list_row_age, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Typeface mTypeface = Typeface.createFromAsset(activity.getAssets(), TextViewWithFont.Font);
        final AgeRange age = ages.get(position);
        holder.text.setText(age.getTitle());
        holder.text.setTypeface(mTypeface);

        if(position == selectedIndex){
            holder.text.setBackgroundResource(R.drawable.age_bg_selected);
        } else {
            holder.text.setBackgroundResource(R.drawable.age_bg);
        }

       /* if(selectedAges.contains(age)){
            holder.text.setBackgroundResource(R.drawable.age_bg_selected);
        } else {
            holder.text.setBackgroundResource(R.drawable.age_bg);
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(position == 0 || lastPosition == 0){
                    selectedAges.clear();
                }

                if(selectedAges.contains(ages.get(position))){
                    if(selectedAges.size()>1){
                        selectedAges.remove(ages.get(position));
                    }
                } else {
                    selectedAges.add(ages.get(position));
                }*/

                selectedIndex = position;
                notifyDataSetChanged();
                activity.refreshList();
                activity.refreshAgeTitle(getTitles());

                lastPosition = position;

               /* if(position == 0){ // all ages
                    activity.refreshAgeTitle(age.getTitle(), true);
                } else {
                    activity.refreshAgeTitle(age.getTitle(), false);
                }

                toggleLikeState(position, holder.text);
                activity.refreshList();

                if(selectedIndex==0 || position==0){
                    notifyDataSetChanged();
                }
                selectedIndex = position;*/
            }
        });


//        AgeRange ar = ages.get(position);
        /*if(ageRange == ALL || ageRange.getMin()==99){
            if(position == positionALL){
                setSelected(holder);
            } else {
                setUnSelected(holder);
            }
        } else {
            if((ar.getMin() >= ageRange.getMin() && ar.getMax()<= ageRange.getMax())) {
                setSelected(holder);
            } else {
                setUnSelected(holder);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position == positionALL){
                    ageRange = ALL;
                } else {
                    updateAgeRangValue(position);
                }

                notifyDataSetChanged();
                activity.refreshAgeTitle(ageRange.getText());
                activity.refreshList();
            }
        });*/
    }

    private String getTitles() {
        /*Utils.sortArray(selectedAges);
        StringBuilder sb = new StringBuilder();
        for(AgeRange age : selectedAges){
            Log.e("adapter","selected: "+age.getTitle());
            sb.append(age.getTitle());
            sb.append("،");
        }

        if(sb.toString().contains("،")){
            sb.deleteCharAt(sb.toString().lastIndexOf("،"));
        }
        return sb.toString(); */
        return ages.get(selectedIndex).getTitle();
    }

/*    private void updateAgeRangValue(int position) {
        if(ageRange == ALL){
            ageRange = new AgeRange(99,0);
        }

        int newMin = ages.get(position).getMin();
        int newMax = ages.get(position).getMax();

        if(newMin >= ageRange.getMin() && newMax<=ageRange.getMax()){
            int average = (ageRange.getMax() + ageRange.getMin()) / 2;
            if(newMin >= average){
                ageRange.setMin(newMax + 1);
            } else {
                ageRange.setMin(newMax + 1);
            }

        } else {
            if(newMin < ageRange.getMin()){
                ageRange.setMin(newMin);
            } else if(newMax > ageRange.getMax()){
                ageRange.setMax(newMax);
            }
        }

        if(ageRange.getMax() == 0){
            ageRange.setMax(newMax);
        }
        if(ageRange.getMin() == 0){
            ageRange.setMin(newMin);
        }


    }*/

    @Override
    public int getItemCount() {
        return DataStore.ages.size();
    }


    private void toggleLikeState(int position, TextView holder){
        AgeRange age = ages.get(position);
        if(age.isSelected()){
            age.setSelected(false);
            holder.setBackgroundResource(R.drawable.age_bg);
        } else {
            age.setSelected(true);
            holder.setBackgroundResource(R.drawable.age_bg_selected);
        }
    }


    public void setSelected(MyViewHolder holder){
        holder.text.setBackgroundResource(R.drawable.age_bg_selected);
        holder.text.setTextColor(Color.WHITE);
    }

    public void setUnSelected(MyViewHolder holder){
        holder.text.setBackgroundResource(R.drawable.age_bg);
        holder.text.setTextColor(activity.getResources().getColor(R.color.black));
    }


    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView text;

        public MyViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

}
