package uy.com.bix.app.smsproject.controllers;


import android.app.PendingIntent;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;

public class MessageController extends AppCompatActivity {

  private static MessageController instance = null;

  private MessageController() {
  }

  public static MessageController getInstance() {
    if (instance == null) {
      instance = new MessageController();
    }
    return instance;
  }

  public void sendMessage(String phoneNumber, String text, PendingIntent sent, PendingIntent delivered) {

    //We use SmsManager API to send the message
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(phoneNumber, null, text, sent, delivered);
    } catch (Exception e) {
      Log.d("Exception: ", e.getMessage());
      //We have to notify the user
    }

  }
}
