package com.walmart.ticketing.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class SeatHold extends BaseEntity {
	
	
	Boolean reservationConfirmedFlag;
	String confirmationCode;
	String customerEmail;
	Date dateCreated;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval= true,  mappedBy="hold")
	@LazyCollection(LazyCollectionOption.FALSE)
	List<Seat> seats;
	@JsonIgnore
	@ManyToOne(optional = false, fetch=FetchType.LAZY)
	private Venue venue;
	
	public Boolean getReservationConfirmedFlag() {
		return reservationConfirmedFlag;
	}
	public void setReservationConfirmedFlag(Boolean reservationConfirmedFlag) {
		this.reservationConfirmedFlag = reservationConfirmedFlag;
	}
	public String getConfirmationCode() {
		return confirmationCode;
	}
	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public List<Seat> getSeats() {
		return seats;
	}
	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Venue getVenue() {
		return venue;
	}
	public void setVenue(Venue venue) {
		this.venue = venue;
	}	
}
