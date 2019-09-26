package com.hope.commonsense.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hope.commonsense.Activities.TagActivity;
import com.hope.commonsense.R;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by User on 6/17/2017.
 */

public class TagFragment extends Fragment{

    public ArrayList<Object> objects;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objects = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void bringToFront(){}

    public void parseData(JSONArray array){}

    public void refreshList(){}
}
