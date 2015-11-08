//@@ Yu Ting
package date;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.Parser;

import type.MonthType;

public class DateTime {
	private static final Parser dateParser = new Parser();
	private static final int _flags = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
	private static final int DAY = 0;
	private static final int MONTH = 1;
	private static final int YEAR = 2;
	private static final int DATE_SIZE = 3;
	private static final int DAY_MONTH_SIZE = 2;
	private static final int DAY_MONTH_YEAR_SIZE = 3;
	private static final HashMap<MonthType, Integer> months = new HashMap<MonthType, Integer>();
	private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	static {
		months.clear();
		
		months.put(MonthType.JAN, 1);
		months.put(MonthType.JANUARY, 1);
		
		months.put(MonthType.FEB, 2);
		months.put(MonthType.FEBRUARY, 2);
		
		months.put(MonthType.MAR, 3);
		months.put(MonthType.MARCH, 3);
		
		months.put(MonthType.APR, 4);
		months.put(MonthType.APRIL, 4);
		
		months.put(MonthType.MAY, 5);
		
		months.put(MonthType.JUN, 6);
		months.put(MonthType.JUNE, 6);
		
		months.put(MonthType.JUL, 7);
		months.put(MonthType.JULY, 7);
		
		months.put(MonthType.AUG, 8);
		months.put(MonthType.AUGUST, 8);
		
		months.put(MonthType.SEP, 9);
		months.put(MonthType.SEPTEMBER, 9);
		
		months.put(MonthType.OCT, 10);
		months.put(MonthType.OCTOBER, 10);
		
		months.put(MonthType.NOV, 11);
		months.put(MonthType.NOVEMBER, 11);
		
		months.put(MonthType.DEC, 12);
		months.put(MonthType.DECEMBER, 12);
	}
	
