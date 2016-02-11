package uy.com.bix.app.smsproject.activity;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_HOUR;
import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_MINUTES;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;


import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.classes.AlertReceiver;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	int expirationYear, expirationMonth, expirationDay, expirationHour, expirationMinute;
	boolean isLastDay, isActive;
	private AppCompatDelegate mDelegate;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1111;
	static final String MESSAGE_KEY = "Message";
	static final String NUMBER_KEY = "Phone";
	static final String MAX_KEY = "Max";
	static final String DATE_KEY = "btnDateFilter";
	static final String TIME_KEY = "btnTimeFilter";
	public static Context contextOfApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getDelegate().installViewFactory();
		getDelegate().onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_activity);
		contextOfApplication = getApplicationContext();

		Preference btnDateFilter = findPreference("btnDateFilter");
		btnDateFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});

		Preference btnTimeFilter = findPreference("btnTimeFilter");
		btnTimeFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(TIME_DIALOG_ID);
				return false;
			}
		});

		JodaTimeAndroid.init(this);
		DateTime today = DateTime.now();

		// Get saved user settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);

		// The month in the date picker is in 0-11 range
		expirationMonth = settings.getInt("Month", today.getMonthOfYear()) - 1;
		expirationDay = settings.getInt("Day", today.getDayOfMonth());
		expirationHour = settings.getInt("Hour", DEFAULT_HOUR);
		expirationMinute = settings.getInt("Minute", DEFAULT_MINUTES);
		isLastDay = settings.getBoolean("LastDay", false);
		expirationYear = settings.getInt("Year", today.getYear());

		initSummary(getPreferenceScreen());
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getDelegate().onPostCreate(savedInstanceState);
	}

	@Override
	public MenuInflater getMenuInflater() {
		return getDelegate().getMenuInflater();
	}

	@Override
	public void setContentView(@LayoutRes int layoutResID) {
		getDelegate().setContentView(layoutResID);
	}

	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		getDelegate().addContentView(view, params);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getDelegate().onPostResume();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		getDelegate().setTitle(title);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getDelegate().onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		super.onStop();
		getDelegate().onStop();
		saveSettings();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		getDelegate().onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Set up a listener whenever a key changes
		getPreferenceScreen()
			.getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Unregister the listener whenever a key changes
		getPreferenceScreen()
			.getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this);
		saveSettings();
	}

	public void invalidateOptionsMenu() {
		getDelegate().invalidateOptionsMenu();
	}

	private AppCompatDelegate getDelegate() {
		if (mDelegate == null) {
			mDelegate = AppCompatDelegate.create(this, null);
		}
		return mDelegate;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
																				String key) {
		updatePrefSummary(findPreference(key));
	}

	private void initSummary(Preference p) {
		if (p instanceof PreferenceGroup) {
			PreferenceGroup pGrp = (PreferenceGroup) p;
			for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
				initSummary(pGrp.getPreference(i));
			}
		}
		else {
			updatePrefSummary(p);
		}
	}

	private void updatePrefSummary(Preference p) {
		String key = p.getKey();
		if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			if (editTextPref.getText() == null || editTextPref.getText().equals("")) {
				switch(p.getKey()) {
					case MESSAGE_KEY:
						p.setSummary("Mensaje a enviar");
						break;
					case MAX_KEY:
						p.setSummary("Tope maximo de mensajes a enviar");
						break;
					case NUMBER_KEY:
						p.setSummary("Numero destinatario de la donacion");
						break;
				}
			}
			else {
				p.setSummary(editTextPref.getText());
			}
		}
		else if (key.equals(DATE_KEY)) {
			p.setSummary(String.valueOf(expirationDay) + "/" +
				String.valueOf(expirationMonth + 1) + "/" +
				String.valueOf(expirationYear));
		}
		else if (key.equals(TIME_KEY)) {
			if (expirationMinute < 10) {
				p.setSummary(String.valueOf(expirationHour) + ":" + "0" + String.valueOf(expirationMinute));
			}
			else {
				p.setSummary(String.valueOf(expirationHour) + ":" + String.valueOf(expirationMinute));
			}

		}

	}

	public void saveSettings() {
		DateTime sendingDate = DateTime.now();
		sendingDate = sendingDate.withMinuteOfHour(expirationMinute);
		sendingDate = sendingDate.withHourOfDay(expirationHour);
		sendingDate = sendingDate.withYear(expirationYear);
		sendingDate = sendingDate.withMonthOfYear(expirationMonth + 1);
		sendingDate = sendingDate.withDayOfMonth(expirationDay);

		if (sendingDate.isBeforeNow()) {
			Toast.makeText(contextOfApplication,
				"La fecha debe ser posterior al momento actual, guardado cancelado",
				Toast.LENGTH_LONG).show();
		}
		else {

			// We save the user preferences using the default shared preferences
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("Year", expirationYear);
			editor.putInt("Month", expirationMonth + 1);
			editor.putInt("Day", expirationDay);
			editor.putInt("Hour", expirationHour);
			editor.putInt("Minute", expirationMinute);
			editor.apply();

			// The date must be in milliseconds
			long whenToFireTask = sendingDate.getMillis();

			isActive = settings.getBoolean("Active", false);
			if (isActive) {
				scheduleAlarm(whenToFireTask);
				System.out.println(sendingDate);
			}

			Toast.makeText(contextOfApplication, "Guardado exitoso", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DATE_DIALOG_ID) {
			DatePickerDialog datePickerDialog = new DatePickerDialog(this, dPickerListener, expirationYear, expirationMonth, expirationDay);
			datePickerDialog.getDatePicker().setMinDate(DateTime.now().getMillis() - 1000);
			return datePickerDialog;
		}
		else if (id == TIME_DIALOG_ID) {
			return new TimePickerDialog(this, timePickerListener, expirationHour, expirationMinute, false);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener dPickerListener
		= new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				expirationYear = year;
				expirationMonth = month;
				expirationDay = day;
				isLastDay = false;

				// If the day selected is the last we update the boolean isLastDay
				DateTime dt = DateTime.now();
				dt = dt.withMonthOfYear(expirationMonth + 1);
				dt = dt.dayOfMonth().withMaximumValue();
				int last = dt.getDayOfMonth();
				if (expirationDay == last) {
					isLastDay = true;
				}

				updatePrefSummary(findPreference(DATE_KEY));
			}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			expirationHour   = hourOfDay;
			expirationMinute = minutes;
			updatePrefSummary(findPreference(TIME_KEY));
		}

	};

	public void scheduleAlarm(long dateOfFiring)
	{
		// create an Intent and set the class which will execute when Alarm triggers, here we have
		// given AlertReceiver in the Intent, the onReceive() method of this class will execute when
		// alarm triggers
		Intent intentAlarm = new Intent(this, AlertReceiver.class);

		// Create the object
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

		// Cancel any existing alarm for this app
		alarmManager.cancel(pendingIntent);

		// set the alarm for particular time
		alarmManager.set(AlarmManager.RTC_WAKEUP, dateOfFiring, pendingIntent);
	}
}
