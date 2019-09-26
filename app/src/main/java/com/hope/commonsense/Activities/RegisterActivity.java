package com.hope.commonsense.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.User;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Views.TextViewWithFont;
import com.hope.commonsense.Web.WebStatistics;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterActivity extends MyActivity {

    LinearLayout parent;
    RelativeLayout loadingLayout;
    EditText phoneNumber;
    EditText password;
    EditText repassword;
    EditText nickname;

    Typeface mTypeface;
    String token;
    String usernameTxt;
    String nicknameTxt;
    String passwordTxt;
    String repasswordTxt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        onCreateViews();
    }

    private void onCreateViews() {
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.back_layout);
        parent = (LinearLayout) findViewById(R.id.parent);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        TextView registeredBefore = (TextView) findViewById(R.id.registered_before);
        TextView tryAgain = (TextView) findViewById(R.id.try_again);
        Button registerBtn = (Button) findViewById(R.id.btn_register);
        phoneNumber = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        nickname = (EditText) findViewById(R.id.nickname);

        mTypeface = Typeface.createFromAsset(getAssets(), TextViewWithFont.Font);
        phoneNumber.setTypeface(mTypeface);
        password.setTypeface(mTypeface);
        repassword.setTypeface(mTypeface);
        nickname.setTypeface(mTypeface);
        registerBtn.setTypeface(mTypeface);


        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        registeredBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, LoginActivity.class));
            }
        });

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameTxt = phoneNumber.getText().toString().trim();
                nicknameTxt = nickname.getText().toString().trim();
                passwordTxt = password.getText().toString().trim();
                repasswordTxt = repassword.getText().toString().trim();

                if(!usernameTxt.equals("")){
                    if(!nickname.equals("")){
                        if(checkPasswords()){
                            sendRequest();
                        } else {
                            Utils.showSnackBar(activity, parent, getString(R.string.passwords_dont_match));
                        }
                    } else{
                       Utils.showSnackBar(activity, parent, getString(R.string.enter_nickname));
                    }
                } else {
                    Utils.showSnackBar(activity, parent, getString(R.string.enter_phone_number));
                }
            }
        });

    }



    private boolean checkPasswords(){
        if(passwordTxt.equals(repasswordTxt)){
            return true;
        } else
            return false;
    }

    private void doRegister(){
        User user = new User();
        user.setToken(token);
        user.setUsername(usernameTxt);
        user.setNickname(nicknameTxt);
        user.setPassword(passwordTxt);
        user.setStatus("");
        user.setEmail("");
        user.setImage("");
        DataStore.currentUser = user;
        DataStore.isUserRegistered = true;

        SharedPreferences prefs = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(DataStore.PREFERENCES_KEY_USERNAME, user.getUsername())
                .putString(DataStore.PREFERENCES_KEY_NICKNAME, user.getNickname())
                .putString(DataStore.PREFERENCES_KEY_PASSWORD, user.getPassword())
                .putString(DataStore.PREFERENCES_KEY_STATUS, "")
                .putString(DataStore.PREFERENCES_KEY_IMAGE, "")
                .putString(DataStore.PREFERENCES_KEY_EMAIL, "")
                .putString(DataStore.PREFERENCES_KEY_TOKEN, user.getToken())
                .putBoolean(DataStore.PREFERENCES_IS_USER_LOGGED_IN, true)
                .commit();
        finish();
    }


// -------------------------------------------------------------------------------------------------
    public void sendRequest(){
        loadingLayout.setVisibility(View.VISIBLE);
        resizeView(loadingLayout, findViewById(R.id.form_layout));
        token = Utils.generateToken();
        requestCode = 1;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_REGISTER_USER);
        parameters.put(WebStatistics.KEY_TOKEN, token);
        parameters.put(WebStatistics.KEY_USERNAME, phoneNumber.getText().toString());
        parameters.put(WebStatistics.KEY_PASSWORD, password.getText().toString());
        parameters.put(WebStatistics.KEY_NICKNAME, nickname.getText().toString());
        super.sendRequest();
    }

    private void resizeView(RelativeLayout layout1, View layout2) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout2.getLayoutParams();
        layout1.setLayoutParams(params);
    }

    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);

        loadingLayout.setVisibility(View.GONE);
        try {
            if(result){
                if(requestCode == 1){
                    doRegister();
                }
            } else {
                Utils.showSnackBar(activity, parent, new JSONObject(data).getString("ResultMessage"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
