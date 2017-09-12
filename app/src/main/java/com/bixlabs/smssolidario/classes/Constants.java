package com.bixlabs.smssolidario.classes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Constants {

  private Constants() {
  }

  public static final String SMS_SENT = "com.bixlabs.smssolidario.SENT_SMS";
  public static final String PREF_MINUTE = "Minute";
  public static final String PREF_HOUR = "Hour";
  public static final String PREF_DAY = "Day";
  public static final String PREF_MONTH = "Month";
  public static final String PREF_YEAR = "Year";
  public static final String PREF_ACTIVE = "Active";
  public static final String PREF_ALLOWED_PREMIUM = "AllowedPremium";
  public static final String PREF_SENT_SMS = "sentMessages";
  public static final String PREF_LAST_DAY = "LastDay";
  public static final String PREF_NOTIFY = "Notify";
  public static final String PREF_PHONE = "Phone";
  public static final String PREF_MESSAGE = "Message";
  public static final String PREF_MAX = "Max";
  public static final String PREF_SMS_TO_SEND = "MessagesToSend";
  public static final String PREF_ERROR = "Error";
  public static final String PREF_CONFIGURED = "Configured";
  public static final int DEFAULT_HOUR = 23;
  public static final int DEFAULT_MINUTES = 30;
  public static final int DEFAULT_MAX = 1;
  public static final int DEFAULT_SENT_SMS = 0;
  public static final int DEFAULT_SMS_TO_SEND = 0;
  public static final boolean DEFAULT_ACTIVE = false;
  public static final boolean DEFAULT_NOTIFY = false;
  public static final boolean DEFAULT_LAST_DAY = false;
  public static final boolean DEFAULT_ERROR = false;
  public static final boolean DEFAULT_CONFIGURED = false;
  public static final boolean DEFAULT_ALLOWED_PREMIUM = false;
  public static final String DEFAULT_MESSAGE = "Hello";
  public static final String DEFAULT_PHONE = "1";
  public static final String COMPANY_NAME = "BixLabs";
  public static final Map<String, String[]> ORGANIZATION_INFO;
  public static final int VISIBILITY_PUBLIC = 1;
  public static final int VISIBILITY_PRIVATE = 0;
  public static final int VISIBILITY_SECRET = -1;
  static {
    Map<String, String[]> aMap = new HashMap<>();
    aMap.put("ASH", new String[]{"Amigos", "24200"});
    aMap.put("FACB", new String[]{"Mamas", "62627"});
    aMap.put("UNCF", new String[]{"Unicef", "3662"});
    aMap.put("PG", new String[]{"Amigo", "10100"});
    aMap.put("AI", new String[]{"Aldeas", "10101"});
    aMap.put("TA", new String[]{"Teleton", "7857"});
    aMap.put("TM", new String[]{"Teleton", "950010"});
    ORGANIZATION_INFO = Collections.unmodifiableMap(aMap);
  }
}
