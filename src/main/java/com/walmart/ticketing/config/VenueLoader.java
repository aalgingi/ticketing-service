package com.walmart.ticketing.config;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.walmart.ticketing.domain.SeatHold;
import com.walmart.ticketing.domain.Venue;
import com.walmart.ticketing.repository.VenueRepository;

@Component
public class VenueLoader implements ApplicationListener<ContextRefreshedEvent> {
	
	@Autowired
	VenueRepository venueRepository;

	
	/**
	* Loads in a venue with capacity 297 on application start
	*
	*/
	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		Venue venue = new Venue();
		venue.setCapacity(297);
		venue = venueRepository.save(venue);
		venue.setSeatHolds(new ArrayList<SeatHold>());
		venueRepository.save(venue);
		
	}

}
