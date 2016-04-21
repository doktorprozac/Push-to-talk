package com.example.push_to_talk1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

	private Utils() {
	}

	public static final int SERVERPORT = 55984;
	public static final Pattern IP_ADDRESS_PATTERN = Pattern
			.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
					+ "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
					+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
					+ "|[1-9][0-9]|[0-9]))");

	public static  boolean validateIp(String ipAddress)
	{
		if(ipAddress == null)
			return false;
		
		Matcher matcher = IP_ADDRESS_PATTERN.matcher(ipAddress);
		return matcher.matches();
	}

}
