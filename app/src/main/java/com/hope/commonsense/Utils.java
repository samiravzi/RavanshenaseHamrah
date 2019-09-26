package com.hope.commonsense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hope.commonsense.Entities.AgeRange;
import com.hope.commonsense.Entities.Article;
import com.hope.commonsense.Entities.Category;
import com.hope.commonsense.Entities.Question;
import com.hope.commonsense.Entities.Video;
import com.hope.commonsense.Views.CustomTypefaceSpan;
import com.hope.commonsense.Views.TextViewWithFont;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ir.smartlab.persindatepicker.util.PersianCalendar;

/**
 * Created by User on 5/21/2017.
 */
public class Utils {

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }


    public static void applyFont(Context context, NavigationView navigationView){
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, context);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi, context);
        }
    }

    private static void applyFontToMenuItem(MenuItem mi, Context ctx) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(), TextViewWithFont.Font);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public static String getFormattedDate(Calendar date) {
        PersianCalendar calendar = new PersianCalendar();
        calendar.setTime(date.getTime());
        return calendar.getPersianLongDate();
    }

    public static String getFormattedDatePastTime(Calendar calendar) {
        String text="";
        Calendar now = Calendar.getInstance();
        int diffInYear = now.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
        int diffInMonth = now.get(Calendar.MONTH) - calendar.get(Calendar.MONTH);
        //int diffInWeek = now.get(Calendar.WEEK_OF_MONTH) - calendar.get(Calendar.WEEK_OF_MONTH);
        int diffInDay = now.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
        int diffInHour = now.get(Calendar.HOUR_OF_DAY) - calendar.get(Calendar.HOUR_OF_DAY);
        int diffInMinuet = now.get(Calendar.MINUTE) - calendar.get(Calendar.MINUTE);

        if(diffInYear > 0){
            text = diffInYear +" سال پیش ";

        } else if(diffInMonth > 0) {
            text = diffInMonth + " ماه پیش";

        } /*else if(diffInWeek > 0){
                if(diffInDay > 0)
                    text = diffInWeek+" هفته و "+diffInDay+" روز پیش";
                else
                    text = diffInWeek+" هفته پیش";

        }*/ else if(diffInDay > 0){
            text = diffInDay+"روز پیش";

        } else if(diffInHour > 0){
            text = diffInHour+" ساعت پیش";

        } else if(diffInMinuet > 0) {
            text = "کمتر از 1 دقیقه پیش";
        }
        Log.e("Utils", "get time format : " + text);
        return text;
    }

    public static Calendar stringToDate(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = format.parse(dateStr);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static String[] getTitles(ArrayList<Category> categories) {
        String[] titles = new String[categories.size()];
        for(int i=0; i< categories.size(); i++){
            titles[i] = categories.get(i).getTitle();
        }
        return titles;
    }

    public static String generateToken(){
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

// -------------------------------------------------------------------------------------------------
    public static int convertDpToPx(int dp){
        return Math.round(dp*(Resources.getSystem().getDisplayMetrics().xdpi/ DisplayMetrics.DENSITY_DEFAULT));

    }

    public static int convertPxToDp(int px){
        return Math.round(px/(Resources.getSystem().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT));
    }
// -------------------------------------------------------------------------------------------------


    public static void changeTabsFont(Context context, TabLayout tabLayout) {
        Typeface mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts.ttf");

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(mTypeface);
                }
            }
        }
    }


    public static void populateTags(LinearLayout linearLayout, ArrayList<View> views, Context context) {
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        linearLayout.removeAllViews();
        int maxWidth = display.getWidth() - 20;

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(context);
        newLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        newLL.setGravity(Gravity.RIGHT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0; i < views.size(); i++)
        {
            LinearLayout LL = new LinearLayout(context);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.RIGHT);
            LL.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            views.get(i).measure(0, 0);
            params = new LinearLayout.LayoutParams(views.get(i).getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);

            if(views.get(i).getParent()!=null)
                ((ViewGroup)views.get(i).getParent()).removeView(views.get(i));
            LL.addView(views.get(i), params);
            LL.measure(0, 0);
            widthSoFar += views.get(i).getMeasuredWidth();
            if (widthSoFar >= maxWidth)
            {
                linearLayout.addView(newLL);

                newLL = new LinearLayout(context);
                newLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.RIGHT);
                params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            }
            else
            {
                newLL.addView(LL);
            }
        }
        linearLayout.addView(newLL);
    }

// -------------------------------------------------------------------------------------------------

    public static void showSnackBar(Activity activity, View view, String text){
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = (TextView) layout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View snackView = activity.getLayoutInflater().inflate(R.layout.snackbar_layout, null);
        TextView textViewTop = (TextView) snackView.findViewById(R.id.text);
        textViewTop.setText(text);

        layout.addView(snackView);
        snackbar.show();
    }


// -------------------------------------------------------------------------------------------------

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String base64(String filePath){
        try {
            InputStream inputStream = new FileInputStream(filePath);//You can get an inputStream using any IO API
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            bytes = output.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {

            e.printStackTrace();
        }

        return "";
    }



    public static void showKeyboard(Activity activity, View view){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideKeyboard(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static boolean isEmailValid(String input) {
        return input != null && android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

// -------------------------------------------------------------------------------------------------

    public static void showWebPage(Context context, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }


    public static void shareObject(Context context, Article article) {
        String title = article.getTitle();
        String content = article.getContent();
        String footer = context.getString(R.string.share_footer1) +" \""+ context.getString(R.string.app_name) +"\" "+ context.getString(R.string.share_footer2);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(title+"<br>"+content + "<br><br>" + footer));
        sendIntent.setType("text/html");
        context.startActivity(sendIntent);
    }

    public static void shareObject(Context context, Question question) {
        String title = question.getTitle();
        String content = question.getAnswer();
        String footer = context.getString(R.string.share_footer1) +" \""+ context.getString(R.string.app_name) +"\" "+ context.getString(R.string.share_footer2);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(title+"<br>"+content+"<br><br>"+footer));
        sendIntent.setType("text/html");
        context.startActivity(sendIntent);
    }

    public static void shareObject(Context context, Video video){
        String title = video.getTitle();
        String footer = context.getString(R.string.share_footer_video1) +" \""+ context.getString(R.string.app_name) +"\" "+ context.getString(R.string.share_footer2);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(title+"<br><br>"+footer));
        sendIntent.setType("text/html");
        context.startActivity(sendIntent);
    }



    public static void sortArray(ArrayList<AgeRange> objects){
        Collections.sort(objects, new Comparator<AgeRange>(){
            public int compare(AgeRange o1, AgeRange o2){
                if(o1.getPriority() == o2.getPriority())
                    return 0;
                return o1.getPriority() < o2.getPriority() ? -1 : 1;
            }
        });
    }


    public static int getScreenWidth(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }




}
