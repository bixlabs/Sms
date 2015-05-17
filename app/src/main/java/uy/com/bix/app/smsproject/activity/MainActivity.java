package uy.com.bix.app.smsproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import uy.com.bix.app.smsproject.R;
import uy.com.bix.app.smsproject.service.SmsIntentService;


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
                Intent smsIntent = new Intent(getBaseContext(), SmsIntentService.class);
                Bundle smsData = new Bundle();
                smsData.putString(SmsIntentService.SMS_MESSAGE, mEditTextMessage.getText().toString());
                smsData.putString(SmsIntentService.SMS_PHONE, mEditTextTelephone.getText().toString());
                smsIntent.putExtras(smsData);

                startService(smsIntent);
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
