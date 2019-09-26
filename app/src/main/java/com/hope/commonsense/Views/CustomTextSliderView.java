package com.hope.commonsense.Views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.hope.commonsense.R;


public class CustomTextSliderView extends BaseSliderView {

    private static Typeface font = null;
    private Context context ;

    public CustomTextSliderView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text,null);
        ImageView target = (ImageView)v.findViewById(R.id.daimajia_slider_image);
        TextView description = (TextView)v.findViewById(R.id.description);
        description.setText(getDescription());
//        description.setTextSize(getContext().getResources().getDimension(R.dimen.activity_main_Lists_title_size));
        description.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.activity_main_Lists_title_size2));
        if(font == null){
            font = Typeface.createFromAsset(context.getAssets(), TextViewWithFont.Font);
        }

        LinearLayout descLayout = (LinearLayout) v.findViewById(R.id.description_layout);
        descLayout.setPadding(30, 50, 30, 50);

        description.setTypeface(font);
        bindEventAndShow(v, target);
        return v;
    }

}
