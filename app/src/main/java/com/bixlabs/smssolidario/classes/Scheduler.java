package com.bixlabs.smssolidario.classes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Scheduler {

  public void scheduleAlarm(Context context, long dateOfFiring)
  {
    // create an Intent and set the class which will execute when Alarm triggers, here we have
    // given AlertReceiver in the Intent, the onReceive() method of this class will execute when
    // alarm triggers
    Intent intentAlarm = new Intent(context, AlertReceiver.class);

    // Create the object
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

    // Cancel any existing alarm for this app
    alarmManager.cancel(pendingIntent);

    // set the alarm for particular time
    alarmManager.set(AlarmManager.RTC_WAKEUP, dateOfFiring, pendingIntent);
  }

}
