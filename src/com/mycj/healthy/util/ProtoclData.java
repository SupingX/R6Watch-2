package com.mycj.healthy.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class ProtoclData {

	/** 操作码 **/
	public final static String PROTOCL_SYNC_TIME = "11";
	public final static String PROTOCL_STEP = "17";
	public final static String PROTOCL_PHONE_MESSAGE_INCOMING = "07";
	public final static String PROTOCL_CLOCK = "13";
	public final static String PROTOCL_SLEEP = "15";
	public final static String PROTOCL_HEART_RATE_MAX = "18";
	public final static String PROTOCL_ATUO_HEART_RATE = "19";
	public final static String PROTOCL_UPDATA = "05";

	/**
	 * 
	 * 同步日期的协议 byte[]
	 * 
	 * @param date
	 *            当前时间
	 * @param context
	 *            当前上下文 可获取手机当前设置的时间格式24小时/12小时制
	 * @return
	 */
	public static byte[] toByteForDateProtocl(Date date, Context context) {
		StringBuffer sb = new StringBuffer();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = 0;
		int type = getTimeFormat(context);
		String typeStr;
		if (type == 0) {
			typeStr = "00";
			hour = c.get(Calendar.HOUR_OF_DAY);// 24小时制
		} else {
			hour = c.get(Calendar.HOUR);// 12小时制
			typeStr = "01";
		}
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);

		String yearHighStr = getYearHightHexString(year);
		String yearLowStr = getYearLowHexString(year);
		String monthStr = DataUtil.toHexString(month);
		String dayStr = DataUtil.toHexString(day);
		String hourStr = DataUtil.toHexString(hour);
		String minuteStr = DataUtil.toHexString(minute);
		String secondStr = DataUtil.toHexString(second);

		Log.v("DataUtilForProject", year + "-" + month + "-" + day + "  " + hour + "/" + minute + "/" + second);

		sb.append(PROTOCL_SYNC_TIME);
		sb.append(typeStr);
		sb.append(yearHighStr);
		sb.append(yearLowStr);
		sb.append(monthStr);
		sb.append(dayStr);
		sb.append(hourStr);
		sb.append(minuteStr);
		sb.append(secondStr);
		Log.v("", "同步日期协议 : " + sb.toString());

		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 
	 * 目标步数的协议 byte[]
	 * 
	 * @param int step 步数
	 * @param type
	 *            0:随意模式 1:目标模式
	 * @return
	 */
	public static byte[] toByteForStepProtocl(int step, int model) {
		StringBuffer sb = new StringBuffer();

		String modle = "";
		if (model == 0) {
			modle = "00";// 随意模式
		} else {
			modle = "01";// 目标模式
		}
		String result = toHexStringForStep(step);

		sb.append(PROTOCL_STEP);
		sb.append(modle);
		sb.append(result);
		Log.v("", "目标步数协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 提醒
	 * 
	 * @param phone
	 *            来电个数
	 * @param message
	 *            新信息个数
	 * @return
	 */
	public static byte[] toByteForPhoneMessageIncomingProtocl(int phone, int message) {
		StringBuffer sb = new StringBuffer();
		String phoneStr = DataUtil.toHexString(phone);
		String msgStr = DataUtil.toHexString(message);
		sb.append(PROTOCL_PHONE_MESSAGE_INCOMING);
		sb.append(phoneStr);
		sb.append(msgStr);
		Log.v("", "提醒协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 闹钟设置
	 * 
	 * @param hour
	 * @param minute
	 * @param open
	 *            1:开 0：关
	 * @return
	 */
	public static byte[] toByteForClockProtocl(int hour, int minute, int open) {
		StringBuffer sb = new StringBuffer();
		String hourStr = DataUtil.toHexString(hour);
		String minuteStr = DataUtil.toHexString(minute);
		String openStr = open == 0 ? "01" : "00";

		sb.append(PROTOCL_CLOCK);
		sb.append(openStr);
		sb.append(hourStr);
		sb.append(minuteStr);

		Log.v("", "提醒协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 睡眠时间设置
	 * 
	 * @param clock
	 *            clock[0]~clock[7] 分别为： 开始午休时间：时；开始午休时间：分； 结束午休时间：时；结束午休时间：分；
	 *            开始睡眠时间：时；开始睡眠时间：分； 结束睡眠时间：时；结束睡眠时间：分；
	 * @return
	 */
	public static byte[] toByteForSleepProtocl(int[] sleep) {
		StringBuffer sb = new StringBuffer();
		sb.append(PROTOCL_SLEEP);
		for (int i = 0; i < sleep.length; i++) {
			String time = DataUtil.toHexString(sleep[i]);
			sb.append(time);
		}
		Log.v("", "提醒协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 最大心率设置
	 * 
	 * @param max
	 *            最大心率值
	 * @return
	 */
	public static byte[] toByteForHeartRateMaxProtocl(int max) {
		StringBuffer sb = new StringBuffer();
		String maxStr = DataUtil.toHexString(max);
		sb.append(PROTOCL_HEART_RATE_MAX);
		sb.append(maxStr);
		Log.v("", "提醒协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 自动心率测试设置
	 * 
	 * @param offset
	 *            时间间隔
	 * @param open
	 *            01:开 00：关
	 * @return
	 */
	public static byte[] toByteForAutoHeartRateProtocl(int open) {
		StringBuffer sb = new StringBuffer();
		sb.append(PROTOCL_ATUO_HEART_RATE);
		String openStr = DataUtil.toHexString(open);
		sb.append(openStr);
		String timeStr = "01";
		sb.append(timeStr);
		Log.v("", "提醒协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 同步数据
	 * 
	 * @param offset
	 * @return
	 */
	public static byte[] toByteForAutoHeartRateProtocl() {
		StringBuffer sb = new StringBuffer();
		sb.append(PROTOCL_UPDATA);

		// 未完成

		Log.v("", "gengxin协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	// <----------------------------------notify------------------------------------>
	// //

	/**
	 * 解析为睡眠数据
	 * 
	 * @param data
	 * @return
	 */
	public static HashMap<String, Integer> parserDataForSleep(byte[] data) {
		HashMap<String, Integer> sleepData = null;
		String hexString = DataUtil.getStringByBytes(data);
		if (hexString.substring(0, 2).equals("05")) {
			if (hexString.length() == 16 && hexString.substring(2, 4).equals("10")) { // 睡眠数据
				String hexSleep = hexString.substring(2, hexString.length());
				Log.v("ProtoclData", "睡眠数据 ：" + hexSleep);
				String sleep = hexSleep.substring(2, 6);
				String middle = hexSleep.substring(6, 10);
				String rest = hexSleep.substring(10, 14);

				int sleepValue = DataUtil.hexStringToInt(sleep);
				int middleValue = DataUtil.hexStringToInt(middle);
				int restValue = DataUtil.hexStringToInt(rest);
				Log.v("ProtoclData", "---------------------------------");
				Log.v("ProtoclData", "睡眠数据 ：" + hexString);
				Log.v("ProtoclData", "sleepValue ：" + sleepValue);
				Log.v("ProtoclData", "middleValue ：" + middleValue);
				Log.v("ProtoclData", "restValue ：" + restValue);
				Log.v("ProtoclData", "---------------------------------");

				sleepData = new HashMap<>();
				sleepData.put("sleep", sleepValue);
				sleepData.put("middle", middleValue);
				sleepData.put("rest", restValue);
			} else {
				Log.e("ProtoclData", "没有睡眠数据");
			}
		}

		return sleepData;
	}

	/**
	 * 解析为运动数据
	 * 
	 * @param data
	 * @return
	 */
	public static HashMap<String, Integer> parserDataForSport(byte[] data) {
		HashMap<String, Integer> sportData = null;
		String hexString = DataUtil.getStringByBytes(data);
		if (hexString.substring(0, 2).equals("05")) {
			if (hexString.length() == 22 && hexString.substring(2, 4).equals("20")) { // 运动数据
				String hexSport = hexString.substring(2, hexString.length());
				Log.v("ProtoclData", "运动数据 ：" + hexSport);
				String month = hexSport.substring(2, 4);
				String day = hexSport.substring(4, 6);
				String hour = hexSport.substring(6, 8);
				String minute = hexSport.substring(8, 10);
				String second = hexSport.substring(10, 12);
				String step = hexSport.substring(12, 20);

				int monthValue = DataUtil.hexStringToInt(month);
				int dayValue = DataUtil.hexStringToInt(day);
				int hourValue = DataUtil.hexStringToInt(hour);
				int minuteValue = DataUtil.hexStringToInt(minute);
				int secondValue = DataUtil.hexStringToInt(second);
				int stepValue = DataUtil.hexStringToInt(step);
				Log.v("ProtoclData", "---------------------------------");
				Log.v("ProtoclData", "运动数据 ：" + hexString);
				Log.v("ProtoclData", "stepValue ：" + stepValue);
				Log.v("ProtoclData", "---------------------------------");
				sportData = new HashMap<>();
				sportData.put("mouth", monthValue);
				sportData.put("day", dayValue);
				sportData.put("hour", hourValue);
				sportData.put("minute", minuteValue);
				sportData.put("second", secondValue);
				sportData.put("step", stepValue);

			} else {
				Log.e("ProtoclData", "没有运动数据");
			}
		}
		return sportData;
	}

	/**
	 * 来电提醒/短信提醒
	 * 
	 * @param data
	 * @return
	 */
	public static HashMap<String, Integer> parserDataForIncoming(byte[] data) {
		HashMap<String, Integer> incomingData = null;
		String hexString = DataUtil.getStringByBytes(data);
		if (hexString.substring(0, 2).equals("07") && hexString.length() == 6) {
			String phone = hexString.substring(2, 4);
			String msg = hexString.substring(4, 6);
			int phoneValue = DataUtil.hexStringToInt(phone);
			int msgValue = DataUtil.hexStringToInt(msg);

			incomingData = new HashMap<>();
			incomingData.put("phone", phoneValue);
			incomingData.put("msg", msgValue);
		} else {
			Log.e("ProtoclData", "没有来电&信息提醒数据");
		}

		return incomingData;
	}

	/**
	 * 时间同步
	 * 
	 * @param data
	 * @return
	 */
	public static int parserDataForTimeSync(byte[] data) {
		String hexString = DataUtil.getStringByBytes(data);
		if (hexString.substring(0, 2).equals("11") && hexString.length() == 2) {
			return 1;
		} else {
			Log.e("ProtoclData", "没有同步时间提醒数据");
		}

		return 0;
	}
	
	public static int actualSteps(byte[] data) {
		if (data == null || data.length < 4) {
			return 0;			
		};
		
		byte[] temp = new byte[4];
		for (int i = 0; i < data.length; i++) {
			temp[3 - i] = data[i];
		}
		
		String tempStr = DataUtil.getStringByBytes(temp);
		
		return DataUtil.hexToInt(tempStr);
	}

	/**
	 * 实时步数
	 * 
	 * @param data
	 * @return
	 */
	public static HashMap<String, Integer> parserDataForStep(byte[] data) {
		HashMap<String, Integer> sportData = null;
		String hexString = DataUtil.getStringByBytes(data);
		Log.v("ProtoclData", "-----------------hexString----------------" + hexString);
		if (hexString.substring(0, 2).equals("23") && hexString.length() == 20) {

			String month = hexString.substring(2, 4);
			String day = hexString.substring(4, 6);
			String hour = hexString.substring(6, 8);
			String minute = hexString.substring(8, 10);
			String second = hexString.substring(10, 12);

			int monthValue = Integer.parseInt(month, 16);
			int dayValue = Integer.parseInt(day, 16);
			int hourValue = Integer.parseInt(hour, 16);
			int minuteValue = Integer.parseInt(minute, 16);
			int secondValue = Integer.parseInt(second, 16);
			byte [] stepValueData  = new byte[]{data[6],data[7],data[8],data[9]};
			int stepValue = actualSteps(stepValueData);

			// byte [] value = new byte[]{data[6],data[7],data[8],data[9]};

			// int dayValue = DataUtil.hexStringToInt(day);
			// int hourValue = DataUtil.hexStringToInt(hour);
			// int minuteValue = DataUtil.hexStringToInt(minute);
			// int secondValue = DataUtil.hexStringToInt(second);
			// int stepValue = DataUtil.hexStringToInt(step);
			Log.v("ProtoclData", "---------------------------------");
			Log.v("ProtoclData", "实时步数数据 ：" + hexString);
			Log.v("ProtoclData", "stepValue ：" + stepValue);
			Log.v("ProtoclData", "---------------------------------");

			sportData = new HashMap<>();
			sportData.put("mouth", monthValue);
			sportData.put("day", dayValue);
			sportData.put("hour", hourValue);
			sportData.put("minute", minuteValue);
			sportData.put("second", secondValue);
			sportData.put("step", stepValue);

		} else {
			Log.e("ProtoclData", "没有实时步数数据");
		}
		return sportData;
	}

	/**
	 * 实时心跳
	 * 
	 * @param data
	 * @return
	 */
	public static HashMap<String, Integer> parserDataForHeartRate(byte[] data) {
		HashMap<String, Integer> heartRateData = null;
		String hexString = DataUtil.getStringByBytes(data);
		// Log.v("ProtoclData", "hexString.length : "+ hexString.length());
		// Log.v("ProtoclData", "hsubstring(0, 2) : "+ hexString.substring(0,
		// 2));
		if (hexString.substring(0, 2).equals("25") && hexString.length() == 14) {
			String month = hexString.substring(2, 4);
			String day = hexString.substring(4, 6);
			String hour = hexString.substring(6, 8);
			String minute = hexString.substring(8, 10);
			String second = hexString.substring(10, 12);
			String heartRate = hexString.substring(12, 14);

			int monthValue = DataUtil.hexStringToInt(month);
			int dayValue = DataUtil.hexStringToInt(day);
			int hourValue = DataUtil.hexStringToInt(hour);
			int minuteValue = DataUtil.hexStringToInt(minute);
			int secondValue = DataUtil.hexStringToInt(second);
			int heartRateValue = DataUtil.hexStringToInt(heartRate);

			Log.v("ProtoclData", "---------------------------------");
			Log.v("ProtoclData", "实时心跳数据 ：" + hexString);
			Log.v("ProtoclData", "stepValue ：" + heartRateValue);
			Log.v("ProtoclData", "---------------------------------");

			heartRateData = new HashMap<>();
			heartRateData.put("mouth", monthValue);
			heartRateData.put("day", dayValue);
			heartRateData.put("hour", hourValue);
			heartRateData.put("minute", minuteValue);
			heartRateData.put("second", secondValue);
			heartRateData.put("heartRate", heartRateValue);

		} else {
			Log.e("ProtoclData", "没有实时心跳数据");
		}
		return heartRateData;
	}

	// <----------------------------------notify------------------------------------>
	// //

	private static String getYearHightHexString(int year) {
		String yearStr = String.valueOf(year);
		return DataUtil.toHexStringByString(yearStr.substring(0, 2));
	}

	private static String getYearLowHexString(int year) {
		String yearStr = String.valueOf(year);
		return DataUtil.toHexStringByString(yearStr.substring(2, 4));
	}

	/**
	 * 获取当前手机的时间格式
	 * 
	 * @param context
	 * @return 24小时制/12小时制
	 */
	private static int getTimeFormat(Context context) {
		String timeFormat = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.TIME_12_24);
		if (timeFormat.equals("24")) {
			return 0;
		} else if (timeFormat.equals("12")) {
			return 1;
		}
		return -1;
	}

	/**
	 * 计步模式 字符串 8位16进制数
	 * 
	 * @param value
	 * @return
	 */
	private static String toHexStringForStep(int value) {
		String result = Integer.toHexString(value);
		if (result.length() == 1) {
			result = "0000000" + result;
		} else if (result.length() == 2) {
			result = "000000" + result;
		} else if (result.length() == 3) {
			result = "00000" + result;
		} else if (result.length() == 4) {
			result = "0000" + result;
		} else if (result.length() == 5) {
			result = "000" + result;
		} else if (result.length() == 6) {
			result = "00" + result;
		} else if (result.length() == 7) {
			result = "0" + result;
		}
		return result;
	}

}
