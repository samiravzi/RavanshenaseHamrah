package com.hope.commonsense.Web;

import java.util.HashMap;

/**
 * Created by User on 4/30/2017.
 */
public class WebUtils {


    public static String makeBody(String url, HashMap<String, String> parameters) {
        StringBuilder body = new StringBuilder();
        body.append("{");

        for(String key : parameters.keySet()){
            body.append("\"");
            body.append(key);
            body.append("\":");
            if(key.equals(WebStatistics.KEY_AGES)){
                body.append(parameters.get(key));
            } else {
                body.append("\"");
                body.append(parameters.get(key));
                body.append("\"");
            }
            body.append(",");
        }
        if(body.toString().contains(",")){
            body.deleteCharAt(body.lastIndexOf(","));
        }

        body.append("}");
        return body.toString();
    }
}