	/**
	 * Combine the date and time into a standard date format.
	 * @param date 
	 * @param time
	 * @return standard date format
	 */
	public static Date combineDateTime(Date date, Date time) {
		if (date != null && time != null) {
			Calendar calendar = Calendar.getInstance();
			Calendar dateCalendar = Calendar.getInstance();
			Calendar timeCalendar = Calendar.getInstance();

			calendar.clear();
			dateCalendar.setTime(date);
			timeCalendar.setTime(time);

			calendar.set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR));
			calendar.set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH));
			calendar.set(Calendar.DATE, dateCalendar.get(Calendar.DATE));
			calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));

			return calendar.getTime();
		}
		return null;
	}
	
	public static Date getOneWeekLater(Date date) {
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			calendar.add(Calendar.DAY_OF_MONTH, 7);
			date = calendar.getTime();
		}
		return date;
	}
	
	public static Date getOneYearLater(Date date) {
		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			
			calendar.add(Calendar.YEAR, 1);
			date = calendar.getTime();
		}
		return date;
	}
	
	public static Date getToday() {
		Calendar todayCalendar = Calendar.getInstance();
		
		// Initialize today
		todayCalendar.set(Calendar.HOUR_OF_DAY, 23);
		todayCalendar.set(Calendar.MINUTE, 59);
		todayCalendar.set(Calendar.SECOND, 59);
		
		return todayCalendar.getTime();
	}

	public static Date parse(String input) {
		Date date = null;
		try {
			String dateStr = getAmericanDate(input);
			String timeStr = getTime(input);
			
			if (dateStr != null) {
				if (timeStr != null) {
					dateStr += " " + timeStr;
				}
				date = dateParser.parse(dateStr).get(0).getDates().get(0);
			} else {
				date = dateParser.parse(input).get(0).getDates().get(0);
			}

		} catch (Exception e) {
			logger.log(Level.INFO, "Exception was thrown when parsing date from input.", e);
			return null;
		}
		return date;
	}

	public static String getTime(String input) {
		String regexAmPm = ".*?([0-9]{1,2})\\s?(am|pm).*";
		String regexAmPm2 = ".*?([0-9]{1,2})[:.]([0-9]{1,2})\\s?(am|pm).*";
		String regexColon = ".*?([0-9]{1,2})[:.]([0-9]{1,2}).*";
		
		Pattern pattern = Pattern.compile(regexAmPm, _flags);
		Matcher matcher = pattern.matcher(input);
		
		if (matcher.matches()) {
			if(isValidTime(matcher.group(1), null)) {
				return matcher.group(1) + matcher.group(2);
			}
		}
		
		pattern = Pattern.compile(regexAmPm2);
		matcher = pattern.matcher(input);
		
		if (matcher.matches()) {
			if(isValidTime(matcher.group(1), matcher.group(2))) {
				return matcher.group(1) + ":" + matcher.group(2) + matcher.group(3);
			}
		}
		
		pattern = Pattern.compile(regexColon);
		matcher = pattern.matcher(input);
		
		if (matcher.matches()) {
			if(isValidTime(matcher.group(1), matcher.group(2))) {
				return matcher.group(1) + ":" + matcher.group(2);
			}
		}
		return null;
	}
	
	private static boolean isValidTime(String hourStr, String minStr) {
		try {
			int hour = Integer.parseInt(hourStr);
			
			if (hour > 23) {
				return false;
			}
			
			if (minStr != null) {
				int min = Integer.parseInt(minStr);
				if (min > 59) {
					return false;
				}
			}
			
		} catch (Exception e) {
			logger.log(Level.INFO, "Exception was thrown when determining validity of time.", e);
			return false;
		}
		return true;
	}

	/**
	 * Convert the British date format into American date format, "MMM DD YYYY" or "MMM DD YY".
	 * @param input - possible British date format input
	 * @return standard date format
	 */
	private static String getAmericanDate(String input) {
		String shortMonth = "jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec";
		String longMonth = "january|february|march|april|may|june|"
				+ "july|august|september|october|november|december";
		String month = shortMonth + "|" + longMonth;
		String regexNumbericDM = ".*?([0-9]{1,2})[/-]([0-9]{1,2}).*";
		String regexTextDM1 = ".*?([0-9]{1,2})[ /-](" + month + ")\\s.*";
		String regexTextDM2 = ".*?([0-9]{1,2})[ /-](" + month + ")";
		String regexNumericDMY = ".*?([0-9]{1,2})[/-]([0-9]{1,2})[/-]([0-9]{2,4}).*";
		String regexTextDMY1 = ".*?([0-9]{1,2})[ /-](" + month + ")[ ,/-]\\s?([0-9]{2,4})\\s.*";
		String regexTextDMY2 = ".*?([0-9]{1,2})[ /-](" + month + ")[ ,/-]\\s?([0-9]{2,4})";
				
		String[] twoGroups = {regexNumbericDM, regexTextDM1, regexTextDM2 };
		String[] threeGroups = {regexNumericDMY, regexTextDMY1, regexTextDMY2};
		
		for (int i = 0; i < threeGroups.length; i++) {
			String result = getDateMatcher(input, threeGroups[i], DAY_MONTH_YEAR_SIZE);
			
			if (result != null) {
				return result;
			}
		}
	
		for (int i = 0 ; i< twoGroups.length; i++) {
			String result = getDateMatcher(input, twoGroups[i], DAY_MONTH_SIZE);
			
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param input
	 * @param regex
	 * @param groupNumber
	 * @return
	 */
	private static String getDateMatcher(String input, String regex, int groupNumber) {
		String dateTime = null;
		String[] dayMonthYear = new String[DATE_SIZE];
		Arrays.fill(dayMonthYear, "");

		Pattern pattern = Pattern.compile(regex, _flags);
		Matcher matcher = pattern.matcher(input);

		if (matcher.matches()) {
			dateTime = "";
			for (int i = 0; i < groupNumber; i++) {
				dayMonthYear[i] = matcher.group(i + 1);
			}

			if (groupNumber >= DAY_MONTH_SIZE) {
				String monthStr = dayMonthYear[MONTH].toLowerCase();
				if (!isConvertable(monthStr)) {
					MonthType month = MonthType.toMonth(monthStr);
					if(months.containsKey(month)) {
						dayMonthYear[MONTH] = months.get(month).toString();
					}
				}
				
				dateTime += dayMonthYear[MONTH] + "-";
				dateTime += dayMonthYear[DAY];
			}

			if (groupNumber == DAY_MONTH_YEAR_SIZE) {
				dateTime += "-" + dayMonthYear[YEAR];
			}
		}

		return dateTime;
	}
	
	private static boolean isConvertable(String number) {
		try {
			Integer.parseInt(number);
		} catch (NumberFormatException e) {
			logger.info("Month format was MMM or month.");
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		//System.out.println(isValidTime("23", "59"));
		//System.out.println(parse("at 5 nov 12 12pm"));
		//System.out.println(parse("at 5 dec 15"));
		//System.out.println(parse("at 12 jan"));
		System.out.println(parse("17 March 2016 22:10"));
		//System.out.println(parse("16 Dec 2015"));
		//System.out.println(getTime("4 pm"));
	}
}