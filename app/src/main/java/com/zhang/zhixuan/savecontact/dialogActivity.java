package com.zhang.zhixuan.savecontact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
     * Created by Chen Hian on 23/7/2015.
     */
    public class dialogActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        final String locationText1 = bundle.getString("locationText");
        setContentView(R.layout.activity_dialog);
        TextView t = (TextView)findViewById(R.id.dialog_textview);
        t.setText("Target has been located around " + locationText1);
        Button btnShowMap = (Button) findViewById(R.id.dialog_showMap);
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    finish();
                    Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:,0?q=" + locationText1)); // Prepare intent
                    startActivity(geoIntent); // Initiate lookup
                } catch (Exception e) {
                }
            }
        });
        Button btnClose = (Button) findViewById(R.id.dialog_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}





            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("AppName")
                    .setMessage("Target has been located around " + locationText1)
                    .setCancelable(false)
                    .setPositiveButton("Open Maps", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:,0?q=" + locationText1)); // Prepare intent
                                startActivity(geoIntent); // Initiate lookup
                            } catch (Exception e) {
                            }
                        }
                    })


                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();*/





