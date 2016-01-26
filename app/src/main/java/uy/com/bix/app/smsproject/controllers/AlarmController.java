package uy.com.bix.app.smsproject.controllers;


import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class AlarmController extends AppCompatActivity {

	private static AlarmController instance = null;
	int newDay, newHour, newMinute;
	String phoneNumber, textMessage;
	boolean notify, lastDay;

	private AlarmController() {}

	public static AlarmController getInstance() {
		if (instance == null) {
			instance = new AlarmController();
		}
		return instance;
	}

	public void prepareAndSendMessage (Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

		notify = settings.getBoolean("Notify", false);
		newDay = settings.getInt("Day", 1);
		newHour = settings.getInt("Hour", 23);
		newMinute = settings.getInt("Minute", 30);
		lastDay = settings.getBoolean("LastDay", false);
		phoneNumber = settings.getString("Phone", "1");
		textMessage = settings.getString("Message", "Hello");

		Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show();

		sendMessages(phoneNumber, textMessage, context);
	}

	public void sendMessages(String phoneNum, String text, Context context) {
		final MessageController msgController = MessageController.getInstance();

		msgController.sendMessage(phoneNum, text, context);
	}
}
