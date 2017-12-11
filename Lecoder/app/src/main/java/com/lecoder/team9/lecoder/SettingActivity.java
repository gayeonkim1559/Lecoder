package com.lecoder.team9.lecoder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Schwa on 2017-11-24.
 */

public class SettingActivity extends PreferenceActivity {
    Toolbar toolbar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    SwitchPreference togglePushAlarm,sttService,autoRecord;
    Preference dataReset;
    SharedPreferences shref;
    private SharedPreferences fastShref;
    private SharedPreferences lecShref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);
        toolbar.setTitle("설정");
        toolbar.setTitleTextColor(Color.WHITE);
        preferences=getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor=preferences.edit();
        togglePushAlarm= (SwitchPreference) findPreference("pushAlarm");
        sttService= (SwitchPreference) findPreference("sttService");
        dataReset=findPreference("app_data_reset");
        sttService.setChecked(false);
        sttService.setEnabled(false);
        autoRecord= (SwitchPreference) findPreference("autoRecord");
        autoRecord.setChecked(false);
        autoRecord.setEnabled(false);
        togglePushAlarm.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                boolean isChecked= (boolean) o;
                editor.putBoolean("push",isChecked);
                editor.commit();
                String toggle=(isChecked==true?"푸시알림이 수신으":"푸시알림이 거부");
                Toast.makeText(getApplicationContext(),toggle+"로 설정되었습니다.",Toast.LENGTH_SHORT).show();
                togglePushAlarm.setChecked(isChecked);
                return false;
            }
        });
        dataReset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("경고 !!");
                builder.setMessage("모든 데이터를 초기화 하시겠습니까?\n이미 녹음된 파일은 삭제되지 않습니다.");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteShref();
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
                return false;
            }
        });
        shref = getApplicationContext().getSharedPreferences("table", Context.MODE_PRIVATE);
        fastShref=getApplicationContext().getSharedPreferences("fastList", Context.MODE_PRIVATE);;
        lecShref=getApplicationContext().getSharedPreferences("lectureList", Context.MODE_PRIVATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void deleteShref() {
        editor=shref.edit();
        editor.clear();
        editor.commit();
        editor=fastShref.edit();
        editor.clear();
        editor.commit();
        editor=lecShref.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(getApplicationContext(),"초기화가 완료되었습니다. 재시작 후에 적용됩니다.",Toast.LENGTH_SHORT).show();
        finishAffinity();
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.end_enter, R.anim.end_exit);
    }
}
