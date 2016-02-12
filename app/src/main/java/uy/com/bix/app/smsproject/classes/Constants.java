package uy.com.bix.app.smsproject.classes;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Constants {

	private Constants() {
	}

	public static final String MSG_SENT = "SMS_SENT";
	public static final int DEFAULT_HOUR = 23;
	public static final int DEFAULT_MINUTES = 30;
	public static final String DEFAULT_MAX = "50";
	public static final Map<String, String[]> ORGANIZATION_INFO;
	static {
		Map<String, String[]> aMap = new HashMap<>();
		aMap.put("ASH", new String[]{"Amigos", "24200"});
		aMap.put("FACB", new String[]{"Mamas", "62627"});
		aMap.put("UNCF", new String[]{"Unicef", "3662"});
		aMap.put("OJ", new String[]{"Niño", "20202"});
		aMap.put("PG", new String[]{"Amigo", "10100"});
		aMap.put("AI", new String[]{"Aldeas", "10101"});
		ORGANIZATION_INFO = Collections.unmodifiableMap(aMap);
	}
}
