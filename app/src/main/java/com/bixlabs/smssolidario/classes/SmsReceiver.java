package com.bixlabs.smssolidario.classes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	public SmsReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// Get the data (SMS data) bound to intent
		Bundle bundle = intent.getExtras();

		SmsMessage[] messages = null;

		String messageToShow = "";

		if (bundle != null) {
			// Retrieve the SMS Messages received
			Object[] pdus = (Object[]) bundle.get("pdus");
			messages = new SmsMessage[pdus.length];

			// For every SMS message received
			for (int i = 0; i < messages.length; i++) {
				// Convert Object array
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				// Sender's phone number
				messageToShow += "SMS from " + messages[i].getOriginatingAddress() + " : ";
				// Fetch the text message
				messageToShow += messages[i].getMessageBody().toString();
				// Newline
				messageToShow += "\n";
			}

			// Display the entire SMS Message
			Toast.makeText(context, messageToShow, Toast.LENGTH_SHORT).show();
		}
	}
}
