package com.zhang.zhixuan.savecontact;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SMS extends BroadcastReceiver {
    // private static final String[] PROJECTION = new String[]{ContactColumns._ID,
    //        ContactColumns.NAME, ContactColumns.NUMBER,ContactColumns.TEXT};
    private String smsContent = "";
    private String body;
    private String date;
    private String sender;
    private boolean isWanted = false;
    private GPSTracker gps;
    private Location location;
    private String key;
    private String[] keys;

    // private String unlockScreenTime = "Last Time Use:" + "\n";

    @Override
    public void onReceive(Context mcontext, Intent intent) {
        final Context context = mcontext;
        SharedPreferences privacyPrefs = mcontext.getSharedPreferences("PrivacySelection",0 );
        //Toast.makeText(context, "For debugging :" + privacyPrefs.getInt("spinnerSelection",-1), Toast.LENGTH_LONG).show();

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED") && privacyPrefs.getInt("spinnerSelection",0) == 0 && MainActivity.textList!=null) {
            //Toast.makeText(context, "For debugging :" + privacyPrefs.getInt("spinnerSelection",-2), Toast.LENGTH_LONG).show();
            gps = new GPSTracker(context);
            location = gps.getLocation();
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object p : pdus) {
                byte[] pdu = (byte[]) p;
                SmsMessage message = SmsMessage.createFromPdu(pdu);


                String address = "";
                Geocoder geoCoder = new Geocoder(
                        context, Locale.getDefault());
                location = gps.getLocation();
                if (location != null) {
                    //Toast.makeText(context, "For debugging : Location ok" , Toast.LENGTH_LONG).show();
                    try {
                        List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.size() > 0) {
                            for (int index = 0;
                                 index < addresses.get(0).getMaxAddressLineIndex(); index++)
                                address += addresses.get(0).getAddressLine(index) + " ";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Toast.makeText(context, "For debugging : Null Location,(Inform CH if message is seen while GPS is on)", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(context, "For debugging :" + address, Toast.LENGTH_LONG).show();


                body = address;
                date = new Date(message.getTimestampMillis()).toLocaleString();
                sender = message.getOriginatingAddress();
                key = message.getMessageBody();
                //Toast.makeText(context, sender, Toast.LENGTH_LONG).show();
                //Toast.makeText(context, key, Toast.LENGTH_LONG).show();
                //Toast.makeText(context, MainActivity.numberList, Toast.LENGTH_LONG).show();



                if (MainActivity.numberList==null||MainActivity.textList==null) {
                    //Toast.makeText(mcontext, "null", Toast.LENGTH_LONG).show();
                    SharedPreferences prefs = mcontext.getSharedPreferences("arraylists", Context.MODE_PRIVATE);

                    try {
                        MainActivity.numberList = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString("numberList", ObjectSerializer.serialize(new ArrayList<String>())));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        MainActivity.textList = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString("textList", ObjectSerializer.serialize(new ArrayList<String>())));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
/*
                    String t2 = "t2";
                    String n2 = "t2";
                    for (String s : MainActivity.textList) {
                        t2 += s + "\t";
                    }
                    for (String s : MainActivity.numberList) {
                        n2 += s + "\t";
                    }
                    Toast.makeText(mcontext, t2, Toast.LENGTH_LONG).show();
                    Toast.makeText(mcontext, n2, Toast.LENGTH_LONG).show();
*/
                   // MainActivity.numberList = ;
                   // MainActivity.textList = ;
                }
                for (int j=0;j<MainActivity.numberList.size();j++) {
                    if (MainActivity.numberList.get(j).equalsIgnoreCase(sender)) {
                            if (MainActivity.textList.get(j).equalsIgnoreCase(key)) {
                                //Toast.makeText(context, "isWanted", Toast.LENGTH_LONG).show();
                                isWanted = true;
                                smsContent = "TrackMe: " + "\n" + "Date: " + date + "\n" + " Requester: " + sender + "\n" + " Target Location: " + body;
                                break;
                        }
                    }
                }
                    if (isWanted && location != null) {
                        //Toast.makeText(context, "Sent" , Toast.LENGTH_LONG).show();
                        sendSmsMessage(sender, smsContent);
                        isWanted = false;
                        //   unlockScreenTime = "Last Time Use:" + "\n";
                    }
                }


                if (key.length() > 8) {
                    String test = key.substring(0, 8);
                    if (test.equalsIgnoreCase("TrackMe:")) {
                        //Toast.makeText(context, "location reply", Toast.LENGTH_LONG).show();

                        int temp = body.lastIndexOf("Location:");
                        temp += 1;
                        final String location = body.substring(temp);
                        //Toast.makeText(context, location, Toast.LENGTH_LONG).show();
                        //String locationText = location;
                        Intent dialogIntent = new Intent(context, dialogActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("locationText", location);
                        context.startActivity(dialogIntent);
                    }
                }


                if (isWanted && location != null) {
                    sendSmsMessage(sender, smsContent);
                    isWanted = false;
                    //   unlockScreenTime = "Last Time Use:" + "\n";
                }
            }
        }



   /*     if (intent.getAction().equals("android.intent.action.USER_PRESENT")) {
            unlockScreenTime = unlockScreenTime + new Date(System.currentTimeMillis()).toLocaleString();

        }
   */


        public void sendSmsMessage(String phoneNumber, String content) {
            SmsManager smsManager = SmsManager.getDefault();
            if (content.length() >= 70) {
                List<String> list = smsManager.divideMessage(content);
                for (String mMsg : list) {
                    smsManager.sendTextMessage(phoneNumber, null, mMsg, null, null);
                }
            } else {
                smsManager.sendTextMessage(phoneNumber, null, content, null, null);
            }
        }
    }








