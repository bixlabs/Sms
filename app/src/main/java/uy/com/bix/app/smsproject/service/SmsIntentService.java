package uy.com.bix.app.smsproject.service;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

public class SmsIntentService extends IntentService {

    public final static String SMS_PHONE = "destinationAddress";
    public final static String SMS_MESSAGE = "text";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public SmsIntentService() {
        super("SmsIntent");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle smsData = intent.getExtras();

        String destinationAddress = smsData.getString(SMS_PHONE);
        String text = smsData.getString(SMS_PHONE);

        SmsManager.getDefault().sendTextMessage(destinationAddress, null, text, null, null);
    }
}
