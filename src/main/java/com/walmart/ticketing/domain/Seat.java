package com.walmart.ticketing.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Seat extends BaseEntity{
	
	String row;
	Integer column;
	
	@JsonIgnore
	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	private SeatHold hold;
	
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public Integer getColumn() {
		return column;
	}
	public void setColumn(Integer column) {
		this.column = column;
	}
	public SeatHold getHold() {
		return hold;
	}
	public void setHold(SeatHold hold) {
		this.hold = hold;
	}
}
