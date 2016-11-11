package com.walmart.ticketing.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.walmart.ticketing.TicketingServiceApplicationTests;
import com.walmart.ticketing.domain.SeatHold;
import com.walmart.ticketing.exception.TicketingServiceErrorCode;
import com.walmart.ticketing.exception.TicketingServiceException;


public class TicketServiceTest extends TicketingServiceApplicationTests {


	@Autowired
	TicketService ticketService;

	@Test
	public void numSeatsAvailable(){
		int avilableSeats = ticketService.numSeatsAvailable();
		Assert.isTrue(avilableSeats >=0, "Cannot get right number of unavailable seats");
	}

	@Test
	public void findAndHoldSeats(){
		SeatHold hold = ticketService.findAndHoldSeats(32, "john.doe1@gmail.com");
		Assert.isTrue(hold.getSeats().size() == 32,"cannot hold the right number of seats");
		Assert.isTrue(hold.getId() != null, "hold count not be saved");
		try{
			ticketService.findAndHoldSeats(800, "john.doe@gmail.com");
		}
		catch(TicketingServiceException e){
			Assert.isTrue(e.getErrorCode() == TicketingServiceErrorCode.E_SeatsNumberNotAvaiable, "find seats exceeding the limit");
		}
		try{
			ticketService.findAndHoldSeats(-5, "john.doe@gmail.com");
		}
		catch(TicketingServiceException e){
			Assert.isTrue(e.getErrorCode() == TicketingServiceErrorCode.E_InvalidNumSeat, "find seats exceeding the limit");
		}

		try{
			ticketService.findAndHoldSeats(7, "");
		}
		catch(TicketingServiceException e){
			Assert.isTrue(e.getErrorCode() == TicketingServiceErrorCode.E_InvalidEmailAddress, "find seats exceeding the limit");
		}
	}

	@Test
	public void reserveSeats(){
		SeatHold hold = ticketService.findAndHoldSeats(32, "john.doe2@gmail.com"); 
		String confirmationId = ticketService.reserveSeats(hold.getId(), "john.doe2@gmail.com");
		Assert.isTrue(!StringUtils.isEmpty(confirmationId));
		try{
			ticketService.reserveSeats(500, "test.test@gmail.com");
		}
		catch(TicketingServiceException e){
			Assert.isTrue(e.getErrorCode() == TicketingServiceErrorCode.E_HoldNotFound, "found a random error");
		}

		try{
			ticketService.reserveSeats(5, "");
		}
		catch(TicketingServiceException e){
			Assert.isTrue(e.getErrorCode() == TicketingServiceErrorCode.E_InvalidEmailAddress, "This email shouldn't be valid");
		}
	}

}
