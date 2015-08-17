package com.mycj.healthy.util;

public class Constant {

	public final static int MAX_STEP = 99990;
	public final static int MAX_HEART_RATE = 200;
	public final static int MIN_HEART_RATE = 40;
	// 本地绑定设备
	public final static String SHARE_CURRENT_DEVICE_NAME = "share_current_device_name";
	public final static String SHARE_CURRENT_DEVICE_ADDRESS = "share_current_device_address";

	// 步数目标设置
	public final static String SHARE_STEP_GOAL = "share_step_goal";
	public final static String SHARE_STEP_ON_OFF = "share_step_on_off";
	// 心率最大值
	public final static String SHARE_HEART_RATE_MAX = "share_heart_rate_max";
	// 手表闹钟
	public final static String SHARE_CLOCK_ON_OFF = "share_clock_on_off";
	public final static String SHARE_CLOCK_HOUR = "share_clock_hour";
	public final static String SHARE_CLOCK_MIN = "share_clock_min";
	// 睡眠时间
	public final static String SHARE_MIDDLE_SLEEP_START_HOUR = "share_middle_sleep_start_hour";
	public final static String SHARE_MIDDLE_SLEEP_START_MIN = "share_middle_sleep_start_min";
	public final static String SHARE_MIDDLE_SLEEP_END_HOUR = "share_middle_sleep_end_hour";
	public final static String SHARE_MIDDLE_SLEEP_END_MIN = "share_middle_sleep_end_min";
	public final static String SHARE_SLEEP_START_HOUR = "share_sleep_start_hour";
	public final static String SHARE_SLEEP_START_MIN = "share_sleep_start_min";
	public final static String SHARE_SLEEP_END_HOUR = "share_sleep_end_hour";
	public final static String SHARE_SLEEP_END_MIN = "share_sleep_end_min";
	// 自动心率测试
	public final static String SHARE_AUTO_HEART_RATE_ON_OFF = "share_auto_heart_rate_on_off";
	public final static String SHARE_BINDING_DEVICE_NAME = "share_binding_device_name";
	public final static String SHARE_BINDING_DEVICE_ADRESS = "share_binding_device_adress";
	// 来电提醒
	public final static String SHARE_INCOMING_ON_OFF = "share_incoming_on_off";
	public final static String SHARE_INCOMING_VIBRATION_ON_OFF = "share_incoming_vibration_on_off";
	public final static String SHARE_INCOMING_SOUND_ON_OFF = "share_incoming_sound_on_off";
	
	//协议 手表-->手机
	public final static int PROTOCL_UPDATE_ALL_NOTIFY = 0x05;
	public final static int PROTOCL_INCOMING_NOTIFY = 0x07;
	public final static int PROTOCL_TIME_SYNC_NOTIFY = 0x11;
	public final static int PROTOCL_CLOCK_NOTIFY = 0x13;
	public final static int PROTOCL_STEP_GOAL_NOTIFY = 0x17;
	public final static int PROTOCL_HEART_RATE_MAX_NOTIFY = 0x19;
	public final static int PROTOCL_HEART_RATE_MAX_NOTIFY_TO_PHONE = 0x18;
	public final static int PROTOCL_CAMERA_NOTIFY = 0x1A;
	public final static int PROTOCL_REMIND_NOTIFY = 0x1C;
	public final static int PROTOCL_ELECTRIC_NOTIFY = 0x21;
	public final static int PROTOCL_STEP_NOTIFY = 0x23;
	public final static int PROTOCL_HEART_RATE_NOTIFY = 0x25;
	
	//协议 手机-->手表
//	public final static int PROTOCL_SLEEP_NOTIFY = 0x05;
//	public final static int PROTOCL_INCOMING_WRITE = 0x7;
//	public final static int PROTOCL_TIME_SYNC_NOTIFY = 0x11;
//	public final static int PROTOCL_CLOCK_NOTIFY = 0x13;
//	public final static int PROTOCL_STEP_GOAL_NOTIFY = 0x17;
//	public final static int PROTOCL_HEART_RATE_MAX_NOTIFY = 0x19;
//	public final static int PROTOCL_CAMERA_NOTIFY = 0x1A;
//	public final static int PROTOCL_REMIND_NOTIFY = 0x1C;
//	public final static int PROTOCL_ELECTRIC_NOTIFY = 0x21;
//	public final static int PROTOCL_STEP_NOTIFY = 0x23;
//	public final static int PROTOCL_HEART_RATE_NOTIFY = 0x25;
	
	
	
	
}
