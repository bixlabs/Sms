package com.bixlabs.smssolidario.classes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import static com.bixlabs.smssolidario.classes.Constants.KEY_ACTIVE;
import static com.bixlabs.smssolidario.classes.Constants.KEY_ALLOWED_PREMIUM;
import static com.bixlabs.smssolidario.classes.Constants.KEY_DAY;
import static com.bixlabs.smssolidario.classes.Constants.KEY_ERROR;
import static com.bixlabs.smssolidario.classes.Constants.KEY_MAX;
import static com.bixlabs.smssolidario.classes.Constants.KEY_MESSAGE;
import static com.bixlabs.smssolidario.classes.Constants.KEY_NOTIFY;
import static com.bixlabs.smssolidario.classes.Constants.KEY_PHONE;
import static com.bixlabs.smssolidario.classes.Constants.KEY_SENT_SMS;
import static com.bixlabs.smssolidario.classes.Constants.KEY_SMS_TO_SEND;


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
				case Activity.RESULT_CANCELED:
          settings = PreferenceManager.getDefaultSharedPreferences(context);
          boolean isActive = settings.getBoolean(KEY_ACTIVE, DEFAULT_ACTIVE);
          if (isActive) {
            JodaTimeAndroid.init(context);
            int maxMessages = settings.getInt(KEY_MAX, DEFAULT_MAX);
            editor = settings.edit();
            editor.putInt(KEY_SMS_TO_SEND, maxMessages);
            editor.apply();

            phoneNumber = settings.getString(KEY_PHONE, DEFAULT_PHONE);
            textMessage = settings.getString(KEY_MESSAGE, DEFAULT_MESSAGE);
            boolean notifyWhenSending = settings.getBoolean(KEY_NOTIFY, DEFAULT_NOTIFY);
            if (notifyWhenSending) {
              Toast.makeText(context, context.getString(R.string.toaster_sending_sms),
                Toast.LENGTH_LONG).show();
            }
            msgController = MessageController.getInstance();
            msgController.sendMessage(phoneNumber, textMessage, context);
          }
					break;
				case Activity.RESULT_OK:
          settings = PreferenceManager.getDefaultSharedPreferences(context);
          boolean allowedPremium = settings.getBoolean(KEY_ALLOWED_PREMIUM, DEFAULT_ALLOWED_PREMIUM);
          if (!allowedPremium) {
            editor = settings.edit();
            editor.putBoolean(KEY_ALLOWED_PREMIUM, true);
            editor.apply();
          }
          int messagesToSend = settings.getInt(KEY_SMS_TO_SEND, DEFAULT_SMS_TO_SEND);
          messagesToSend--;
          sentMessages = settings.getInt(KEY_SENT_SMS, DEFAULT_SENT_SMS);
          sentMessages++;
          editor = settings.edit();
          editor.putInt(KEY_SENT_SMS, sentMessages);
          editor.putInt(KEY_SMS_TO_SEND, messagesToSend);
          editor.putBoolean(KEY_ERROR, false);
          editor.apply();
          // If the day changes we stop sending messages to prevent spending wrong credit
          DateTime actualTime = DateTime.now();
          DateTime actualExpirationDate = DateTime.now();
          int expirationDay = settings.getInt(KEY_DAY, actualExpirationDate.getDayOfMonth());
          boolean stopForChangeOfDay = actualTime.getDayOfMonth() != expirationDay;
          boolean stopForMax = messagesToSend == 0;
          if (!stopForMax && !stopForChangeOfDay) {
            phoneNumber = settings.getString(KEY_PHONE, DEFAULT_PHONE);
            textMessage = settings.getString(KEY_MESSAGE, DEFAULT_MESSAGE);
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

	private void alertErrorWhileSendingSms (Context context) {
		settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(KEY_ERROR, true);
		editor.apply();
		Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
	}
}

