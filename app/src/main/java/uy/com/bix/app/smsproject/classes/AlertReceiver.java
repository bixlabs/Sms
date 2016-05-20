package uy.com.bix.app.smsproject.classes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import uy.com.bix.app.smsproject.controllers.AlarmController;


public class AlertReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			switch (getResultCode()) {
				case Activity.RESULT_CANCELED:
					AlarmController alarmController = AlarmController.getInstance();
					alarmController.loadPreferencesAndSendMessages(context);
					break;
				case Activity.RESULT_OK:
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					alertErrorWhileSendingSms(context);
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					alertErrorWhileSendingSms(context);
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					alertErrorWhileSendingSms(context);
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					alertErrorWhileSendingSms(context);
					break;
			}
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void alertErrorWhileSendingSms (Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("Error", true);
		Toast.makeText(context, "Stop for error!!!", Toast.LENGTH_SHORT).show();
	}
}

