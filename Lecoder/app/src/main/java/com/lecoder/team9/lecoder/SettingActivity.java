package com.lecoder.team9.lecoder;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Schwa on 2017-11-24.
 */

public class SettingActivity extends PreferenceActivity {
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);
        toolbar.setTitle("설정");
        toolbar.setTitleTextColor(Color.WHITE);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        ViewGroup contentView= (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.activity_settings,new LinearLayout(this),false);

        toolbar=contentView.findViewById(R.id.toolbar_setting);
        ViewGroup contentWrapper=contentView.findViewById(R.id.setting_content);
        LayoutInflater.from(this).inflate(layoutResID,contentWrapper,true);
        getWindow().setContentView(contentView);
    }
}
