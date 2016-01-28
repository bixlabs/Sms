package uy.com.bix.app.smsproject.controllers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import uy.com.bix.app.smsproject.classes.AlertReceiver;

public class AlarmController extends AppCompatActivity {

	private static AlarmController instance = null;
	int expirationDay, expirationHour, expirationMinute, maxMessages;
	long dateOfNextFiring;
	String phoneNumber, textMessage;
	boolean notifyWhenSending, isLastDay, isActive;

	private AlarmController() {}

	public static AlarmController getInstance() {
		if (instance == null) {
			instance = new AlarmController();
		}
		return instance;
	}

	public void prepareAndSendMessage (Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		JodaTimeAndroid.init(context);
		isActive = settings.getBoolean("Active", false);

		// We make sure the app is active to send messages
		if (isActive) {

			// We get all the information the user set
			notifyWhenSending = settings.getBoolean("Notify", false);
			expirationDay = settings.getInt("Day", 1);
			expirationHour = settings.getInt("Hour", 23);
			expirationMinute = settings.getInt("Minute", 30);
			isLastDay = settings.getBoolean("LastDay", false);
			phoneNumber = settings.getString("Phone", "1");
			textMessage = settings.getString("Message", "Hello");
			maxMessages = Integer.parseInt(settings.getString("Max", "1"));

			//We need to configure next month's alarm
			DateTime sendingDate = DateTime.now();
			sendingDate = sendingDate.withMinuteOfHour(expirationMinute);
			sendingDate = sendingDate.withHourOfDay(expirationHour);
			sendingDate = sendingDate.plusMonths(1);

			//If the expirationDay is the last day of the month we must handle it differently
			if (isLastDay) {
				expirationDay = sendingDate.dayOfMonth().withMaximumValue().getDayOfMonth();
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("Day", expirationDay);
				editor.apply();
			}
			sendingDate = sendingDate.withDayOfMonth(expirationDay);

			// The date must be in milliseconds
			dateOfNextFiring = sendingDate.getMillis();
			System.out.println("Proxima Fecha:");
			System.out.println(sendingDate);

			Intent intentAlarm = new Intent(context, AlertReceiver.class);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

			//set the alarm for particular time
			alarmManager.set(AlarmManager.RTC_WAKEUP, dateOfNextFiring, pendingIntent);

			// If the notification is activated, we must notify the user
			if (notifyWhenSending) {
				Toast.makeText(context, "Se va a donar dinero de tu saldo, muchas gracias! :)", Toast.LENGTH_LONG).show();
			}

			sendMessages(phoneNumber, textMessage, maxMessages, context);
		}

	}

	public void sendMessages(String phoneNum, String text, int maxMessagesToSend, Context context) {
		final MessageController msgController = MessageController.getInstance();
		boolean stopForMax = maxMessagesToSend == 0;

		while (!stopForMax) {
			msgController.sendMessage(phoneNum, text, context);

			maxMessagesToSend --;
			stopForMax = maxMessagesToSend == 0;
		}
	}
}
