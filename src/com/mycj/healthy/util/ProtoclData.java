package com.mycj.healthy.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mycj.healthy.entity.HeartRateData;
import com.mycj.healthy.entity.HistoryData;
import com.mycj.healthy.entity.SportData;
import com.mycj.healthy.service.LiteBlueService;

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

	
	// <----------------------------------write start------------------------------------>
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
		int month = c.get(Calendar.MONTH) + 1;
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
		
		String yearStr = toHexStringForUpdateTime(year);
	
		
		String yearHighStr = yearStr.substring(0, 2);
		String yearLowStr = yearStr.substring(2, 4);
		Log.v("", "yearStr : " + yearStr);
		Log.v("", "yearHighStr : " + yearHighStr);
		Log.v("", "yearLowStr : " + yearLowStr);
		
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
		String timeStr = DataUtil.toHexString(60);
		sb.append(timeStr);
		Log.v("", "提醒协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 同步所有数据
	 * 
	 * @return
	 */
	public static byte[] toByteUpdateAllProtocl() {
		StringBuffer sb = new StringBuffer();
		sb.append(PROTOCL_UPDATA);
		sb.append("11");
		Log.v("", "同步所有协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}

	/**
	 * 同步当天数据
	 * @return
	 */
	public static byte[] toByteUpdateTodayProtocl() {
		StringBuffer sb = new StringBuffer();
		sb.append(PROTOCL_UPDATA);
		sb.append("10");
		Log.v("", "同步今天协议 : " + sb.toString());
		return DataUtil.toBytesByString(sb.toString());
	}


	/**
	 * App同步设置到手表
	 * 
	 */
	public static boolean syncSetting(final LiteBlueService service ,Context context) {
		 final List<byte[]> byteList;
		final long start = System.currentTimeMillis();
		Log.v("LiteBlueServiceTest", "_______________________同步设置开始。");
		int step = (int) SharedPreferenceUtil.get(context, Constant.SHARE_STEP_GOAL, 500);
		int model = (boolean) SharedPreferenceUtil.get(context, Constant.SHARE_STEP_ON_OFF, false) ? 1 : 0;
		int hour = (int) SharedPreferenceUtil.get(context, Constant.SHARE_CLOCK_HOUR, 12);
		int minute = (int) SharedPreferenceUtil.get(context, Constant.SHARE_CLOCK_MIN, 00);
		int open = (boolean) SharedPreferenceUtil.get(context, Constant.SHARE_CLOCK_ON_OFF, false) ? 0 : 1;
		int sleepStartHour = (int) SharedPreferenceUtil.get(context, Constant.SHARE_SLEEP_START_HOUR, 22);
		int sleepStartMin = (int) SharedPreferenceUtil.get(context, Constant.SHARE_SLEEP_START_MIN, 00);
		int sleepEndHour = (int) SharedPreferenceUtil.get(context, Constant.SHARE_SLEEP_END_HOUR, 8);
		int sleepEndMin = (int) SharedPreferenceUtil.get(context, Constant.SHARE_SLEEP_END_MIN, 00);
		int middleStartHour = (int) SharedPreferenceUtil.get(context, Constant.SHARE_MIDDLE_SLEEP_START_HOUR, 12);
		int middleStartMin = (int) SharedPreferenceUtil.get(context, Constant.SHARE_MIDDLE_SLEEP_START_MIN, 00);
		int middleEndHour = (int) SharedPreferenceUtil.get(context, Constant.SHARE_MIDDLE_SLEEP_END_HOUR, 14);
		int middleEndMin = (int) SharedPreferenceUtil.get(context, Constant.SHARE_MIDDLE_SLEEP_END_MIN, 00);
		int[] sleep = new int[] { sleepStartHour, sleepStartMin, sleepEndHour, sleepEndMin, middleStartHour, middleStartMin, middleEndHour, middleEndMin };
		int max = (int) SharedPreferenceUtil.get(context, Constant.SHARE_HEART_RATE_MAX, 100);
		int openForAutoHr = (boolean) SharedPreferenceUtil.get(context, Constant.SHARE_AUTO_HEART_RATE_ON_OFF, false) ? 1 : 0;
		Log.v("ProtoclData", "--最大目标步数 : " + step + "是否开启 : " + model);
		Log.v("ProtoclData", "--闹钟 : " + hour + ":" + minute + ",是否开启 : " + open);
		Log.v("ProtoclData", "--睡眠开始时间 : " + sleepStartHour + ":" + sleepStartMin + "睡眠结束时间 : " + sleepEndHour + ":" + sleepEndMin);
		Log.v("ProtoclData", "--午睡开始时间 : " + middleStartHour + ":" + middleStartMin + "午睡结束时间 : " + middleEndHour + ":" + middleEndMin);
		Log.v("ProtoclData", "--最大心率设置 : " + max);
		Log.v("ProtoclData", "--自动心率设置 : " + openForAutoHr);
		byteList = new ArrayList<>();
		byteList.add(ProtoclData.toByteForDateProtocl(new Date(), context));
		byteList.add(ProtoclData.toByteForStepProtocl(step, model));
		byteList.add(ProtoclData.toByteForClockProtocl(hour, minute, open));
		byteList.add(ProtoclData.toByteForSleepProtocl(sleep));
		byteList.add(ProtoclData.toByteForHeartRateMaxProtocl(max));
		byteList.add(ProtoclData.toByteForAutoHeartRateProtocl(openForAutoHr));
		byteList.add(ProtoclData.toByteForPhoneMessageIncomingProtocl(MessageUtil.readMissCall(context),
				MessageUtil.getNewSmsCount(context) + MessageUtil.getNewMmsCount(context)));

		
		ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
		for (int i = 0; i < byteList.size(); i++) {
			final int index = i;
//			try {
//				Thread.sleep(index * 500);
//			} catch (InterruptedException e) {
//				Log.e("", "_____________________i："+i);
//				e.printStackTrace();
//			}
			service.writeValueToDevice(byteList.get(index));
			
//			Log.e("", "_____________________i："+i);
//			singleThreadExecutor.execute(new Runnable() {
//				@Override
//				public void run() {
//					System.out.println(index);
//					service.writeCharacticsUseConnectListener(byteList.get(index));
//				}
//			});
		}
		final long end = System.currentTimeMillis();
		Log.v("LiteBlueServiceTest", "_______________________同步设置结束。耗时 ："+(end - start) +"ms");
		return true;
	}
		
	// <----------------------------------write end------------------------------------>
	
	// <----------------------------------notify start------------------------------------>

	/**
	 * 弃用 
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
	 * 得到历史数据
	 * 
	 * @param data
	 * @return
	 */
	public static HistoryData parserProtoclDataForHistory(byte[] data) {
		// 得到16进制字符串
				String hexString = DataUtil.getStringByBytes(data);
		Log.v("ProtoclData", "解析历史数据"+hexString);
		// 判断长度
		if (hexString.length() != 24) {
			Log.e("ProtoclData", "协议长度错误：" + hexString);
			return null;
		}
		String protoclStr = hexString.substring(0, 2);
		if (!protoclStr.equals(PROTOCL_UPDATA)) {
			Log.e("ProtoclData", "错误的协议：" + hexString);
			return null;
		}
		// 分割数据
		String dateStr = hexString.substring(2, 4);
		String sleepStr = hexString.substring(4, 8);
		String middleStr = hexString.substring(8, 12);
		String restStr = hexString.substring(12, 16);
		String stepStr = hexString.substring(16, 24);
		// 解析
		String date = getDate(dateStr);
		int sleep = Integer.valueOf(sleepStr, 16);// 单位分钟
		int middle = Integer.valueOf(middleStr, 16);// 单位分钟
		int rest = Integer.valueOf(restStr, 16);// 单位分钟
		int step = Integer.valueOf(stepStr, 16);// 单位分钟
		// 数据 --> 类
		HistoryData historyData = new HistoryData(date, sleep, middle, rest, step);
		return historyData;
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

	/**
	 * 实时步数
	 * 
	 * @param data
	 * @return
	 */
	public static SportData parserDataForStep(byte[] data) {
		String hexString = DataUtil.getStringByBytes(data);
		Log.v("ProtoclData", "-----------------hexString----------------" + hexString);
		if (hexString.length() != 20) {
			Log.e("ProtoclData", "协议长度错误：" + hexString);
			return null;
		}
		if (!hexString.substring(0, 2).equals("23")) {
			Log.e("ProtoclData", "错误的协议：" + hexString);
			return null;
		}

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

		byte[] stepValueData = new byte[] { data[6], data[7], data[8], data[9] };
		int stepValue = actualSteps(stepValueData);

		SportData sportData = new SportData();
		sportData.setStep(stepValue);

		return sportData;
	}
	
	/**
	 * 最大目标步数
	 * @param data
	 * @return
	 */
	public static int[] parserDataForGoalStep(byte[] data) {
		String hexString = DataUtil.getStringByBytes(data);
		Log.v("ProtoclData", "-----------------hexString----------------" + hexString);
		if (hexString.length() != 12) {
			Log.e("ProtoclData", "协议长度错误：" + hexString);
			return null;
		}
		if (!hexString.substring(0, 2).equals("17")) {
			Log.e("ProtoclData", "错误的协议：" + hexString);
			return null;
		}
		int model = Integer.valueOf(hexString.substring(2,4),16);
		byte[] stepValueData = new byte[] { data[2], data[3], data[4], data[5] };
		int goalStep = actualSteps(stepValueData);
		return new int[]{model,goalStep};
	}
	/**
	 * 最大心率
	 * @param data
	 * @return
	 */
	public static int parserDataForMaxHeartRate(byte[] data) {
		String hexString = DataUtil.getStringByBytes(data);
		Log.v("ProtoclData", "-----------------hexString----------------" + hexString);
		if (hexString.length() != 4) {
			Log.e("ProtoclData", "协议长度错误：" + hexString);
			return 0;
		}
		if (!hexString.substring(0, 2).equals("18")) {
			Log.e("ProtoclData", "错误的协议：" + hexString);
			return 0;
		}
		int maxHeartRate = Integer.valueOf(hexString.substring(2,4),16);
		return maxHeartRate;
	}
	
	/**
	 * 实时心跳
	 * 
	 * @param data
	 * @return
	 */
	public static HeartRateData parserDataForHeartRate(byte[] data) {
		String hexString = DataUtil.getStringByBytes(data);
		Log.v("ProtoclData", "-----------------hexString----------------" + hexString);
		if (hexString.length() != 14) {
			Log.e("ProtoclData", "协议长度错误：" + hexString);
			return null;
		}
		if (!hexString.substring(0, 2).equals("25")) {
			Log.e("ProtoclData", "错误的协议：" + hexString);
			return null;
		}
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
		int heartRateValue = Integer.valueOf(heartRate, 16);
		String dateStr  = TimeUtil.dateToString(new Date());
		HeartRateData mHeartRateData = new HeartRateData(dateStr, heartRateValue);
		return mHeartRateData;
	}

	// <----------------------------------notify------------------------------------>

	private static int actualSteps(byte[] data) {
		if (data == null || data.length != 4) {
			return 0;
		}
		;
	
		byte[] temp = new byte[4];
		for (int i = 0; i < data.length; i++) {
			temp[3 - i] = data[i];
		}
	
		String tempStr = DataUtil.getStringByBytes(temp);
	
		return DataUtil.hexToInt(tempStr);
	}

	private static String getDate(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateString;
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		switch (Integer.valueOf(dateStr,16)) {
		case 0x10:
			// 今天
			break;
		case 0x11:
			c.add(Calendar.DATE, -1);// 昨天
			date = c.getTime();
			break;
		case 0x12:
			c.add(Calendar.DATE, -2);// 前天
			date = c.getTime();
			break;
		case 0x13:
			c.add(Calendar.DATE, -3);// 大前天
			date = c.getTime();
			break;
		case 0x14:
			c.add(Calendar.DATE, -4);
			date = c.getTime();
			break;
		case 0x15:
			c.add(Calendar.DATE, -5);
			date = c.getTime();
			break;
		case 0x16:
			c.add(Calendar.DATE, -6);
			date = c.getTime();
			break;
	
		default:
			break;
		}
		Log.e("", "_________________date : " + date);
		return sdf.format(date);
	}

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
	/**
	 * 计步模式 字符串 8位16进制数
	 * 
	 * @param value
	 * @return
	 */
	private static String toHexStringForUpdateTime(int value) {
		String result = Integer.toHexString(value);
		if (result.length() == 1) {
			result = "000" + result;
		} else if (result.length() == 2) {
			result = "00" + result;
		} else if (result.length() == 3) {
			result = "0" + result;
		} 
		return result;
	}

}
