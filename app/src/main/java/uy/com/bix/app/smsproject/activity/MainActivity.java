package uy.com.bix.app.smsproject.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import uy.com.bix.app.smsproject.R;


public class MainActivity extends AppCompatActivity {

    private Button mButtonSend;
    private EditText mEditTextTelephone;
    private EditText mEditTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectUi();
    }

    private void connectUi() {
      mButtonSend = (Button) findViewById(R.id.btn_send);
      mEditTextTelephone = (EditText) findViewById(R.id.txt_telephone_num);
      mEditTextMessage = (EditText) findViewById(R.id.txt_message);

      mButtonSend.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          //We get the number and the message that were inserted in the view
          String phoneNro = mEditTextTelephone.getText().toString();
          String smsText = mEditTextMessage.getText().toString();

          String sent = "SMS_SENT";
          String delivered = "SMS_DELIVERED";

          PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
            new Intent(sent), 0);

          PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
            new Intent(delivered), 0);

          //---when the SMS has been sent---
          registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
              switch (getResultCode())
              {
                case Activity.RESULT_OK:
                  Toast.makeText(getBaseContext(), "SMS sent",
                    Toast.LENGTH_SHORT).show();
                  break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                  Toast.makeText(getBaseContext(), "Generic failure",
                    Toast.LENGTH_SHORT).show();
                  break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                  Toast.makeText(getBaseContext(), "No service",
                    Toast.LENGTH_SHORT).show();
                  break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                  Toast.makeText(getBaseContext(), "Null PDU",
                    Toast.LENGTH_SHORT).show();
                  break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                  Toast.makeText(getBaseContext(), "Radio off",
                    Toast.LENGTH_SHORT).show();
                  break;
              }
            }
          }, new IntentFilter(sent));

          //---when the SMS has been delivered---
          registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
              switch (getResultCode())
              {
                case Activity.RESULT_OK:
                  Toast.makeText(getBaseContext(), "SMS delivered",
                    Toast.LENGTH_SHORT).show();
                  break;
                case Activity.RESULT_CANCELED:
                  Toast.makeText(getBaseContext(), "SMS not delivered",
                    Toast.LENGTH_SHORT).show();
                  break;
              }
            }
          }, new IntentFilter(delivered));

          //We use SmsManager API to send the message
          SmsManager smsManager = SmsManager.getDefault();
          smsManager.sendTextMessage(phoneNro, null, smsText, sentPI, deliveredPI);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
