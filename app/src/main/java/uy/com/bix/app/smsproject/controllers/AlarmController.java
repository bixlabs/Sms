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
	int expirationYear, expirationMonth, expirationDay, expirationHour, expirationMinute, maxMessages;
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
			expirationYear = settings.getInt("Year", 2015);
			expirationMonth = settings.getInt("Month", 1);
			expirationDay = settings.getInt("Day", 1);
			expirationHour = settings.getInt("Hour", 23);
			expirationMinute = settings.getInt("Minute", 30);
			isLastDay = settings.getBoolean("LastDay", false);
			phoneNumber = settings.getString("Phone", "1");
			textMessage = settings.getString("Message", "Hello");
			maxMessages = Integer.parseInt(settings.getString("Max", "1"));

			//We need to configure next month's alarm
			DateTime actualExpirationDate = DateTime.now();
			DateTime nextExpirationDate = actualExpirationDate.withMinuteOfHour(expirationMinute);
			nextExpirationDate = nextExpirationDate.withHourOfDay(expirationHour);
			nextExpirationDate = nextExpirationDate.plusMonths(1);
			int lastDayOfNextMonth = nextExpirationDate.dayOfMonth().withMaximumValue().getDayOfMonth();

			//If the expirationDay is the last day of the month we must handle it differently
			if (isLastDay) {
				expirationDay = lastDayOfNextMonth;
				nextExpirationDate = nextExpirationDate.withDayOfMonth(expirationDay);
			}
			else {
				if (expirationDay > nextExpirationDate.getDayOfMonth() && expirationDay <= lastDayOfNextMonth) {

					// If the expiration date isn't last day but is bigger than this month's capacity 
					// and smaller or equal than next month capacity			
					nextExpirationDate = nextExpirationDate.withDayOfMonth(expirationDay);
				}
			}

			// We need to update the preferneces of the user according to the month
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("Year", nextExpirationDate.getYear());
			editor.putInt("Month", nextExpirationDate.getMonthOfYear());
			editor.putInt("Day", nextExpirationDate.getDayOfMonth());
			editor.apply();

			// The date must be in milliseconds
			dateOfNextFiring = nextExpirationDate.getMillis();
			System.out.println("Esta Fecha:");
			System.out.println(actualExpirationDate);
			System.out.println("Proxima Fecha:");
			System.out.println(nextExpirationDate);

			Intent intentAlarm = new Intent(context, AlertReceiver.class);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm, 
				PendingIntent.FLAG_UPDATE_CURRENT);

			//set the alarm for particular time
			alarmManager.set(AlarmManager.RTC_WAKEUP, dateOfNextFiring, pendingIntent);

			// If the notification is activated, we must notify the user
			if (notifyWhenSending) {
				Toast.makeText(context, "Se va a donar dinero de tu saldo, muchas gracias! :)", 
					Toast.LENGTH_LONG).show();
			}

			sendMessages(phoneNumber, textMessage, maxMessages, actualExpirationDate, context);
		}

	}

	public void sendMessages(String phoneNum, String text, int maxMessagesToSend, 
													 DateTime expirationDate, Context context) {
		final MessageController msgController = MessageController.getInstance();
		boolean stopForMax = maxMessagesToSend == 0;

		// If the day changes we stop sending messages to prevent spending wrong credit
		DateTime actualTime = DateTime.now();
		boolean stopForChangeOfDay = actualTime.getDayOfMonth() != expirationDate.getDayOfMonth();

		while (!stopForMax && !stopForChangeOfDay) {
			msgController.sendMessage(phoneNum, text, context);

			// We update the stopping criteria
			maxMessagesToSend --;
			stopForMax = maxMessagesToSend == 0;
			actualTime = DateTime.now();
			stopForChangeOfDay = actualTime.getDayOfMonth() != expirationDate.getDayOfMonth();
		}
	}
}
