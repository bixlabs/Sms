package com.bixlabs.smssolidario.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.awesomego.widget.ToggleButton;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import io.fabric.sdk.android.Fabric;
import com.bixlabs.smssolidario.BuildConfig;
import com.bixlabs.smssolidario.R;
import com.bixlabs.smssolidario.SmsAnalyticsApplication;

import static com.bixlabs.smssolidario.classes.Constants.COMPANY_NAME;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_ACTIVE;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_ALLOWED_PREMIUM;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_CONFIGURED;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_MESSAGE;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_PHONE;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_SENT_SMS;
import static com.bixlabs.smssolidario.classes.Constants.PREF_ACTIVE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_ALLOWED_PREMIUM;
import static com.bixlabs.smssolidario.classes.Constants.PREF_CONFIGURED;
import static com.bixlabs.smssolidario.classes.Constants.PREF_MAX;
import static com.bixlabs.smssolidario.classes.Constants.PREF_MESSAGE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_PHONE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_SENT_SMS;


public class MainActivity extends AppCompatActivity {

	public static final int SETTINGS_SCREEN = 1;
	private static final int PERMISSION_SEND_SMS = 24601;
	public static Context contextOfApplication;
	boolean isConfigured, isActive, allowedPremium;
	int totalMessages;
	SharedPreferences settings;
	ViewFlipper mainViewFlipper;
	TextView statusText, sentMessages;
	Button scheduleButton;
	ToggleButton toggleButton;
	SharedPreferences.Editor editor;
  AlertDialog smsPermissionDialog;
  AlertDialog.Builder builderDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_main);

    setToolBar();
    sendTrackerInstance();

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
		toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				changeAppStatus(on);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case (SETTINGS_SCREEN) : {
				if (resultCode == Activity.RESULT_OK) {
					// TODO Extract the data returned from the child Activity.
					Snackbar.make(this.findViewById(android.R.id.content), "Programación de donación cancelada", Snackbar.LENGTH_LONG)
							.setAction("VOLVER", new View.OnClickListener() {
								@Override
								public void onClick(View view) {
									Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
									startActivityForResult(intent, SETTINGS_SCREEN);
								}
							})
							.show();
				}
				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
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
    if (smsPermissionDialog == null) {
      testSmsPremium();
    }
    if (isActive && !allowedPremium) {
      smsPermissionDialog.show();
    } else {
      smsPermissionDialog.dismiss();
    }
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

  /**
   * Sets the tool bar with a custom view
   */
  private void setToolBar() {
    // Set the toolbar and the custom view with the logo
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();
    View view = getLayoutInflater().inflate(R.layout.partial_actionbar_main, null);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setCustomView(view);
  }

  /**
   * Sends the tracker for google analytics
   */
  private void sendTrackerInstance() {
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
  }

  /**
   * Shows the about dialog with info of the app
   */
  private void showAbout() {
		String versionName = BuildConfig.VERSION_NAME;
		View aboutView = getLayoutInflater().inflate(R.layout.dialog_about, null, false);
		TextView version = (TextView) aboutView.findViewById(R.id.textView_about_version);
		String aboutVersion = "Versión: " + versionName;
		version.setText(aboutVersion);
    DateTime today = DateTime.now();
    String copyrightText = "©" + Integer.toString(today.getYear()) + " " + COMPANY_NAME;
    TextView copyright = (TextView) aboutView.findViewById(R.id.textView_about_copyrights);
    copyright.setText(copyrightText);
    AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
		aboutDialog.setView(aboutView);
		aboutDialog.create();
		aboutDialog.show();
	}

	private void checkFirstLayout() {
		if (!isActive && totalMessages < 1) {
      setButtonIconText(
        scheduleButton,
        R.drawable.ic_configure_schedule,
        R.string.activity_main_configure_schedule_text
      );
			mainViewFlipper.setDisplayedChild(0);
		}
	}

	private void checkSecondLayout() {
		if (isConfigured && totalMessages < 1 && isActive) {
      setButtonIconText(
        scheduleButton,
        R.drawable.ic_edit_schedule,
        R.string.activity_main_edit_donation
      );
			mainViewFlipper.setDisplayedChild(1);
		}
	}

	private void checkThirdLayout() {
		if (isConfigured && totalMessages >= 1 ) {
			if (isActive) {
        setButtonIconText(
          scheduleButton,
          R.drawable.ic_edit_schedule,
          R.string.activity_main_edit_donation
        );
			} else {
        setButtonIconText(
          scheduleButton,
          R.drawable.ic_configure_schedule,
          R.string.activity_main_configure_schedule_text
        );
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

  /**
   * Sets an icon and a text in a button
   * @param button button which will contain the icon and the text
   * @param iconResId id of the icon resource to be applied to the button
   * @param stringResId id of the string resource to be applied to the button
   */
  private void setButtonIconText(Button button, int iconResId, int stringResId) {
    button.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
    button.setText(getString(stringResId));
  }

	private void goToSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
    Bundle bundleParameter = new Bundle();
    bundleParameter.putBoolean("active", isActive);
    intent.putExtras(bundleParameter);
		startActivityForResult(intent, SETTINGS_SCREEN);
	}

	private void checkConfiguration() {
		isActive = settings.getBoolean(PREF_ACTIVE, DEFAULT_ACTIVE);
		totalMessages = settings.getInt(PREF_SENT_SMS, DEFAULT_SENT_SMS);
		isConfigured = settings.getBoolean(PREF_CONFIGURED, DEFAULT_CONFIGURED);
    allowedPremium = settings.getBoolean(PREF_ALLOWED_PREMIUM, DEFAULT_ALLOWED_PREMIUM);
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

  /**
   * Checks the value for the Max SMS to be send key in the shared preferences
   * This key was previously a string in older versions
   * So we need to check if the key is string to delete it as it needs
   * To be an integer now, otherwise an exception will be thrown
   */
  private void checkMaxKey() {
		boolean convertionError = false;

		try {
			settings.getString(PREF_MAX, "1");
		} catch(ClassCastException e) {
			convertionError = true;
			Log.e("Conversion error", "Max key is not string");
		}

		// If there wasn't an error it means that the key max contains a string
		// So we have to delete it, this is because in older versions
		// This key was a string and now it needs to be integer
		if (!convertionError) {
			editor = settings.edit();
			editor.remove(PREF_MAX);
			editor.apply();
		}
	}

	public void changeAppStatus(boolean toggleStatus) {
		if (toggleStatus) {
			checkPermissions();
		} else {
			statusText.setText(R.string.activity_main_app_status_inactive);
			editor = settings.edit();
			editor.putBoolean(PREF_ACTIVE, false);
			editor.apply();
			checkConfiguration();
			checkFirstLayout();
			if (isConfigured && totalMessages > 0 ) {
        setButtonIconText(
          scheduleButton,
          R.drawable.ic_configure_schedule,
          R.string.activity_main_configure_schedule_text
        );
			}
		}
	}

  public void testSmsPremium() {
    builderDialog = new AlertDialog.Builder(this);
    View aboutView = getLayoutInflater().inflate(R.layout.sms_permission, null, false);
    builderDialog.setView(aboutView);
    DateTime today = DateTime.now();
    String copyrightText = "©" + Integer.toString(today.getYear()) + " " + COMPANY_NAME;
    TextView copyright = (TextView) aboutView.findViewById(R.id.textView_permission_copyrights);
    copyright.setText(copyrightText);
    smsPermissionDialog = builderDialog.create();
    Button validate = (Button) aboutView.findViewById(R.id.button_permission_validated);
    validate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        smsPermissionDialog.dismiss();
        sendTestSms();
      }
    });
  }

  public void sendTestSms() {
    String phoneNumber = settings.getString(PREF_PHONE, DEFAULT_PHONE);
    String textMessage = settings.getString(PREF_MESSAGE, DEFAULT_MESSAGE);
    editor = settings.edit();
    editor.putBoolean(PREF_ALLOWED_PREMIUM, true);
    editor.apply();
    try {
      SmsManager smsManager = SmsManager.getDefault();
      smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
    } catch (IllegalArgumentException e) {
      Toast.makeText(contextOfApplication, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
  }
}
