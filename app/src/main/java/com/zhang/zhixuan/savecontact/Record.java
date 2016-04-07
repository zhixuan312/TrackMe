package com.zhang.zhixuan.savecontact;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.*;
import com.zhang.zhixuan.savecontact.Fields.ContactColumns;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Record extends Activity {

    private ListView lvcontact;

    private static final String[] PROJECTION = new String[] { ContactColumns._ID,
            ContactColumns.NAME,ContactColumns.NUMBER,ContactColumns.TEXT };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        lvcontact = (ListView)findViewById(R.id.lvcontact);

        Intent intent = new Intent();
        intent.setData(ContactColumns.CONTENT_URI);

        Cursor cursor = this.getContentResolver().query(intent.getData(), PROJECTION, null,
                null, ContactColumns.DEFAULT_SORT_ORDER);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.record_row, cursor, new String[] { ContactColumns.NAME,
                ContactColumns.TEXT }, new int[] { R.id.record_row_name,
                R.id.record_row_number });
        lvcontact.setAdapter(adapter);

        // once click on the individual record, go to New activity and edit
        lvcontact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
                startActivity(new Intent(New.EDIT_CONTACT_ACTION, uri));
            }
        });

        // long press for delete
        lvcontact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
                getContentResolver().delete(uri, null, null);
                renderListView();
                Toast.makeText(getBaseContext(),"Deleted",Toast.LENGTH_SHORT).show();

                Uri uriNew = ContactColumns.CONTENT_URI;
                Cursor cursor = getContentResolver().query(uriNew,PROJECTION,null,null,null);

                //String numberListNew = "";
                //String textListNew = "";
                ArrayList<String> numberListNew = new ArrayList<>();
                ArrayList<String> textListNew = new ArrayList<>();
                if (cursor.moveToFirst()){

                    for(int i= 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        String number1 = cursor.getString(2);
                        String text1 = cursor.getString(3);
                        //Toast.makeText(Record.this, text1, Toast.LENGTH_LONG).show();
                        //Toast.makeText(Record.this, number1, Toast.LENGTH_LONG).show();
                        numberListNew.add(number1);
                        textListNew.add(text1);
                    }
                    /*    cursor.moveToPosition(i);
                        String number = cursor.getString(2);
                        String text = cursor.getString(3);
                        numberListNew = numberListNew + number;
                        textListNew = textListNew + text;
                    }*/
                }
                MainActivity.numberList = numberListNew;
                MainActivity.textList = textListNew;
                SharedPreferences prefs = getSharedPreferences("arraylists", Record.this.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                try {
                    editor.putString("numberList", ObjectSerializer.serialize(numberListNew));
                    editor.putString("textList", ObjectSerializer.serialize(textListNew));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                editor.commit();

                return true;
            }
        });
    }

    private void renderListView() {
        Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null,
                null, ContactColumns.DEFAULT_SORT_ORDER);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.record_row, cursor, new String[] { ContactColumns.NAME,
                ContactColumns.TEXT }, new int[] { R.id.record_row_name,
                R.id.record_row_number });
        lvcontact.setAdapter(adapter);
    }
}


