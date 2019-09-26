package com.hope.commonsense.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hope.commonsense.Web.WebServiceManager;

import java.util.HashMap;


public class MyActivity extends AppCompatActivity {

    MyActivity activity;
    public HashMap<String, String> parameters;

    int requestCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);

        activity = this;
        parameters = new HashMap<>();
    }


    public void sendRequest(){
        WebServiceManager webServiceManager = new WebServiceManager(this, requestCode);
        webServiceManager.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, parameters);
    }


    public void onActivityRequestResult(boolean result, int requestCode, String data){

    }


    public void refreshList(){

    }

    public void refreshAgeTitle(String newTitle){

    }


}
