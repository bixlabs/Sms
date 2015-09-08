package uy.com.bix.app.smsproject.controllers;


import android.app.PendingIntent;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;

public class MessageController extends AppCompatActivity {

  private static MessageController instance = new MessageController();

  private MessageController() {
  }

  public static MessageController getInstance() {
    return instance;
  }

  public void sendMessage(String phoneNumber, String text, PendingIntent sent, PendingIntent delivered) {

    //We use SmsManager API to send the message
    SmsManager smsManager = SmsManager.getDefault();
    smsManager.sendTextMessage(phoneNumber, null, text, sent, delivered);
  }
}
