package uy.com.bix.app.smsproject.activity;

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

          try {
            //We use SmsManager API to send the message
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNro, null, smsText, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!", Toast.LENGTH_LONG).show();
          } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
          }
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
