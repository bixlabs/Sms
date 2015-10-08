package uy.com.bix.app.smsproject.controllers;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import static uy.com.bix.app.smsproject.classes.Constants.MSG_SENT;


public class MessageController extends AppCompatActivity {

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

    String sent = MSG_SENT;
    PendingIntent sentPI = PendingIntent.getBroadcast(appContext, 0,
      new Intent(sent), 0);

    //---when the SMS has been sent---
    appContext.registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context arg0, Intent arg1) {
        try {
          switch (getResultCode()) {
            case Activity.RESULT_OK:
              Toast.makeText(appContext, "SMS sent", Toast.LENGTH_SHORT).show();
              break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
              Toast.makeText(appContext, "Generic failure", Toast.LENGTH_SHORT).show();
              break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
              Toast.makeText(appContext, "No service", Toast.LENGTH_SHORT).show();
              break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
              Toast.makeText(appContext, "Null PDU", Toast.LENGTH_SHORT).show();
              break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
              Toast.makeText(appContext, "Radio off", Toast.LENGTH_SHORT).show();
              break;
          }
        } catch (Exception e) {
          Log.d("Exception: ", e.getMessage());
        }
      }
    }, new IntentFilter(sent));


    //We use SmsManager API to send the message
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(phoneNumber, null, text, sentPI, null);
    } catch (IllegalArgumentException e) {
      Log.d("Exception: ", e.getMessage());
      //We have to notify the user
    }

  }
}
