package com.bixlabs.smssolidario.controllers;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import static com.bixlabs.smssolidario.classes.Constants.PREF_ERROR;
import static com.bixlabs.smssolidario.classes.Constants.SMS_SENT;


public class MessageController {

  private static MessageController instance = null;

  private MessageController() {}

  public static MessageController getInstance() {
    if (instance == null) {
      instance = new MessageController();
    }
    return instance;
  }

  /**
   * This method is in charge of sending a text message that contains the information given in the
   * parameters
   * @param phoneNumber The destination address to send the message
   * @param text The text of the message
   * @param appContext The context of the application that wants to send a message
   */
  public void sendMessage(String phoneNumber, String text, final Context appContext) {

    String sent = SMS_SENT;
    PendingIntent sentPI = PendingIntent.getBroadcast(appContext, 0, new Intent(sent), 0);

    //We use SmsManager API to send the message
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(phoneNumber, null, text, sentPI, null);
    } catch (IllegalArgumentException e) {
      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(appContext);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(PREF_ERROR, true);
      editor.apply();
      Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

  }
}
