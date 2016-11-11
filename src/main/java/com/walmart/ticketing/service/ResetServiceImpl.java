package com.walmart.ticketing.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.ticketing.domain.Venue;
import com.walmart.ticketing.repository.VenueRepository;

@Service
@Transactional
public class ResetServiceImpl implements ResetService{
	
	
	@Autowired
	VenueRepository venueRepository;

	@Override
	public void resetVenue() {
		Venue venue = venueRepository.findAll().get(0);
		venue.getSeatHolds().clear();
		venueRepository.save(venue);	
	}

}
