package com.mycj.healthy.util;

public abstract class Protocl {

	public byte[] toByte() {
		StringBuffer sb = new StringBuffer();
		appending(sb);
		return DataUtil.toBytesByString(sb.toString());

	}

	public abstract void appending(StringBuffer sb);

}
