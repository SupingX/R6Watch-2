package com.mycj.healthy.entity;

import java.util.Date;

public class SleepData {
	private int id;
	private Date date;
	private float hours;

	public SleepData() {
		super();
	}

	public SleepData(Date date, float hours) {
		super();
		this.date = date;
		this.hours = hours;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getHours() {
		return hours;
	}

	public void setHours(float hours) {
		this.hours = hours;
	}
}
