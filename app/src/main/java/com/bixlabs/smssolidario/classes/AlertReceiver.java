package com.bixlabs.smssolidario.classes;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import com.bixlabs.smssolidario.R;
import com.bixlabs.smssolidario.controllers.AlarmController;
import com.bixlabs.smssolidario.controllers.MessageController;

import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_ACTIVE;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_ALLOWED_PREMIUM;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_MAX;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_MESSAGE;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_NOTIFY;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_PHONE;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_SENT_SMS;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_SMS_TO_SEND;
import static com.bixlabs.smssolidario.classes.Constants.PREF_ACTIVE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_ALLOWED_PREMIUM;
import static com.bixlabs.smssolidario.classes.Constants.PREF_DAY;
import static com.bixlabs.smssolidario.classes.Constants.PREF_ERROR;
import static com.bixlabs.smssolidario.classes.Constants.PREF_MAX;
import static com.bixlabs.smssolidario.classes.Constants.PREF_MESSAGE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_NOTIFY;
import static com.bixlabs.smssolidario.classes.Constants.PREF_PHONE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_SENT_SMS;
import static com.bixlabs.smssolidario.classes.Constants.PREF_SMS_TO_SEND;
import static com.bixlabs.smssolidario.classes.Constants.VISIBILITY_PUBLIC;


public class AlertReceiver extends BroadcastReceiver {

  SharedPreferences settings;
  int sentMessages;
  String phoneNumber, textMessage;
  SharedPreferences.Editor editor;
  MessageController msgController;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			switch (getResultCode()) {
        // When the alarm triggers
				case Activity.RESULT_CANCELED:
          settings = PreferenceManager.getDefaultSharedPreferences(context);
          boolean isActive = settings.getBoolean(PREF_ACTIVE, DEFAULT_ACTIVE);
          // Check if the app is active to send the message
          if (isActive) {
            sendFirstMessage(context);
          }
					break;
        // This is the case when the message is sent
				case Activity.RESULT_OK:
          settings = PreferenceManager.getDefaultSharedPreferences(context);
          boolean allowedPremium = settings.getBoolean(PREF_ALLOWED_PREMIUM, DEFAULT_ALLOWED_PREMIUM);
          // If the messages were sent successfully then we don't
          // Need to show again the validation dialog
          if (!allowedPremium) {
            editor = settings.edit();
            editor.putBoolean(PREF_ALLOWED_PREMIUM, true);
            editor.apply();
          }
          int messagesToSend = settings.getInt(PREF_SMS_TO_SEND, DEFAULT_SMS_TO_SEND);
          messagesToSend--;
          sentMessages = settings.getInt(PREF_SENT_SMS, DEFAULT_SENT_SMS);
          sentMessages++;
          editor = settings.edit();
          editor.putInt(PREF_SENT_SMS, sentMessages);
          editor.putInt(PREF_SMS_TO_SEND, messagesToSend);
          editor.putBoolean(PREF_ERROR, false);
          editor.apply();
          // If the day changes we stop sending messages to prevent spending wrong credit
          DateTime actualTime = DateTime.now();
          DateTime actualExpirationDate = DateTime.now();
          int expirationDay = settings.getInt(PREF_DAY, actualExpirationDate.getDayOfMonth());
          boolean stopForChangeOfDay = actualTime.getDayOfMonth() != expirationDay;
          boolean stopForMax = messagesToSend == 0;
          if (!stopForMax && !stopForChangeOfDay) {
            phoneNumber = settings.getString(PREF_PHONE, DEFAULT_PHONE);
            textMessage = settings.getString(PREF_MESSAGE, DEFAULT_MESSAGE);
            msgController = MessageController.getInstance();
            msgController.sendMessage(phoneNumber, textMessage, context);
          } else {
            AlarmController alarmController = AlarmController.getInstance();
            alarmController.configureNextMonthAlarm(actualExpirationDate, context);
          }
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				case SmsManager.RESULT_ERROR_NO_SERVICE:
				case SmsManager.RESULT_ERROR_NULL_PDU:
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					alertErrorWhileSendingSms(context);
					break;
			}
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

  private void sendFirstMessage(Context context) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    JodaTimeAndroid.init(context);
    int maxMessages = settings.getInt(PREF_MAX, DEFAULT_MAX);
    SharedPreferences.Editor editor = settings.edit();
    editor.putInt(PREF_SMS_TO_SEND, maxMessages);
    editor.apply();

    String phoneNumber = settings.getString(PREF_PHONE, DEFAULT_PHONE);
    String textMessage = settings.getString(PREF_MESSAGE, DEFAULT_MESSAGE);
    boolean notifyWhenSending = settings.getBoolean(PREF_NOTIFY, DEFAULT_NOTIFY);
    if (notifyWhenSending) {
      NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notification_sms_solidario)
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(context.getString(R.string.toaster_sending_sms))
                                .setNumber(maxMessages)
                                .setVisibility(VISIBILITY_PUBLIC)
                                .setAutoCancel(true);
      NotificationManager mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      int mnNotifyId = 1;
      mNotificationManager.notify(mnNotifyId, mBuilder.build());
    }
    MessageController msgController = MessageController.getInstance();
    msgController.sendMessage(phoneNumber, textMessage, context);
  }

	private void alertErrorWhileSendingSms (Context context) {
		settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREF_ERROR, true);
		editor.apply();
		Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
	}

}

