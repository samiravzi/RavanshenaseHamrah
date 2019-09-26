package com.hope.commonsense.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.hope.commonsense.DataStore;
import com.hope.commonsense.Entities.User;
import com.hope.commonsense.R;
import com.hope.commonsense.Utils;
import com.hope.commonsense.Web.WebStatistics;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;


public class ProfileActivity extends MyActivity{

    String TAG = "ProfileActivity";
    MyActivity activity;

    ProgressDialog progressDialog;
    User user;
    boolean isEditMode;
    boolean isImageChanged = false;
    boolean isMyProfile =false;
    int backCounter=0;

    LinearLayout parentLayout;
    TextInputLayout mNicknameWrapper;
    TextInputLayout mStatusWrapper;
    TextInputLayout mPhoneNumberWrapper;
    TextInputLayout mEmailWrapper;
    TextInputLayout mPasswordNumberWrapper;
    EditText mNicknameEdt;
    EditText mStatusEdt;
    EditText mPhoneNumberEdt;
    EditText mPasswordEdt;
    EditText mEmailEdt;
    TextView mEditLayoutTitle;
    ImageView mProfileImage;
    ImageView mAddNewProfileImage;
    View mLoading;
    NestedScrollView nestedScrollView;
    Button saveBtn;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        activity = this;

        user = (User) getIntent().getSerializableExtra("user");
        if(user.getUsername().equals(DataStore.currentUser.getUsername())){
            isMyProfile = true;
        }

        onCreateView();
    }


    private void onCreateView(){
        parentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        mNicknameWrapper = (TextInputLayout) findViewById(R.id.nickname_wrapper);
        mStatusWrapper = (TextInputLayout) findViewById(R.id.status_wrapper);
        mPhoneNumberWrapper = (TextInputLayout) findViewById(R.id.phone_number_wrapper);
        mEmailWrapper = (TextInputLayout) findViewById(R.id.email_wrapper);
        mPasswordNumberWrapper = (TextInputLayout) findViewById(R.id.password_wrapper);
        mNicknameEdt = (EditText) findViewById(R.id.nickname);
        mStatusEdt = (EditText) findViewById(R.id.status);
        mPhoneNumberEdt = (EditText) findViewById(R.id.phone_number);
        mPasswordEdt = (EditText) findViewById(R.id.password);
        mEmailEdt = (EditText) findViewById(R.id.email);
        final LinearLayout editLayout = (LinearLayout) findViewById(R.id.edit_layout);
        LinearLayout backLayout = (LinearLayout) findViewById(R.id.back_layout);
        mEditLayoutTitle = (TextView) findViewById(R.id.edit_layout_title);
        RelativeLayout imageLayout = (RelativeLayout) findViewById(R.id.profile_image_layout);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mAddNewProfileImage = (ImageView) findViewById(R.id.add_new_image);
        mLoading = findViewById(R.id.loading);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scrollView);
        saveBtn = (Button) findViewById(R.id.btn_save);

        mPhoneNumberEdt.setText(user.getUsername());
        mNicknameEdt.setText(user.getNickname());
        mEmailEdt.setText(user.getEmail());
        mStatusEdt.setText(user.getStatus());
        mPasswordEdt.setText(user.getPassword());

        mStatusWrapper.setErrorEnabled(true);
        mStatusWrapper.setError(getString(R.string.status_sample));
        setErrorTextColor(mStatusWrapper, getResources().getColor(R.color.dark_gray2));

        mPhoneNumberWrapper.setErrorEnabled(true);
        mPhoneNumberWrapper.setError(getString(R.string.phone_number_hint));
        setErrorTextColor(mPhoneNumberWrapper, getResources().getColor(R.color.dark_gray2));


        if(user.getImage().contains("http")){
            Picasso.with(activity).load(user.getImage()).into(mProfileImage);
        } else {
            if(user.getImage().equals("")){
                Picasso.with(activity).load(R.drawable.noprofile).into(mProfileImage);
            } else {
                Picasso.with(activity).load(new File(user.getImage())).into(mProfileImage);
            }
        }


        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickNewImage();
            }
        });

        mAddNewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickNewImage();
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    isEditMode = false;
                    setChanges();
                } else {
                    isEditMode = true;
                    switchEditMode();
                }
            }
        });

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(isMyProfile){
            switchProfileMode();
        } else {
            switchOthersProfileMode();
        }

        isEditMode = false;
        switchNormalMode();
    }
