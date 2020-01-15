package shared.datahelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle strings and json objects like conversion methods, hash generator, etc.
 */
public class DataHelper {
	/**
	 * Convert a map to json object
	 * @param mapData input map type <String, Object>
	 * @return json as String
	 */
	public static String convertMapToJsonString(Map<String, Object> mapData) {
		Gson gson = new Gson();
		return gson.toJson(mapData);
	}

	/**
	 * Generate checksum using md5
	 * @param secretKey api key
	 * @param dataString input data
	 * @return hashed md5 
	 */
	public static String generateChecksum(String secretKey, String dataString) {
		String concatenatedString = secretKey + dataString;
		String hashedString = md5Hash(concatenatedString);
		return md5Hash(hashedString + dataString);
	}

	/**
	 * md5 hashing function
	 * @param rawString
	 * @return md5 hashed string
	 */
	private static String md5Hash(String rawString) {
		String hashedString = "";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(rawString.getBytes());
			byte[] bytes = messageDigest.digest();
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				stringBuilder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			hashedString = stringBuilder.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hashedString;
	}

	/**
	 * Convert json string to java map object
	 * @param json
	 * @return
	 * @throws IOException
	 */
		public static Map<String, Object> convertJsonStringToMapObject(JsonObject json) throws IOException {
		String jsonString = json.toString();
		return new ObjectMapper().readValue(jsonString, HashMap.class);
	}

	/**
	 *
	 * @param dateTimeString input should be yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date parseDateTimeString(String dateTimeString) {
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateTime = null;
		try {
			dateTime = dateTimeFormatter.parse(dateTimeString);
		} catch (ParseException e) {
			System.err.println("Wrong format input");
			e.printStackTrace();
		}
		return dateTime;
	}

	/**
	 * Check if the input date is within the range between current date and the date of 62 days before
	 * @param inputDate
	 * @return true if it is in the range, false otherwise
	 */
	public static boolean isDateWithin62DaysUntilToday(Date inputDate) {
		Date currentDate = Calendar.getInstance().getTime();
		Calendar calendarFrom62DaysBefore = Calendar.getInstance();
		calendarFrom62DaysBefore.add(Calendar.DAY_OF_MONTH, -62);
		calendarFrom62DaysBefore.set(Calendar.HOUR_OF_DAY, 0);
		calendarFrom62DaysBefore.set(Calendar.MINUTE, 0);
		calendarFrom62DaysBefore.set(Calendar.SECOND, 0);
		calendarFrom62DaysBefore.set(Calendar.MILLISECOND, 0);
		Date dateFrom62DaysBefore = calendarFrom62DaysBefore.getTime();
		return (inputDate.compareTo(dateFrom62DaysBefore) >= 0 && inputDate.compareTo(currentDate) <= 0);
	}

	public static String convertDateToUnixTimeStampString(Date date) {
		long unixTimeStamp = date.getTime()/1000;
		return String.valueOf(unixTimeStamp);
	}

	/**
	 * https://www.baeldung.com/sha-256-hashing-java
	 * @param rawString
	 * @return hash 256
	 */
	public static String sha256Hash(String rawString) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] encodedHash = digest.digest(
				rawString.getBytes(StandardCharsets.UTF_8));
		return bytesToHex(encodedHash);
	}

	private static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if(hex.length() == 1) hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Remove all leading zeros, all special characters and add prefix 84 for the phone number
	 * @param phoneNumber mobile phone number in string format, i.e +84945xxxx
	 * @return formatted phone number as description
	 */
	public static String formatMobileNumber(String phoneNumber) {
		return "84" + phoneNumber
			.replaceFirst("^0+(?!$)", "")
			.replaceAll("[^0-9]", "");
	}
}
