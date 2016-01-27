package uy.com.bix.app.smsproject.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.Calendar;

import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.classes.AlertReceiver;

public class SettingsActivity extends AppCompatActivity {

	private EditText mEditPhone, mEditMessage, mEditMax;
	private Switch isActive;
	private CheckBox notifyWhenSending;
	int newYear, newMonth, expirationDay, expirationHour, expirationMinute;
	private ImageButton mButtonHour;
	boolean isLastDay;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1111;
	public static Context contextOfApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		contextOfApplication = getApplicationContext();

		JodaTimeAndroid.init(this);

		final Calendar cal = Calendar.getInstance();
		newYear = cal.get(Calendar.YEAR);
		newMonth = cal.get(Calendar.MONTH);
		expirationDay = cal.get(Calendar.DATE);

		// Get saved user settings
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);

		mEditPhone = (EditText)findViewById(R.id.editTextPhone);
		mEditPhone.setText(settings.getString("Phone", "1"), TextView.BufferType.EDITABLE);
		mEditMessage = (EditText)findViewById(R.id.editTextMessage);
		mEditMessage.setText(settings.getString("Message", "1"), TextView.BufferType.EDITABLE);
		mEditMax = (EditText)findViewById(R.id.editTextMax);
		mEditMax.setText(settings.getString("Max", "1"), TextView.BufferType.EDITABLE);
		isActive = (Switch) findViewById(R.id.active_switch);
		isActive.setChecked(settings.getBoolean("Active", false));
		notifyWhenSending = (CheckBox) findViewById(R.id.notify_checkBox);
		notifyWhenSending.setChecked(settings.getBoolean("Notify", false));
		expirationDay = settings.getInt("Day", 1);
		expirationHour = settings.getInt("Hour", 23);
		expirationMinute = settings.getInt("Minute", 30);
		isLastDay = settings.getBoolean("LastDay", false);

		onTimeButtonClick();
		onDateButtonClick();
	}


	public void saveOnButtonClick() {
		if (isActive.isChecked()) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("Phone", mEditPhone.getText().toString());
			editor.putString("Message", mEditMessage.getText().toString());
			editor.putString("Max", mEditMax.getText().toString());
			editor.putBoolean("Active", isActive.isChecked());
			editor.putBoolean("Notify", notifyWhenSending.isChecked());
			editor.putBoolean("LastDay", isLastDay);
			editor.putInt("Day", expirationDay);
			editor.putInt("Hour", expirationHour);
			editor.putInt("Minute", expirationMinute);
			editor.apply();

			DateTime sendingDate = DateTime.now();
			sendingDate = sendingDate.withMinuteOfHour(expirationMinute);
			sendingDate = sendingDate.withHourOfDay(expirationHour);
			sendingDate = sendingDate.withDayOfMonth(expirationDay);
			System.out.println(sendingDate);
			System.out.println(isLastDay);

			// The date must be in milliseconds
			long whenToFireTask = sendingDate.getMillis();

			scheduleAlarm(whenToFireTask);
		}
		else {
			Toast.makeText(contextOfApplication, "Para guardar, debes activar la aplicación", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DATE_DIALOG_ID) {
			return new DatePickerDialog(this, dPickerListener, newYear, newMonth, expirationDay);
		}
		else if (id == TIME_DIALOG_ID) {
			return new TimePickerDialog(this, timePickerListener, expirationHour, expirationMinute, false);
		}
		return null;
	}

	private void onTimeButtonClick() {
		mButtonHour = (ImageButton) findViewById(R.id.btn_hour);

		mButtonHour.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
	}

	private void onDateButtonClick() {
		mButtonHour = (ImageButton) findViewById(R.id.btn_date);

		mButtonHour.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
	}

	private DatePickerDialog.OnDateSetListener dPickerListener
		= new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				newYear = year;
				newMonth = month + 1;
				expirationDay = day;
				isLastDay = false;

				// If the day selected is the last we update the boolean isisLastDay
				DateTime dt = DateTime.now();
				dt = dt.dayOfMonth().withMaximumValue();
				int last = dt.getDayOfMonth();
				if (expirationDay == last) {
					isLastDay = true;
				}
			}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			expirationHour   = hourOfDay;
			expirationMinute = minutes;
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_save) {

			saveOnButtonClick();
		}

		return super.onOptionsItemSelected(item);
	}

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
