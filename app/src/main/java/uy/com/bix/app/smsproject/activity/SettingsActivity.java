package uy.com.bix.app.smsproject.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import java.util.Calendar;

import uy.com.bix.app.smsproject.R;

public class SettingsActivity extends AppCompatActivity implements OnItemSelectedListener {

	private EditText mEditPhone, mEditMessage, mEditMax;
	private Switch isActive;
	private CheckBox notify;
	private Spinner spinner;
	int newYear, newMonth, newDay, newHour, newMinute, selectedOption;
	private ImageButton mButtonHour;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1111;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		final Calendar cal = Calendar.getInstance();
		newYear = cal.get(Calendar.YEAR);
		newMonth = cal.get(Calendar.MONTH);
		newDay = cal.get(Calendar.DATE);

		spinner = (Spinner) findViewById(R.id.spinner);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter <CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dates_array, android.R.layout.simple_spinner_item);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);

		// Get saved user settings
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		Log.v("Phone", settings.getString("Phone", "1"));
		Log.v("Message", settings.getString("Message", "1"));
		Log.v("Max", settings.getString("Max", "1"));

		mEditPhone = (EditText)findViewById(R.id.editTextPhone);
		mEditPhone.setText(settings.getString("Phone", "1"), TextView.BufferType.EDITABLE);
		mEditMessage = (EditText)findViewById(R.id.editTextMessage);
		mEditMessage.setText(settings.getString("Message", "1"), TextView.BufferType.EDITABLE);
		mEditMax = (EditText)findViewById(R.id.editTextMax);
		mEditMax.setText(settings.getString("Max", "1"), TextView.BufferType.EDITABLE);
		isActive = (Switch) findViewById(R.id.active_switch);
		isActive.setChecked(settings.getBoolean("Active", false));
		notify = (CheckBox) findViewById(R.id.notify_checkBox);
		notify.setChecked(settings.getBoolean("Notify", false));
		spinner.setSelection(settings.getInt("Option", 0));
		newDay = settings.getInt("Day", 1);
		newHour = settings.getInt("Hour", 23);
		newMinute = settings.getInt("Minute", 30);

		onTimeButtonClick();
	}

	// Dropdown options
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

		switch (position) {
			case 0:
				// Whatever you want to happen when the first item gets selected
				newDay = 1;
				selectedOption = position;
				break;
			case 1:
				// Whatever you want to happen when the second item gets selected
				newDay = 0; //Hay que discutir esto
				selectedOption = position;
				break;
			case 2:
				// Whatever you want to happen when the third item gets selected
				selectedOption = position;
				showDialog(DATE_DIALOG_ID);
				break;

		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}


	public void saveOnButtonClick() {
		SharedPreferences settings = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("Phone", mEditPhone.getText().toString());
		editor.putString("Message", mEditMessage.getText().toString());
		editor.putString("Max", mEditMax.getText().toString());
		editor.putBoolean("Active", isActive.isChecked());
		editor.putBoolean("Notify", notify.isChecked());
		editor.putInt("Option", selectedOption);
		editor.putInt("Day", newDay);
		editor.putInt("Hour", newHour);
		editor.putInt("Minute", newMinute);
		editor.apply();
		Log.v("Phone", mEditPhone.getText().toString());
		Log.v("Message", mEditMessage.getText().toString());
		Log.v("Max", mEditMax.getText().toString());
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DATE_DIALOG_ID) {
			return new DatePickerDialog(this, dPickerListener, newYear, newMonth, newDay);
		}
		else if (id == TIME_DIALOG_ID) {
			return new TimePickerDialog(this, timePickerListener, newHour, newMinute, false);
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

	private DatePickerDialog.OnDateSetListener dPickerListener
		= new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				newYear = year;
				newMonth = month + 1;
				newDay = day;
			}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			newHour   = hourOfDay;
			newMinute = minutes;

			Log.v("hora seleccionada: ", String.valueOf(newHour) + String.valueOf(newMinute));

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
}
