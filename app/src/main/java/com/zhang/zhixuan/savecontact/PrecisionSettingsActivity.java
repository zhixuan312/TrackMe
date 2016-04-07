package com.zhang.zhixuan.savecontact;

/**
 * Created by Chen Hian on 15/8/2015.
 */
import android.app.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;


public class PrecisionSettingsActivity extends Activity implements AdapterView.OnItemSelectedListener {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.precision_settings);
        SharedPreferences prefs1 = getSharedPreferences("PrivacySelection",0 );
        //  SharedPreferences prefs = getSharedPreferences(0);


        Spinner privacySettings = (Spinner) findViewById(R.id.spinner);
        privacySettings.setSelection(prefs1.getInt("spinnerSelection", 0));

    }

    protected void onPause() {
        super.onPause();
        Spinner privacySettings = (Spinner) findViewById(R.id.spinner);
        SharedPreferences.Editor editor = getSharedPreferences("PrivacySelection",0).edit();
        int selectedPositionPrecision = privacySettings.getSelectedItemPosition();
        editor.putInt("spinnerSelection", selectedPositionPrecision);
        editor.commit();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
