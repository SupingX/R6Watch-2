package com.mycj.healthy.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;

public class MessageUtil {
	/**
	 * 获取本地未接电话数量
	 * 
	 * @return
	 */
	public static  int readMissCall(Context context) {
		int result = 0;
		Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[] { Calls.TYPE }, " type=? and new=?", new String[] { Calls.MISSED_TYPE + "", "1" }, "date desc");

		if (cursor != null) {
			result = cursor.getCount();
			cursor.close();
		}
		return result;
	}
	
	/**
	 * 获取未读彩信数量：
	 * 
	 * @return
	 */
	public static int getNewMmsCount(Context context) {
		int result = 0;
		Cursor csr = context.getContentResolver().query(Uri.parse("content://mms/inbox"), null, "read = 0", null, null);
		if (csr != null) {
			result = csr.getCount();
			csr.close();
		}
		return result;
	}
	/**
	 * 得到未读短信数量：
	 * 
	 * @return
	 */
	public static int getNewSmsCount(Context context) {
		int result = 0;
		Cursor csr = context.getContentResolver().query(Uri.parse("content://sms"), null, "type = 1 and read = 0", null, null);
		if (csr != null) {
			result = csr.getCount();
			csr.close();
		}
		return result;
	}
}
