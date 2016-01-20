package uy.com.bix.app.smsproject.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Switch;
import android.widget.TextView;


import java.util.Calendar;

import uy.com.bix.app.smsproject.R;

public class SettingsActivity extends AppCompatActivity implements OnItemSelectedListener {

	private Button mButtonCancel, mButtonSave;
	private EditText mEditPhone, mEditMessage, mEditMax;
	private Switch isActive;
	private CheckBox notify;
	private Spinner spinner;
	int newYear, newMonth, newDay;
	static final int DIALOG_ID = 0;

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


		cancelOnButtonClick();
		saveOnButtonClick();
	}

	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

		switch (position) {
			case 0:
				// Whatever you want to happen when the first item gets selected
				break;
			case 1:
				// Whatever you want to happen when the second item gets selected
				break;
			case 2:
				// Whatever you want to happen when the third item gets selected
				showDialog(DIALOG_ID);
				break;

		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	public void cancelOnButtonClick() {
		mButtonCancel = (Button) findViewById(R.id.btn_cancel);

		mButtonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	public void saveOnButtonClick() {
		mButtonSave = (Button) findViewById(R.id.btn_save);

		mButtonSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences settings = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("Phone", mEditPhone.getText().toString());
				editor.putString("Message", mEditMessage.getText().toString());
				editor.putString("Max", mEditMax.getText().toString());
				editor.putBoolean("Active", isActive.isChecked());
				editor.putBoolean("Notify", notify.isChecked());
				editor.apply();
				Log.v("Phone", mEditPhone.getText().toString());
				Log.v("Message", mEditMessage.getText().toString());
				Log.v("Max", mEditMax.getText().toString());
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_ID) {
			return new DatePickerDialog(this, dPickerListener, newYear, newMonth, newDay);
		}
		return null;
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
}
