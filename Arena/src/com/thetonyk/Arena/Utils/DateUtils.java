package com.thetonyk.Arena.Utils;

public class DateUtils {
	
	public static String toText(long time, boolean withSeconds) {
		
		int duration = (int) (time / 1000l);
		int seconds = duration % 60;
		int minutes = (duration % 3600) / 60;
		int hours = (duration % 86400) / 3600;
		int days = (duration % 2678400) / 86400;
		int months = duration / 2678400;
		
		String text = (months > 0 ? months + " months " : "") + (days > 0 ? days + " days " : "") + (hours > 0 ? hours + "h " : "") + (minutes > 0 ? minutes + "min " : "") + (seconds > 0 && withSeconds ? seconds + "s " : "");
		
		return text.substring(0, text.length() - 1);
		
	}

}
