package com.hope.commonsense.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hope.commonsense.R;
import com.hope.commonsense.Utils;


/**
 * Created by User on 4/19/2017.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts.ttf");


    public SpinnerAdapter(Context context, int resource, String[] items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(font);
        int valueInPixels = (int)getContext().getResources().getDimension(R.dimen.activity_question_spinner_title);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPxToDp(valueInPixels));
        view.setTextColor(getContext().getResources().getColor(R.color.black));
        return view;
    }

    // Affects opened state of the spinner
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(font);
        int valueInPixels = (int)getContext().getResources().getDimension(R.dimen.activity_question_spinner_title);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, Utils.convertPxToDp(valueInPixels));
        view.setTextColor(getContext().getResources().getColor(R.color.black));
        return view;
    }

}
