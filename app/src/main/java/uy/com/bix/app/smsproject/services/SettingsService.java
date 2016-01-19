package uy.com.bix.app.smsproject.services;


import android.content.Context;
import android.content.SharedPreferences;

public class SettingsService {

	private static SettingsService instance = null;
	private static final String SHARED_PREFERENCES_KEY = "ActivitySharedPreferences_data";


	private SettingsService() {}

	public static SettingsService getInstance() {
		if (instance == null) {
			instance = new SettingsService();
		}
		return instance;
	}
}
