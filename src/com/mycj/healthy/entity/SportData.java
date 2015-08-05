package com.mycj.healthy.entity;

import java.util.Date;

public class SportData {
	private int id;
	private Date date;
	private int step;

	public SportData() {
		super();
	}

	public SportData(Date date, int step) {
		super();
		this.date = date;
		this.step = step;
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

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	
}
