package com.walmart.ticketing.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.walmart.ticketing.constant.ApplicationConstants;
import com.walmart.ticketing.domain.Seat;
import com.walmart.ticketing.domain.SeatHold;
import com.walmart.ticketing.domain.Venue;
import com.walmart.ticketing.exception.TicketingServiceErrorCode;
import com.walmart.ticketing.exception.TicketingServiceException;
import com.walmart.ticketing.repository.SeatHoldRepository;
import com.walmart.ticketing.repository.VenueRepository;


@Service
@Transactional
public class TicketServiceImpl implements TicketService {

	@Autowired
	VenueRepository venueRepository;

	@Autowired
	SeatHoldRepository seatHoldRepository;

	@Override
	public int numSeatsAvailable() {
		Venue venue = venueRepository.findAll().get(0);
		int unavailableSeats = 0;
		if(!CollectionUtils.isEmpty(venue.getSeatHolds())){
			for(SeatHold hold : venue.getSeatHolds()){
				long duration  = new Date().getTime() -  hold.getDateCreated().getTime() ;
				if(!StringUtils.isEmpty(hold.getConfirmationCode()) || TimeUnit.MILLISECONDS.toMinutes(duration) < 60){
					unavailableSeats = unavailableSeats + hold.getSeats().size();
				}
			}
		}
		return venue.getCapacity() - unavailableSeats;
	}

	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		if(numSeats <= 0){
			throw new TicketingServiceException(TicketingServiceErrorCode.E_InvalidNumSeat, "numSeats must be more than 0");
		}

		if(StringUtils.isEmpty(customerEmail)){
			throw new TicketingServiceException(TicketingServiceErrorCode.E_InvalidEmailAddress, "customer email can't be empty");
		}

		Venue venue = venueRepository.findAll().get(0);
		int [][] venueMatrix = new int[9][33];
		
		if(!org.apache.commons.collections.CollectionUtils.isEmpty(venue.getSeatHolds())){
			for(SeatHold hold : venue.getSeatHolds()){
				long duration  = new Date().getTime() -  hold.getDateCreated().getTime() ;
				if(TimeUnit.MILLISECONDS.toMinutes(duration) < 60 ||
						!StringUtils.isEmpty(hold.getConfirmationCode())){
					if(!org.apache.commons.collections.CollectionUtils.isEmpty(hold.getSeats())){
						for(Seat seat : hold.getSeats()){
							venueMatrix[ApplicationConstants.rows.indexOf(seat.getRow())][seat.getColumn() - 1] = 1;
						}
					}
				}
			}
		}
		
		int [][] venueMatrixCopy = cloneArray(venueMatrix);

		List<Seat> seats = new ArrayList<Seat>();
		boolean seatsFoundFlag = false;

		//if numseats is more the the max columns count then no need trying to get the seats next to each other
		if(numSeats <= 33)
			//First we will try to get all seats in the same rows as far away from the screen as possible
			for(int row = 0; row < 10  ; row++ ){
				boolean breakFlag = false;
				//Starting in the middle and going outwards
				int newRow = row%2 == 0 ? 5 - row/2 -1 : 5 + row/2;
				if(newRow > 8){
					continue;
				}
				for(int column = 0 ; column < venueMatrix[0].length + 1 ; column++ ){
					//Starting in the middle and going outwards
					int newColumn = column%2 == 0 ? 17 - column/2 -1 : 17 + column/2;
					if(newColumn >32){
						continue;
					}
					//No need to do try a seat next to an already row flood filled one
					if(venueMatrix[newRow][newColumn] == 0){
						apply(venueMatrix, 0, 1, newRow, newColumn, true, numSeats, seats);
						//We have found the best seats
						if(seats.size() == numSeats){
							breakFlag = true;
							seatsFoundFlag = true;
							break;
						}	
					}
					if(breakFlag){
						break;
					}

					//Did not find seats next to each other, lets reset and try the next row
					seats = new ArrayList<Seat>();
				}
			}


		//We can't find seats in the same row so lets try to do row/column flood fill starting far away from the stage
		if(!seatsFoundFlag){
			seats = new ArrayList<Seat>();
			for(int row = 0; row < 10  ; row++ ){
				int newRow = row%2 == 0 ? 5 - row/2 -1 : 5 + row/2;
				if(newRow > 8){
					continue;
				}
				boolean breakFlag = false;
				for(int column = 0 ; column < venueMatrixCopy[0].length + 1 ; column++ ){
					int newColumn = column%2 == 0 ? 17 - column/2 -1 : 17 + column/2;
					if(newColumn >32){
						continue;
					}
					if(venueMatrixCopy[newRow][newColumn] == 0){
						apply(venueMatrixCopy, 0, 1, newRow, newColumn, false, numSeats, seats);
						//We have found the best seats
						if(seats.size() == numSeats){
							breakFlag = true;
							seatsFoundFlag = true;
							break;
						}	
					}
					if(breakFlag){
						break;
					}
				}
			}
		}

