package com.walmart.ticketing.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.walmart.ticketing.TicketingServiceApplicationTests;
import com.walmart.ticketing.domain.SeatHold;
import com.walmart.ticketing.domain.Venue;

public class VenueRepositoryTest extends TicketingServiceApplicationTests  {
	
	@Autowired
	VenueRepository venueRepository;
	
	public void testRepo(){
		Venue venu = venueRepository.findAll().get(0);
		Assert.isTrue(venu != null);
		SeatHold hold = new SeatHold();
		hold.setDateCreated(new Date());
		hold.setVenue(venu);
		hold.setConfirmationCode("john_doe@gmail.com");
		venu = venueRepository.save(venu);
		venueRepository.delete(venu);
		Optional<Venue> venuOp = venueRepository.findOne(venu.getId());
		Assert.isTrue(!venuOp.isPresent());
	}

}
