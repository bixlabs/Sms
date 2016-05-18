package uy.com.bix.app.smsproject.activity;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.LayoutRes;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_ACTIVE;
import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_HOUR;
import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_MINUTES;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_ACTIVE;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_CONFIGURED;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_DAY;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_HOUR;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_LAST_DAY;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_MINUTE;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_MONTH;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_YEAR;
import static uy.com.bix.app.smsproject.classes.Constants.ORGANIZATION_INFO;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;


import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.classes.Scheduler;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener,
				android.support.v7.widget.Toolbar.OnMenuItemClickListener{

	int expirationYear, expirationMonth, expirationDay, expirationHour, expirationMinute;
	String selectedOrganization;
	boolean isLastDay;
	private AppCompatDelegate mDelegate;
	Preference messagePreference, phonePreference;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1111;
	static final String MESSAGE_KEY = "Message";
	static final String NUMBER_KEY = "Phone";
	static final String MAX_KEY = "Max";
	static final String DATE_KEY = "btnDateFilter";
	static final String TIME_KEY = "btnTimeFilter";
	static final String ORGANIZATION_KEY = "Organization";
	public static Context contextOfApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getDelegate().installViewFactory();
		getDelegate().onCreate(savedInstanceState);
		super.onCreate(savedInstanceState);
		// Set up the toolbar
		setContentView(R.layout.activity_settings);
		setTheme(R.style.PreferenceList);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
		toolbar.inflateMenu(R.menu.menu_settings);
		toolbar.setOnMenuItemClickListener(this);
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

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

		// We must hide the number and message preferences
		messagePreference = findPreference(MESSAGE_KEY);
		phonePreference = findPreference(NUMBER_KEY);
		hideMessageAndPhonePreferences();

		JodaTimeAndroid.init(this);
		getPreferencesFromUser();

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
		if (p != null) {
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
			else if (key.equals(ORGANIZATION_KEY)) {
				ListPreference lp = (ListPreference) p;
				if (lp.getValue().equals("Personalizada")) {
					showMessageAndPhonePreferences();
					Toast.makeText(contextOfApplication, "Por favor, ingresa número y mensaje para donar",
						Toast.LENGTH_SHORT).show();
				}
				else {
					hideMessageAndPhonePreferences();
					updateMessageAndPhoneData(lp.getValue());
				}
				p.setSummary(lp.getEntry());
			}
		}
	}

	private void hideMessageAndPhonePreferences() {
		if (findPreference("Phone") != null && findPreference("Message") != null) {
			PreferenceScreen screen = this.getPreferenceScreen();
			screen.removePreference(messagePreference);
			screen.removePreference(phonePreference);
		}
	}

	private void showMessageAndPhonePreferences() {
		PreferenceScreen screen = this.getPreferenceScreen();
		screen.addPreference(messagePreference);
		updatePrefSummary(messagePreference);
		screen.addPreference(phonePreference);
		updatePrefSummary(phonePreference);
	}

	private void getPreferencesFromUser() {
		DateTime today = DateTime.now();

		// Get saved user settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);

		// The month in the date picker is in 0-11 range
		expirationMonth = settings.getInt(KEY_MONTH, today.getMonthOfYear()) - 1;
		expirationDay = settings.getInt(KEY_DAY, today.getDayOfMonth());
		expirationHour = settings.getInt(KEY_HOUR, DEFAULT_HOUR);
		expirationMinute = settings.getInt(KEY_MINUTE, DEFAULT_MINUTES);
		isLastDay = settings.getBoolean(KEY_LAST_DAY, false);
		expirationYear = settings.getInt(KEY_YEAR, today.getYear());
		selectedOrganization = settings.getString(ORGANIZATION_KEY, "ASH");
	}

	private void updateMessageAndPhoneData(String key) {
		System.out.println(key);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
		SharedPreferences.Editor editor = settings.edit();
		if (key != "0") {
			editor.putString(MESSAGE_KEY, ORGANIZATION_INFO.get(key)[0]);
			editor.putString(NUMBER_KEY, ORGANIZATION_INFO.get(key)[1]);
			editor.apply();
		}
		else {
			editor.putString(MESSAGE_KEY, ORGANIZATION_INFO.get("ASH")[0]);
			editor.putString(NUMBER_KEY, ORGANIZATION_INFO.get("ASH")[1]);
			editor.apply();
		}

	};

	public void saveSettings() {
		DateTime sendingDate = DateTime.now();
		sendingDate = sendingDate.withMinuteOfHour(expirationMinute);
		sendingDate = sendingDate.withHourOfDay(expirationHour);
		sendingDate = sendingDate.withYear(expirationYear);
		sendingDate = sendingDate.withMonthOfYear(expirationMonth + 1);
		sendingDate = sendingDate.withDayOfMonth(expirationDay);
		sendingDate = sendingDate.withSecondOfMinute(10);

		if (sendingDate.isBeforeNow()) {
			Toast.makeText(contextOfApplication,
				"La fecha debe ser posterior al momento actual, guardado cancelado",
				Toast.LENGTH_LONG).show();
		}
		else {

			// We save the user preferences using the default shared preferences
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt(KEY_YEAR, expirationYear);
			editor.putInt(KEY_MONTH, expirationMonth + 1);
			editor.putInt(KEY_DAY, expirationDay);
			editor.putInt(KEY_HOUR, expirationHour);
			editor.putInt(KEY_MINUTE, expirationMinute);
			editor.putBoolean(KEY_CONFIGURED, true);
			editor.putBoolean(KEY_ACTIVE, true);
			editor.apply();

			// The date must be in milliseconds
			long whenToFireTask = sendingDate.getMillis();
			Scheduler scheduler = new Scheduler();
			scheduler.scheduleAlarm(this, whenToFireTask);
			System.out.println(sendingDate);

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

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_done) {
			finish();
		}
		return false;
	}
	private DatePickerDialog.OnDateSetListener dPickerListener
		= new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				DateTime dt = DateTime.now();
				DateTime selectedDate = dt.withYear(year);
				selectedDate = selectedDate.withMonthOfYear(month + 1);
				selectedDate = selectedDate.withDayOfMonth(day);
				if (selectedDate.isBeforeNow()) {
					Toast.makeText(contextOfApplication,
						"Por favor elige una fecha posterior a hoy",
						Toast.LENGTH_LONG).show();
					expirationYear = dt.getYear();
					expirationMonth = dt.getMonthOfYear() - 1;
					expirationDay = dt.getDayOfMonth();
				}
				else {
					expirationYear = year;
					expirationMonth = month;
					expirationDay = day;
				}
				isLastDay = false;

				// If the day selected is the last we update the boolean isLastDay
				dt = DateTime.now();
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
}
