package uy.com.bix.app.smsproject.classes;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import uy.com.bix.app.smsproject.controllers.AlarmController;


public class AlertReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.v("ALERTA ", "RECIBIDA");
		Log.v("Codigo resultado ", String.valueOf(getResultCode()));

		try {
			switch (getResultCode()) {
				case Activity.RESULT_CANCELED:
					AlarmController alarmController = AlarmController.getInstance();
					alarmController.prepareAndSendMessage(context);
					break;
				case Activity.RESULT_OK:
					Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();
					Log.v("Mensaje  ", "Enviado");
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show();
					Log.v("2Mensaje  ", "Enviado");
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show();
					Log.v("3Mensaje  ", "Enviado");
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show();
					Log.v("4Mensaje  ", "Enviado");
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show();
					Log.v("5Mensaje  ", "Enviado");
					break;
			}
		} catch (Exception e) {
			Log.d("Exception: ", e.getMessage());
		}
	}
}

