package com.bixlabs.smssolidario.controllers;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import org.joda.time.DateTime;

import com.bixlabs.smssolidario.classes.AlertReceiver;

import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_HOUR;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_LAST_DAY;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_MINUTES;
import static com.bixlabs.smssolidario.classes.Constants.KEY_DAY;
import static com.bixlabs.smssolidario.classes.Constants.KEY_HOUR;
import static com.bixlabs.smssolidario.classes.Constants.KEY_LAST_DAY;
import static com.bixlabs.smssolidario.classes.Constants.KEY_MINUTE;
import static com.bixlabs.smssolidario.classes.Constants.KEY_MONTH;
import static com.bixlabs.smssolidario.classes.Constants.KEY_YEAR;

public class AlarmController extends AppCompatActivity {

	private static AlarmController instance = null;
	long dateOfNextFiring;

	private AlarmController() {}

	public static AlarmController getInstance() {
		if (instance == null) {
			instance = new AlarmController();
		}
		return instance;
	}

	/**
	 * This function configures next month's expiration date for the app to be executed.
	 * @param actualExpirationDate
	 * @param context
	 */
	public void configureNextMonthAlarm (DateTime actualExpirationDate, Context context) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    int expirationDay = settings.getInt(KEY_DAY, actualExpirationDate.getDayOfMonth());
    int expirationHour = settings.getInt(KEY_HOUR, DEFAULT_HOUR);
    int expirationMinute = settings.getInt(KEY_MINUTE, DEFAULT_MINUTES);
		//We need to configure next month's alarm
		DateTime nextExpirationDate = actualExpirationDate.withMinuteOfHour(expirationMinute);
		nextExpirationDate = nextExpirationDate.withHourOfDay(expirationHour);
		nextExpirationDate = nextExpirationDate.plusMonths(1);
		int lastDayOfNextMonth = nextExpirationDate.dayOfMonth().withMaximumValue().getDayOfMonth();
    boolean isLastDay = settings.getBoolean(KEY_LAST_DAY, DEFAULT_LAST_DAY);

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

		// We need to update the preferences of the user according to the month
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(KEY_YEAR, nextExpirationDate.getYear());
		editor.putInt(KEY_MONTH, nextExpirationDate.getMonthOfYear());
		editor.putInt(KEY_DAY, nextExpirationDate.getDayOfMonth());
		editor.apply();

		// The date must be in milliseconds
		dateOfNextFiring = nextExpirationDate.getMillis();

		Intent intentAlarm = new Intent(context, AlertReceiver.class);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm,
			PendingIntent.FLAG_UPDATE_CURRENT);

		// Cancel any existing alarm for this app
		alarmManager.cancel(pendingIntent);

		//set the alarm for particular time
		alarmManager.set(AlarmManager.RTC_WAKEUP, dateOfNextFiring, pendingIntent);
	}
}
