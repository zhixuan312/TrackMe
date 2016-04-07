package com.zhang.zhixuan.savecontact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhang.zhixuan.savecontact.Fields.ContactColumns;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private static final String[] PROJECTION = new String[]{ContactColumns._ID,
            ContactColumns.NAME, ContactColumns.NUMBER,ContactColumns.TEXT};
    private Button main_new;
    private Button main_record;
    private EditText main_search_edittext;
    private Button main_search_button;
    private TextView main_search_textview;
    public static ArrayList<String> textList;
    public static ArrayList<String> numberList;
    public static MainActivity app;
    public static int count = 0;
    public static SharedPreferences arraylists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        app = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main_new = (Button)findViewById(R.id.main_new);
        main_record = (Button)findViewById(R.id.main_record);
        main_search_edittext = (EditText)findViewById(R.id.main_search_edittext);
        main_search_button = (Button)findViewById(R.id.main_search_button);
        main_search_textview = (TextView)findViewById(R.id.main_search_textview);

        OnClickListener onclicklistener = new OnClickListener();
        main_new.setOnClickListener(onclicklistener);
        main_record.setOnClickListener(onclicklistener);
        main_search_button.setOnClickListener(onclicklistener);

    }


    class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Button button = (Button)v;
            switch (button.getId()) {
                case R.id.main_new:
                    Intent intentNew = new Intent(MainActivity.this, New.class);
                    intentNew.setAction(New.INSERT_CONTACT_ACTION);
                    intentNew.setData(getIntent().getData());
                    startActivity(intentNew);
                    break;
                case R.id.main_record:
                    Intent intentRecord = new Intent(MainActivity.this, Record.class);
                    intentRecord.setData(ContactColumns.CONTENT_URI);
                    startActivity(intentRecord);
                    break;
                case R.id.main_search_button:
                    String search = main_search_edittext.getText().toString();
                    Uri uri = ContactColumns.CONTENT_URI;
                    Cursor cursor = getContentResolver().query(uri,PROJECTION,null,null,null);
                    if (cursor.moveToFirst()){
                        String result = "";
                        for(int i= 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            String name = cursor.getString(1);
                            if (search.equals(name)){
                                String singleResult = cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3);
                                result = result + singleResult + "\n";
                            }
                        }
                        main_search_textview.setText(result);
                    }
                    break;
            }
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.about: {
                aboutMenuItem();
                break;
            }
            case R.id.action_settings: {
                settingsMenuItem();
                break;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    private void settingsMenuItem() {
        Intent PrecisionIntent = new Intent(this, PrecisionSettingsActivity.class);
        startActivity(PrecisionIntent);

    }






    private void aboutMenuItem() {
        new AlertDialog.Builder(this).setTitle("About").setMessage("TrackMe v2.4 An Orbital Project created by Zhixuan and Chen Hian").setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO Auto generated method stub
            }
        }).show();}
    }


