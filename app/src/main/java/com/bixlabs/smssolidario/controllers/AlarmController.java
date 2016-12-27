package com.bixlabs.smssolidario.controllers;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.joda.time.DateTime;

import com.bixlabs.smssolidario.R;
import com.bixlabs.smssolidario.activity.MainActivity;
import com.bixlabs.smssolidario.classes.AlertReceiver;

import java.text.SimpleDateFormat;

import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_HOUR;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_LAST_DAY;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_MINUTES;
import static com.bixlabs.smssolidario.classes.Constants.PREF_DAY;
import static com.bixlabs.smssolidario.classes.Constants.PREF_HOUR;
import static com.bixlabs.smssolidario.classes.Constants.PREF_LAST_DAY;
import static com.bixlabs.smssolidario.classes.Constants.PREF_MINUTE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_MONTH;
import static com.bixlabs.smssolidario.classes.Constants.PREF_YEAR;
import static com.bixlabs.smssolidario.classes.Constants.VISIBILITY_PUBLIC;

public class AlarmController {

	private static AlarmController instance = null;

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
    int expirationDay = settings.getInt(PREF_DAY, actualExpirationDate.getDayOfMonth());
    int expirationHour = settings.getInt(PREF_HOUR, DEFAULT_HOUR);
    int expirationMinute = settings.getInt(PREF_MINUTE, DEFAULT_MINUTES);
		//We need to configure next month's alarm
		DateTime nextExpirationDate = actualExpirationDate.withMinuteOfHour(expirationMinute);
		nextExpirationDate = nextExpirationDate.withHourOfDay(expirationHour);
		nextExpirationDate = nextExpirationDate.plusMonths(1);
		int lastDayOfNextMonth = nextExpirationDate.dayOfMonth().withMaximumValue().getDayOfMonth();
    boolean isLastDay = settings.getBoolean(PREF_LAST_DAY, DEFAULT_LAST_DAY);

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
		editor.putInt(PREF_YEAR, nextExpirationDate.getYear());
		editor.putInt(PREF_MONTH, nextExpirationDate.getMonthOfYear());
		editor.putInt(PREF_DAY, nextExpirationDate.getDayOfMonth());
		editor.apply();

		// The date must be in milliseconds
		long dateOfNextFiring = nextExpirationDate.getMillis();

		Intent intentAlarm = new Intent(context, AlertReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm,
			PendingIntent.FLAG_UPDATE_CURRENT);

		//set the alarm for particular time
    setNextAlarm(context, dateOfNextFiring, pendingIntent);

    // Set the notification for next date
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    String formattedDate = dateFormat.format(nextExpirationDate.toDate());
    Intent resultIntent = new Intent(context, MainActivity.class);
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent =
      stackBuilder.getPendingIntent(
        0,
        PendingIntent.FLAG_CANCEL_CURRENT
      );
    NotificationCompat.Builder mBuilder =
      new NotificationCompat.Builder(context)
                              .setSmallIcon(R.drawable.notification_sms_solidario)
                              .setContentTitle(context.getString(R.string.app_name))
                              .setContentText(context.getString(R.string.notification_next_schedule) + " " + formattedDate)
                              .setVisibility(VISIBILITY_PUBLIC)
                              .setAutoCancel(true)
                              .setContentIntent(resultPendingIntent);
    NotificationManager mNotificationManager =
      (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    int mnNotifyId = 2;
    mNotificationManager.notify(mnNotifyId, mBuilder.build());
	}

  private void setNextAlarm(Context context, long date, PendingIntent intent) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    // Cancel any existing alarm for this app
    alarmManager.cancel(intent);
    if (android.os.Build.VERSION.SDK_INT == 18) {
      alarmManager.set(AlarmManager.RTC_WAKEUP, date, intent);
    } else if (android.os.Build.VERSION.SDK_INT >= 19 && android.os.Build.VERSION.SDK_INT < 23) {
      alarmManager.setExact(AlarmManager.RTC_WAKEUP, date, intent);
    } else if (android.os.Build.VERSION.SDK_INT >= 23) {
      alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, date, intent);
    }
  }
}
