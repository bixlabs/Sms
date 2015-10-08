package uy.com.bix.app.smsproject.services;



public class SettingsService {

	private static SettingsService instance = null;

	private SettingsService() {}

	public static SettingsService getInstance() {
		if (instance == null) {
			instance = new SettingsService();
		}
		return instance;
	}
}
