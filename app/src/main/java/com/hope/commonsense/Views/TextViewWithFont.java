package com.hope.commonsense.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewWithFont extends TextView {

    public static String Font = "fonts.ttf";

    public TextViewWithFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), Font);
        this.setTypeface(face);
    }

    public TextViewWithFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), Font);
        this.setTypeface(face);
    }

    public TextViewWithFont(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), Font);
        this.setTypeface(face);
    }

}