package com.hope.commonsense.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.User;
import com.hope.commonsense.JsonParser;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;
import com.hope.commonsense.Web.WebStatistics;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

public class LoginActivity extends MyActivity {

    String TAG = "LoginActivity";
    LinearLayout parent;
    RelativeLayout loadingLayout;
    EditText mUsernameEdt;
    EditText mPasswordEdt;
    ProgressBar mForgotPassProgressBar;
    TextInputLayout mForgotPassEdt;

    Typeface mTypeface;
    String usernameTxt;
    String passwordTxt;
    User user;

    boolean isEmailSent = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        onCreateViews();
    }


    private void onCreateViews() {
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.back_layout);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        parent = (LinearLayout) findViewById(R.id.parent);
        Button loginBtn = (Button) findViewById(R.id.btn_login);
        mUsernameEdt = (EditText) findViewById(R.id.username);
        mPasswordEdt = (EditText) findViewById(R.id.password);
        TextView notRegistered = (TextView) findViewById(R.id.not_registered_before);
        TextView forgotPasswordTxt = (TextView) findViewById(R.id.forgot_password);

        mTypeface = Typeface.createFromAsset(getAssets(), TextViewWithFont.Font);
        mUsernameEdt.setTypeface(mTypeface);
        mPasswordEdt.setTypeface(mTypeface);
        loginBtn.setTypeface(mTypeface);

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, RegisterActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsernameEdt.setTypeface(Typeface.DEFAULT);
                mPasswordEdt.setTypeface(Typeface.DEFAULT);
                usernameTxt = mUsernameEdt.getText().toString().trim();
                passwordTxt = mPasswordEdt.getText().toString().trim();


                if(!usernameTxt.equals("")){
                    if(!passwordTxt.equals("")){
                        sendRequest();
                    } else{
                        Utils.showSnackBar(activity, parent, getString(R.string.enter_password));
                    }
                } else {
                    Utils.showSnackBar(activity, parent, getString(R.string.enter_phone_number));
                }

                mUsernameEdt.setTypeface(mTypeface);
                mPasswordEdt.setTypeface(mTypeface);
            }
        });

        forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });


    }



    private void doLogin(){
        try{
            SharedPreferences prefs = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
            user.setToken(prefs.getString(DataStore.PREFERENCES_KEY_TOKEN, ""));
            DataStore.currentUser = user;
            DataStore.isUserRegistered = true;

            prefs.edit()
                    .putString(DataStore.PREFERENCES_KEY_USERNAME, usernameTxt)
                    .putString(DataStore.PREFERENCES_KEY_NICKNAME, user.getNickname())
                    .putString(DataStore.PREFERENCES_KEY_PASSWORD, passwordTxt)
                    .putString(DataStore.PREFERENCES_KEY_STATUS, user.getStatus())
                    .putString(DataStore.PREFERENCES_KEY_IMAGE, user.getImage())
                    .putString(DataStore.PREFERENCES_KEY_EMAIL, user.getEmail())
//                    .putString(DataStore.PREFERENCES_KEY_TOKEN, user.getToken())
                    .putBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, true)
                    .commit();
            Log.e(TAG, user.getToken()+"..");

            finish();
        } catch (Exception ex){
            Log.e(TAG, ex.getMessage()+"");
        }
    }

// -------------------------------------------------------------------------------------------------

    public void sendRequestForgotPassword(String username){
        requestCode = 2;
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_LOGIN);
        super.sendRequest();
    }

    @Override
    public void sendRequest() {
        loadingLayout.setVisibility(View.VISIBLE);
        resizeView(loadingLayout, findViewById(R.id.form_layout));
        requestCode = 1;
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_LOGIN);
        parameters.put(WebStatistics.KEY_USERNAME, usernameTxt);
        parameters.put(WebStatistics.KEY_PASSWORD, passwordTxt);
        super.sendRequest();
    }

    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);

        loadingLayout.setVisibility(View.GONE);
        try {
            if(result) {
                if(requestCode == 1){
                    user = JsonParser.jsonToUser(new JSONObject(data).getJSONObject("Body"));
                    Log.e(TAG, "1");
                    doLogin();
                } if(requestCode == 2){
                    mForgotPassProgressBar.setVisibility(View.GONE);
                    if(!result){
                        mForgotPassEdt.setError("error");
                    } else {
                        isEmailSent = true;
                        mForgotPassEdt.setError(getString(R.string.email_has_been_sent));
                        setErrorTextColor(mForgotPassEdt, getResources().getColor(R.color.text_layout_success));
                    }
                }
            } else {
                Utils.showSnackBar(activity, parent, new JSONObject(data).getString("ResultMessage"));
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage()+"");
            e.printStackTrace();
        }

    }

// -------------------------------------------------------------------------------------------------
    private void resizeView(RelativeLayout layout1, View layout2) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout2.getLayoutParams();
        layout1.setLayoutParams(params);
    }

    private void showForgotPasswordDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_layout_edittext, null);
        mForgotPassEdt = (TextInputLayout) dialogView.findViewById(R.id.email_wrapper);
        mForgotPassProgressBar = (ProgressBar) dialogView.findViewById(R.id.progressBar);
        alertDialogBuilder.setView(dialogView);

        alertDialogBuilder
            .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(isEmailSent){
                            isEmailSent = false;
                            dialog.dismiss();
                        } else {
                            String input = mForgotPassEdt.getEditText().getText().toString();
                            Log.e("sami",input);
                            if (TextUtils.isEmpty((input))) {
                                mForgotPassEdt.setErrorEnabled(true);
                                mForgotPassEdt.setError(getString(R.string.enter_nickname));
                            } else if (!Utils.isEmailValid(input)) {
                                mForgotPassEdt.setErrorEnabled(true);
                                mForgotPassEdt.setError(getString(R.string.invalid_email));
                            } else {
                                mForgotPassProgressBar.setVisibility(View.VISIBLE);
                                sendRequestForgotPassword(input);
                            }
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    public static void setErrorTextColor(TextInputLayout textInputLayout, int color) {
        try {
            Field fErrorView = TextInputLayout.class.getDeclaredField("mErrorView");
            fErrorView.setAccessible(true);
            TextView mErrorView = (TextView) fErrorView.get(textInputLayout);
            Field fCurTextColor = TextView.class.getDeclaredField("mCurTextColor");
            fCurTextColor.setAccessible(true);
            fCurTextColor.set(mErrorView, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// -------------------------------------------------------------------------------------------------

}