		if(!seatsFoundFlag){
			throw new TicketingServiceException(TicketingServiceErrorCode.E_SeatsNumberNotAvaiable, "this number of seats in not available");
		}



		SeatHold hold = new SeatHold();
		hold.setCustomerEmail(customerEmail);
		hold.setDateCreated(new Date());
		hold.setSeats(new ArrayList<Seat>());
		for(Seat seat: seats){
			seat.setHold(hold);
		}		
		hold.getSeats().addAll(seats);
		hold.setVenue(venue);
		if(venue.getSeatHolds() == null){
			venue.setSeatHolds(new ArrayList<SeatHold>());
		}
		venue.getSeatHolds().add(hold);
		hold = seatHoldRepository.save(hold);
		return hold;
	}

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {

		if(StringUtils.isEmpty(customerEmail)){
			throw new TicketingServiceException(TicketingServiceErrorCode.E_InvalidEmailAddress,
					"invalid email address");
		}

		Optional<SeatHold> hold = seatHoldRepository.findByIdAndCustomerEmail(seatHoldId, customerEmail);
		if(!hold.isPresent()){
			throw new TicketingServiceException(TicketingServiceErrorCode.E_HoldNotFound,
					"Cannot find a hold using the supplied id and email");
		}
		SeatHold holdObj = hold.get();
		long duration  = new Date().getTime() - holdObj.getDateCreated().getTime() ;
		if(TimeUnit.MILLISECONDS.toMinutes(duration) >= 60 ){
			throw new TicketingServiceException(TicketingServiceErrorCode.E_HoldExpired,
					"Hold expired since its been over 60 minutes");
		}
		holdObj.setReservationConfirmedFlag(true);
		holdObj.setConfirmationCode(UUID.randomUUID().toString());
		holdObj = seatHoldRepository.save(holdObj);	
		return holdObj.getConfirmationCode();
	}


	
	/**
	 * Perform a custom flood-fill algorithm to check the avialable seats next to each other
	 * 
	 * @param venue matrix for the venue seats
	 * @param colorToReplace the empty seats identifier which is zero
	 * @param colorToPaint int should be used for the marked seat
	 * @param row to flood fill with
	 * @param column to start the flood fill with
	 * @param sameRow a flag to only look for empty seats in the same row in case set to true
	 * @param numSeats total number of seats the flood fill should be looking for
	 * @param seats a list of all added seats to the requested hold
	 * 
	 */
	private void apply(int[][] venue, int colorToReplace, int colorToPaint, int row, int column, boolean sameRow, int numSeats, List<Seat> seats) {
		if(seats.size() >= numSeats){
			return;
		}

		int currentColor = getValueAt(venue, row, column);
		if (currentColor == colorToReplace) {
			Seat seat = new Seat();
			seat.setColumn(column + 1);
			seat.setRow(String.valueOf(ApplicationConstants.rows.charAt(row)));
			seats.add(seat);
			venue[row][column] = colorToPaint;
			apply(venue, colorToReplace, colorToPaint, row, column + 1, sameRow, numSeats, seats);
			apply(venue, colorToReplace, colorToPaint, row, column - 1, sameRow, numSeats, seats);
			if(!sameRow){
				apply(venue, colorToReplace, colorToPaint, row + 1, column, sameRow, numSeats, seats);
				apply(venue, colorToReplace, colorToPaint, row - 1, column, sameRow, numSeats, seats);
			}
		}
	}

	/**
	 * Gets the value at a specific row/column
	 * 
	 * @param venueMatrix matrix for the venue seats
	 * @param row to check
	 * @param column to check
	 * 
	 * @return the int value at the provided row/column
	 */
	private static int getValueAt(int[][] venueMatrix, int row, int column) {
		if (row < 0 ||column < 0 || row > venueMatrix.length - 1 || column > venueMatrix[row].length -1) {
			return -1;
		} else {
			return venueMatrix[row][column];
		}
	}
	
	
	/**
	 * Clones the provided array
	 * 
	 * @param src
	 * @return a new clone of the provided array
	 */
	public static int[][] cloneArray(int[][] src) {
	    int length = src.length;
	    int[][] target = new int[length][src[0].length];
	    for (int i = 0; i < length; i++) {
	        System.arraycopy(src[i], 0, target[i], 0, src[i].length);
	    }
	    return target;
	}

}
