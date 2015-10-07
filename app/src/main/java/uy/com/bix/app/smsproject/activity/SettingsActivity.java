package uy.com.bix.app.smsproject.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.controllers.MessageController;

public class SettingsActivity extends AppCompatActivity {

	private Button mButtonDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		connectUi();
	}

	private void connectUi() {
		mButtonDate = (Button) findViewById(R.id.btn_send);

		mButtonDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//We need the application context n order to send the sms
				Context appContext = getApplicationContext();

				Intent intent;
			}
		});
	}

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
