package com.mycj.healthy.entity;


import org.litepal.crud.DataSupport;

import android.os.Parcel;
import android.os.Parcelable;

public class HeartRateData extends DataSupport implements Parcelable {
	private int id;
	private String date;
	private int heartRate;

	public HeartRateData() {
		super();
	}

	public HeartRateData(String date, int heartRate) {
		super();
		this.date = date;
		this.heartRate = heartRate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(date);
		out.writeInt(heartRate);
	}

	public HeartRateData(Parcel in) {
		id = in.readInt();
		date = in.readString();
		heartRate = in.readInt();
	}

	public static final Parcelable.Creator<HeartRateData> CREATOR = new Creator<HeartRateData>() {
		@Override
		public HeartRateData[] newArray(int size) {
			return new HeartRateData[size];
		}

		@Override
		public HeartRateData createFromParcel(Parcel in) {
			return new HeartRateData(in);
		}
	};

}
