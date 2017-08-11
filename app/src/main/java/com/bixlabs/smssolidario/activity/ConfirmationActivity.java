package com.bixlabs.smssolidario.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bixlabs.smssolidario.R;
import com.bixlabs.smssolidario.controllers.MessageController;

import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_MESSAGE;
import static com.bixlabs.smssolidario.classes.Constants.DEFAULT_PHONE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_MESSAGE;
import static com.bixlabs.smssolidario.classes.Constants.PREF_PHONE;


public class ConfirmationActivity extends AppCompatActivity {

  public static Context contextOfApplication;
  static final String MESSAGE_KEY = "Message";
  static final String NUMBER_KEY = "Phone";
  private EditText textMessageInput, phoneInput;
  public static String phoneNumber, textMessage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_confirmation);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    contextOfApplication = getApplicationContext();
    getSupportActionBar().setTitle("Confirma tu donación");

    // Get saved user settings
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
    phoneNumber = settings.getString(PREF_PHONE, DEFAULT_PHONE);
    textMessage = settings.getString(PREF_MESSAGE, DEFAULT_MESSAGE);

    // Buttons behaviour
    Button confirmButton = (Button) findViewById(R.id.confirmButton);
    confirmButton.setOnClickListener( new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        sendMessage(phoneNumber, textMessage);
      }
    });

    Button cancelButton = (Button) findViewById(R.id.cancelButton);
    cancelButton.setOnClickListener( new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        goToMainActivity();
      }
    });

    // Text inputs behaviour
    textMessageInput = (EditText) findViewById(R.id.textMessageInput);
    textMessageInput.setText(textMessage, TextView.BufferType.EDITABLE);

    textMessageInput.addTextChangedListener(new TextWatcher() {

      public void afterTextChanged(Editable s) {

        textMessage = textMessageInput.getText().toString();
        SharedPreferences userSettings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(PREF_MESSAGE, textMessage);
        editor.apply();

      }

      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      public void onTextChanged(CharSequence s, int start, int before, int count) {}
    });

    phoneInput = (EditText) findViewById(R.id.phoneInput);
    phoneInput.setText(phoneNumber, TextView.BufferType.EDITABLE);

    phoneInput.addTextChangedListener(new TextWatcher() {

      public void afterTextChanged(Editable s) {

        phoneNumber = phoneInput.getText().toString();
        SharedPreferences userSettings = PreferenceManager.getDefaultSharedPreferences(contextOfApplication);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(PREF_MESSAGE, phoneNumber);
        editor.apply();

      }

      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      public void onTextChanged(CharSequence s, int start, int before, int count) {}
    });

  }

  private void goToMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }

  private void sendMessage(String phoneNumber, String textMessage) {
    MessageController msgController = MessageController.getInstance();
    msgController.sendMessage(phoneNumber, textMessage, contextOfApplication);
    goToMainActivity();
  }

}
