package com.mycj.healthy.entity;

import java.util.Date;

import org.litepal.crud.DataSupport;

public class HistoryData extends DataSupport{
	private int id;
	private String historyDate;
	private int sleep;
	private int middle;
	private int rest;
	private int step;
	
	public HistoryData() {
		super();
	}

	public HistoryData(String historyDate, int sleep, int middle, int rest, int step) {
		super();
		this.historyDate = historyDate;
		this.sleep = sleep;
		this.middle = middle;
		this.rest = rest;
		this.step = step;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHistoryDate() {
		return historyDate;
	}

	public void setHistoryDate(String historyDate) {
		this.historyDate = historyDate;
	}

	public int getSleep() {
		return sleep;
	}

	public void setSleep(int sleep) {
		this.sleep = sleep;
	}

	public int getMiddle() {
		return middle;
	}

	public void setMiddle(int middle) {
		this.middle = middle;
	}

	public int getRest() {
		return rest;
	}

	public void setRest(int rest) {
		this.rest = rest;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
	@Override
	public String toString() {
		
		
		return "[ id : "+this.id
				+" , historyDate : "+this.historyDate
				+" , sleep : "+ this.sleep
				+" , middle "+ this.middle
				+" , rest " + this.rest
				+" , step " + this.step;
	}
	
}
