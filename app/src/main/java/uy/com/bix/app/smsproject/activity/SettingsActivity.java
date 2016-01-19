package uy.com.bix.app.smsproject.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;


import java.util.Calendar;

import uy.com.bix.app.smsproject.R;

public class SettingsActivity extends AppCompatActivity implements OnItemSelectedListener {

	private Button mButtonDate;
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

		//showDialogOnButtonClick();
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

	/*public void showDialogOnButtonClick() {
		mButtonDate = (Button) findViewById(R.id.btn_date);

		mButtonDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ID);
			}
		});
	}*/

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
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
