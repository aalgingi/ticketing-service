package com.walmart.ticketing.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Table
public class Venue extends BaseEntity {
	
	Integer capacity ;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval= true,  mappedBy="venue")
	@LazyCollection(LazyCollectionOption.FALSE)
	List<SeatHold> seatHolds;
	
	public List<SeatHold> getSeatHolds() {
		return seatHolds;
	}
	public void setSeatHolds(List<SeatHold> seatHolds) {
		this.seatHolds = seatHolds;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	
	
	
}
