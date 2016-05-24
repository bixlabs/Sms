package uy.com.bix.app.smsproject.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.awesomego.widget.ToggleButton;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;
import uy.com.bix.app.smsproject.BuildConfig;
import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.SmsAnalyticsApplication;

import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_ACTIVE;
import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_CONFIGURED;
import static uy.com.bix.app.smsproject.classes.Constants.DEFAULT_SENT_SMS;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_ACTIVE;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_CONFIGURED;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_MAX;
import static uy.com.bix.app.smsproject.classes.Constants.KEY_SENT_SMS;


public class MainActivity extends AppCompatActivity {

	private static final int PERMISSION_SEND_SMS = 24601;
	public static Context contextOfApplication;
	boolean isConfigured, isActive;
	int totalMessages;
	SharedPreferences settings;
	ViewFlipper mainViewFlipper;
	TextView statusText, sentMessages;
	Button scheduleButton;
	ToggleButton toggleButton;
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_main);

		// Set the toolbar and the custom view with the logo
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app);
		setSupportActionBar(toolbar);
		final ActionBar actionBar = getSupportActionBar();
		View view = getLayoutInflater().inflate(R.layout.actionbar_home, null);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(view);

		// Obtain the shared Tracker instance.
		SmsAnalyticsApplication application = (SmsAnalyticsApplication) getApplication();
		Tracker tracker = application.getDefaultTracker();

		Product product = new Product()
				.setName("Sms")
				.setPrice(40.00);

		ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)
				.setTransactionId("TestingTransactionId");

		// Add the transaction data to the event.
		HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
				.setCategory("Donation")
				.setAction("Purchase")
				.addProduct(product)
				.setProductAction(productAction);

		// Send the transaction data with the event.
		tracker.send(builder.build());

		JodaTimeAndroid.init(this);
		contextOfApplication = getApplicationContext();
		settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
		checkMaxKey();

		// Set the click listener for program schedule button
		scheduleButton = (Button) findViewById(R.id.button_configure_schedule);
		scheduleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkPermissions();
			}
		});

		mainViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper_main);
		statusText = (TextView) findViewById(R.id.text_app_status);
		sentMessages = (TextView) findViewById(R.id.text_sms_quantity);

		// Set the toggle button value and event when toggle changes
		toggleButton = (ToggleButton) findViewById(R.id.toggle_app_status);
		toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged(){
			@Override
			public void onToggle(boolean on) {
				changeAppStatus(on);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
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
			checkPermissions();
		}

		if (id == R.id.action_about) {
			showAbout();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		checkConfiguration();
		checkIfActive();
		changeLayout();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_SEND_SMS: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					goToSettings();
				} else {
					toggleButton.setToggleOff();
				}
			}
		}
	}

	private void showAbout() {
		String versionName = BuildConfig.VERSION_NAME;
		View aboutView = getLayoutInflater().inflate(R.layout.about, null, false);
		TextView version = (TextView) aboutView.findViewById(R.id.textView_about_version);
		String aboutVersion = "Versión: " + versionName;
		version.setText(aboutVersion);
		TextView interest = (TextView) aboutView.findViewById(R.id.textView_about_interest);
		String interestParagraph = getString(R.string.about_interest);
		SpannableString interestText = new SpannableString(interestParagraph);
		interestText.setSpan(new ForegroundColorSpan(Color.BLUE), 132, 147, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		interest.setText(interestText);
		AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
		aboutDialog.setView(aboutView);
		aboutDialog.create();
		aboutDialog.show();
	}

	private void checkFirstLayout() {
		if (!isActive && totalMessages < 1) {
			scheduleButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_configure_schedule, 0, 0, 0);
			scheduleButton.setText(R.string.activity_main_configure_schedule_text);
			mainViewFlipper.setDisplayedChild(0);
		}
	}

	private void checkSecondLayout() {
		if (isConfigured && totalMessages < 1 ) {
			scheduleButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_donation, 0, 0, 0);
			scheduleButton.setText(R.string.activity_main_edit_donation);
			mainViewFlipper.setDisplayedChild(1);
		}
	}

	private void checkThirdLayout() {
		if (isConfigured && totalMessages >= 1 ) {
			if (isActive) {
				scheduleButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_edit_donation, 0, 0, 0);
				scheduleButton.setText(R.string.activity_main_edit_donation);
			} else {
				scheduleButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_configure_schedule, 0, 0, 0);
				scheduleButton.setText(R.string.activity_main_configure_schedule_text);
			}
			sentMessages.setText(Integer.toString(totalMessages));
			mainViewFlipper.setDisplayedChild(2);
		}
	}

	private void changeLayout(){
		checkFirstLayout();
		checkSecondLayout();
		checkThirdLayout();
	}

	private void goToSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	private void checkConfiguration() {
		isActive = settings.getBoolean(KEY_ACTIVE, DEFAULT_ACTIVE);
		totalMessages = settings.getInt(KEY_SENT_SMS, DEFAULT_SENT_SMS);
		isConfigured = settings.getBoolean(KEY_CONFIGURED, DEFAULT_CONFIGURED);
	}

	private void checkIfActive() {
		if (isActive) {
			toggleButton.setToggleOn();
			statusText.setText(R.string.activity_main_app_status_active);
		} else {
			toggleButton.setToggleOff();
			statusText.setText(R.string.activity_main_app_status_inactive);
		}
	}

	private void checkPermissions() {
		if (ContextCompat.checkSelfPermission(this,
						Manifest.permission.SEND_SMS)
						!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
		} else {
			goToSettings();
		}
	}

	private void checkMaxKey() {
		boolean convertionError = false;

		try {
			settings.getString(KEY_MAX, "1");
		} catch(ClassCastException e) {
			convertionError = true;
			Log.e("Conversion error", "Max key is not string");
		}

		// If there wasn't an error it means that the key max contains a string
		// So we have to delete it, this is because in older versions
		// This key was a string and now it needs to be integer
		if (!convertionError) {
			editor = settings.edit();
			editor.remove(KEY_MAX);
			editor.apply();
		}
	}

	public void changeAppStatus(boolean toggleStatus) {
		if (toggleStatus) {
			checkPermissions();
		} else {
			statusText.setText(R.string.activity_main_app_status_inactive);
			editor = settings.edit();
			editor.putBoolean(KEY_ACTIVE, false);
			editor.apply();
			checkConfiguration();
			checkFirstLayout();
			if (isConfigured && totalMessages > 0 ) {
				scheduleButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_configure_schedule, 0, 0, 0);
				scheduleButton.setText(R.string.activity_main_configure_schedule_text);
			}
		}
	}
}
