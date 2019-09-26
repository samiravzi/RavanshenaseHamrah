package com.hope.commonsense.Web;

import android.os.AsyncTask;
import android.util.Log;

import com.hope.commonsense.Activities.MyActivity;
import com.hope.commonsense.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class WebServiceManager extends AsyncTask<HashMap<String, String>, Void, String>{

    MyActivity activity;
    String TAG = "WebManager";

    String url = "";
    int requestCode;


    public WebServiceManager(MyActivity activity, int requestCode){
        this.activity = activity;
        this.requestCode = requestCode;
    }


    @Override
    protected String doInBackground(HashMap<String, String>... hashMaps) {
        String body;
        url = hashMaps[0].get(WebStatistics.KEY_URL);
        Log.e("ws", "url: " + url);
        hashMaps[0].remove(WebStatistics.KEY_URL); // remove url to have just parameters

        body = WebUtils.makeBody(url, hashMaps[0]);
        Log.e("ws", "body: " + body);

        String response = sendHttpRequest(url, body);
        Log.e("ws", "response: " + response);

        return response;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        activity.onActivityRequestResult(hasError(data), requestCode, data);
    }

// -------------------------------------------------------------------------------------------------
    private boolean hasError(String response) {
        boolean result = true;
        try {
            Object _json = new JSONTokener(response).nextValue();
            if (_json instanceof JSONObject) {
                JSONObject json = new JSONObject(response);
                int errorId = json.getInt("ResultCode");
                if(errorId > 0)
                    result = true;
                 else
                    result = false;
            }
            else {
                result = false;
            }

        } catch (JSONException e) {
            Log.e(TAG, ""+e.getMessage());
            result = false;
        } catch (Exception ex){
            Log.e(TAG, ""+ex.getMessage());
            result = false;
        }
        return result;
    }

// -------------------------------------------------------------------------------------------------

    public String sendHttpRequest(String url, String parameters){
        try{

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1000000, TimeUnit.SECONDS)
                    .readTimeout(1000000, TimeUnit.SECONDS)
                    .writeTimeout(1000000, TimeUnit.SECONDS)
                    .build();


            Log.e("ws", activity.getPackageName());
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, parameters);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("PackageId", activity.getPackageName())
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string().toString();

        } catch (IOException ex){
            Log.e("web service", ex.getMessage()+"");
        }
        catch (Exception e){
            Log.e("web service",e.getMessage()+"");
        }
        return "";
    }



}
