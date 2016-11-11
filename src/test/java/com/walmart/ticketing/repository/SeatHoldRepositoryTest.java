package com.walmart.ticketing.repository;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.walmart.ticketing.TicketingServiceApplicationTests;
import com.walmart.ticketing.domain.SeatHold;
import com.walmart.ticketing.domain.Venue;

public class SeatHoldRepositoryTest extends TicketingServiceApplicationTests {
	
	
	@Autowired
	SeatHoldRepository seatHoldRepository;
	
	@Autowired
	VenueRepository venueRepository;
	
	@Test
	public void testVenueRepo(){
		Venue venu = venueRepository.findAll().get(0);
		SeatHold hold = new SeatHold();
		hold.setDateCreated(new Date());
		hold.setVenue(venu);
		hold.setCustomerEmail("john_doe@gmail.com");
		hold = seatHoldRepository.save(hold);
		Optional<SeatHold> holdOp = seatHoldRepository.findByIdAndCustomerEmail(hold.getId(), "john_doe@gmail.com");
		Assert.isTrue(holdOp.isPresent());
		seatHoldRepository.delete(holdOp.get());
		holdOp = seatHoldRepository.findByIdAndCustomerEmail(hold.getId(), "john_doe@gmail.com");
		Assert.isTrue(!holdOp.isPresent());
	}

}
