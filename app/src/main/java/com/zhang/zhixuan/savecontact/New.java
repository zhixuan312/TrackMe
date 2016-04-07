package com.zhang.zhixuan.savecontact;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zhang.zhixuan.savecontact.Fields.ContactColumns;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class New extends Activity {

    public static final String TAG = "Contact";
    public static final String EDIT_CONTACT_ACTION = "com.zhang.zhixuan.savecontact.action.EDIT_CONTACT";
    public static final String INSERT_CONTACT_ACTION = "com.zhang.zhixuan.savecontact.action.INSERT_CONTACT";

    private static final String[] PROJECTION = new String[]{ContactColumns._ID,
            ContactColumns.NAME, ContactColumns.NUMBER, ContactColumns.TEXT
    };

    private static final int STATE_NEW = 0;
    private static final int STATE_INSERT = 1;


    private int mState;

    private Uri mUri;
    private Cursor mCursor;

    private EditText new_Name_EditText;
    private EditText new_Number_EditText;
    private EditText new_Text_EditText;
    private Button new_Save_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final String action = intent.getAction();
        setContentView(R.layout.activity_new);

        if (intent.getData() == null) {
            intent.setData(ContactColumns.CONTENT_URI);
        }
        new_Name_EditText = (EditText) findViewById(R.id.new_Name_EditText);
        new_Number_EditText = (EditText) findViewById(R.id.new_Number_EditText);
        new_Text_EditText = (EditText) findViewById(R.id.new_Text_EditText);
        new_Save_Button = (Button) findViewById(R.id.new_Save_Button);

        if (EDIT_CONTACT_ACTION.equals(action)) {
            mState = STATE_NEW;
            mUri = intent.getData();
            mCursor = managedQuery(mUri, PROJECTION, null, null, null);
            mCursor.moveToFirst();
            String name = mCursor.getString(1);
            new_Name_EditText.setTextKeepState(name);
            String number = mCursor.getString(2);
            new_Number_EditText.setTextKeepState(number);
            String text = mCursor.getString(3);
            new_Text_EditText.setTextKeepState(text);
            setResult(RESULT_OK, (new Intent()).setAction(mUri.toString()));
            setTitle("EDIT CONTACT");
        } else if (INSERT_CONTACT_ACTION.equals(action)) {
            mState = STATE_INSERT;
            setTitle("NEW CONTACT");
        } else {
            Log.e(TAG, "no such action error");
            finish();
            return;
        }

        new_Save_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mState == STATE_INSERT) {
                    insertContact();
                } else {
                    updateContact();
                }
                Intent mIntent = new Intent();
                setResult(RESULT_OK, mIntent);
                finish();
            }

        });

    }

    private void insertContact() {
        String name = new_Name_EditText.getText().toString();
        String number = new_Number_EditText.getText().toString();
        String text = new_Text_EditText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(ContactColumns.TEXT, text);
        values.put(ContactColumns.NAME, name);
        values.put(ContactColumns.NUMBER, number);
        getContentResolver().insert(Fields.ContactColumns.CONTENT_URI, values);
        getNumberList();
        /*Toast.makeText(New.this, "DONE", Toast.LENGTH_LONG).show();
        String tl = "";
        String nl = "";
        for (String s : MainActivity.textList) {
            tl += s + "\t";
        }
        for (String s : MainActivity.numberList) {
            nl += s + "\t";
        }
        Toast.makeText(New.this, tl, Toast.LENGTH_LONG).show();
        Toast.makeText(New.this, nl, Toast.LENGTH_LONG).show();
*/
    }

    private void updateContact() {
        String name = new_Name_EditText.getText().toString();
        String number = new_Number_EditText.getText().toString();
        String text = new_Text_EditText.getText().toString();
        ContentValues values = new ContentValues();
        values.put(ContactColumns.TEXT, text);
        values.put(ContactColumns.NAME, name);
        values.put(ContactColumns.NUMBER, number);
        getContentResolver().update(mUri, values, null, null);
        getNumberList();
        //debugging code
        String tl = "";
        String nl = "";
        for (String s : MainActivity.textList) {
            tl += s + "\t";
        }
        for (String s : MainActivity.numberList) {
            nl += s + "\t";
        }
        //Toast.makeText(New.this, tl, Toast.LENGTH_LONG).show();
        //Toast.makeText(New.this, nl, Toast.LENGTH_LONG).show();

    }

    private void getNumberList() {
        Uri uriNew = ContactColumns.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uriNew, PROJECTION, null, null, null);
        ArrayList<String> numberListNew = new ArrayList<>();
        ArrayList<String> textListNew = new ArrayList<>();
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {

                cursor.moveToPosition(i);
                String number1 = cursor.getString(2);
                String text1 = cursor.getString(3);
                //Toast.makeText(New.this, text1, Toast.LENGTH_LONG).show();
                //Toast.makeText(New.this, number1, Toast.LENGTH_LONG).show();
                numberListNew.add(number1);
                textListNew.add(text1);
            }
        }
        MainActivity.numberList = numberListNew;
        MainActivity.textList = textListNew;
        SharedPreferences prefs = getSharedPreferences("arraylists", New.this.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("numberList", ObjectSerializer.serialize(numberListNew));
            editor.putString("textList", ObjectSerializer.serialize(textListNew));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();


    }
}






