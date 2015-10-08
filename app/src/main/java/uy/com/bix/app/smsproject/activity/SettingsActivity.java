package uy.com.bix.app.smsproject.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.controllers.MessageController;

public class SettingsActivity extends AppCompatActivity {

	private Button mButtonDate;
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

		showDialogOnButtonClick();
	}

	public void showDialogOnButtonClick() {
		mButtonDate = (Button) findViewById(R.id.btn_date);

		mButtonDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_ID);
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
