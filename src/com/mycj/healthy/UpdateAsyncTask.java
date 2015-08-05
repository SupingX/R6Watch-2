package com.mycj.healthy;

import java.util.HashMap;

import com.mycj.healthy.util.ProtoclData;

import android.os.AsyncTask;

public class UpdateAsyncTask extends AsyncTask<byte[], Void, byte[]> {

	public UpdateAsyncTask() {
		super();

	}

	@Override
	protected byte[] doInBackground(byte[]... params) {
		HashMap<String, Integer> sleepData = ProtoclData.parserDataForSleep(params[0]);

		return null;
	}

	@Override
	protected void onPostExecute(byte[] result) {
		super.onPostExecute(result);
	}
}
