package uy.com.bix.app.smsproject.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;

import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.SmsAnalyticsApplication;


public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d("Main Activity", "This is my message");


		final ActionBar actionBar = getActionBar();
		View view = getLayoutInflater().inflate(R.layout.actionbar_home, null);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(view);


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

			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}
}