// -------------------------------------------------------------------------------------------------
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

    private void pickNewImage(){
        ImagePicker.create(activity)
                .returnAfterFirst(true) // set whether pick or camera action should return immediate result or not. For pick image only work on single mode
                .folderMode(true) // folder mode (false by default)
                .folderTitle("") // folder selection title
                .imageTitle("") // image selection title
                .single() // single mode
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("") // directory name for captured image  ("Camera" folder by default)
                .start(1); // start image picker activity with request code
    }

    private void switchEditMode(){
        if(isMyProfile){
            mEditLayoutTitle.setText(getString(R.string.save));
            mEditLayoutTitle.setVisibility(View.VISIBLE);
        } else {
            mEditLayoutTitle.setVisibility(View.GONE);
        }
        saveBtn.setBackgroundResource(R.drawable.btn_save);
        mNicknameEdt.setFocusableInTouchMode(true);
        mStatusEdt.setFocusableInTouchMode(true);
        mEmailEdt.setFocusableInTouchMode(true);
        mPasswordEdt.setFocusableInTouchMode(true);
        mStatusEdt.setFocusable(true);
        mEmailEdt.setFocusable(true);
        mPasswordEdt.setFocusable(true);
        mNicknameEdt.setFocusable(true);
        mAddNewProfileImage.setVisibility(View.VISIBLE);
        Utils.showKeyboard(activity, mNicknameEdt);
    }

    private void switchNormalMode(){
        backCounter=0;
        if(isMyProfile){
            mEditLayoutTitle.setText(getString(R.string.edit));
            mEditLayoutTitle.setVisibility(View.VISIBLE);
        } else {
            mEditLayoutTitle.setVisibility(View.GONE);
        }
        saveBtn.setBackgroundResource(R.drawable.btn_edit);
        mNicknameEdt.setFocusable(false);
        mStatusEdt.setFocusable(false);
        mEmailEdt.setFocusable(false);
        mPasswordEdt.setFocusable(false);
        mAddNewProfileImage.setVisibility(View.GONE);
        Utils.hideKeyboard(activity, mNicknameEdt);
    }

    private void switchProfileMode(){
        mPhoneNumberWrapper.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        mAddNewProfileImage.setVisibility(View.VISIBLE);
    }

    private void switchOthersProfileMode(){
        mPhoneNumberWrapper.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        mAddNewProfileImage.setVisibility(View.GONE);
    }

    private void setChanges(){
        if(mNicknameEdt.getText().toString().equals("")){
            mNicknameWrapper.setErrorEnabled(true);
            mNicknameWrapper.setError(getString(R.string.enter_nickname));
            return;
        } else {
            mNicknameWrapper.setErrorEnabled(false);
        }

        if(mPasswordEdt.getText().toString().equals("")){
            mPasswordNumberWrapper.setErrorEnabled(true);
            mPasswordNumberWrapper.setError(getString(R.string.enter_nickname));
            return;
        } else {
            mPasswordNumberWrapper.setErrorEnabled(false);
        }

        user.setNickname(mNicknameEdt.getText().toString());
        user.setStatus(mStatusEdt.getText().toString());
        sendRequest();
    }

    private void saveChanges(){
        SharedPreferences prefs = getSharedPreferences(DataStore.PREFERENCES_NAME, MODE_PRIVATE);
        DataStore.currentUser = user;
        prefs.edit()
                .putString(DataStore.PREFERENCES_KEY_NICKNAME, user.getNickname())
                .putString(DataStore.PREFERENCES_KEY_PASSWORD, user.getPassword())
                .putString(DataStore.PREFERENCES_KEY_STATUS, user.getStatus())
                .putString(DataStore.PREFERENCES_KEY_IMAGE, user.getImage())
                .putString(DataStore.PREFERENCES_KEY_EMAIL, user.getEmail())
                .commit();
    }

    private void updateViews(){
        if(user.getImage().equals("")){
            Picasso.with(activity).load(R.drawable.noprofile).into(mProfileImage);
        } else {
            Picasso.with(activity).load(new File(user.getImage())).into(mProfileImage);
        }
    }

    /*private void getUserInfo(){
        nestedScrollView.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);
        requestCode = 2;
        parameters.clear();
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_GET_USER_BY_ID);
        parameters.put(WebStatistics.KEY_TOKEN, user.getToken());
        super.sendRequest();
    }*/

    @Override
    public void sendRequest() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(getString(R.string.wait_txt));
        progressDialog.setCancelable(false);
        progressDialog.show();
        requestCode = 1;
        parameters.put(WebStatistics.KEY_URL, WebStatistics.URL_EDIT_PROFILE);
        parameters.put(WebStatistics.KEY_TOKEN, user.getToken());
        parameters.put(WebStatistics.KEY_NICKNAME, user.getNickname());
        parameters.put(WebStatistics.KEY_STATUS, user.getStatus());
        if(isImageChanged){
            parameters.put(WebStatistics.KEY_IMAGE, Utils.base64(user.getImage()));
        } else {
            parameters.put(WebStatistics.KEY_IMAGE, "");
        }

        super.sendRequest();
    }

    @Override
    public void onActivityRequestResult(boolean result, int requestCode, String data) {
        super.onActivityRequestResult(result, requestCode, data);

        progressDialog.dismiss();
        nestedScrollView.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
        try {
            if(requestCode==1){
                saveChanges();
            }

            switchNormalMode();

        } catch (Exception ex){
            switchNormalMode();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            ArrayList<Image> images = (ArrayList<Image>) ImagePicker.getImages(data);
            isImageChanged = true;
            user.setImage(images.get(0).getPath());
            Picasso.with(activity).load(new File(user.getImage())).into(mProfileImage);
        }
    }


// -------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        boolean exit = false;
        if(backCounter==0){
            if(isEditMode){
                Utils.hideKeyboard(activity, mPhoneNumberWrapper);
                Utils.showSnackBar(activity, parentLayout, getString(R.string.discard_changes));
                backCounter++;
            } else {
               exit = true;
            }
        } else {
            exit = true;
        }

        if(exit){
            super.onBackPressed();
        }
    }




}
